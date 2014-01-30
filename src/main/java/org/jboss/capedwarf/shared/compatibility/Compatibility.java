/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.shared.compatibility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.jboss.capedwarf.shared.components.AppIdFactory;
import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.Key;
import org.jboss.capedwarf.shared.components.Keys;
import org.jboss.capedwarf.shared.components.SimpleKey;
import org.jboss.capedwarf.shared.util.CountingMapThreadLocal;

/**
 * Allow for custom extensions to GAE API, impl, behavior, etc.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class Compatibility {
    public static final String FILENAME = "capedwarf-compatibility.properties";

    public static enum Feature {
        ENABLE_ALL("enable.all"),
        DISABLE_ENTITY_GROUPS("disable.entity.groups"),
        DISABLE_QUERY_INEQUALITY_FILTER_CHECK("disable.query.inequality.filter.check"),
        IGNORE_ENTITY_PROPERTY_CONVERSION("ignore.entity.property.conversion"),
        IGNORE_LOGGING("ignore.logging"),
        ASYNC_LOGGING("async.logging"),
        ENABLE_EAGER_DATASTORE_STATS("enable.eager.datastore.stats", new RegexpValue("(sync|async)")),
        ENABLE_GLOBAL_TIME_LIMIT("enable.globalTimeLimit"),
        DISABLE_BLACK_LIST("disable.blacklist"),
        DISABLE_METADATA("disable.metadata"),
        ENABLED_SUBSYSTEMS("enabled.subsystems", NotEmpty.INSTANCE),
        DISABLED_SUBSYSTEMS("disabled.subsystems", NotEmpty.INSTANCE),
        FORCE_ASYNC_DATASTORE("force.async.datastore"),
        LOG_TO_FILE("log.to.file", NotEmpty.INSTANCE),  // TODO -- better Value; e.g. FileName
        ENABLE_SOCKET_OPTIONS("enable.socket.options"),
        IGNORE_CAPEDWARF_SOCKETS("ignore.capedwarf.sockets"),
        IGNORE_CAPEDWARF_URL_STREAM_HANDLER("ignore.capedwarf.url.stream.handler"),
        CHANNEL_DEFAULT_DURATION_MINUTES("channel.default.duration.minutes", new IntegerValue(2 * 60)),
        DEFAULT_GCS_BUCKET_NAME("default.gcs.bucket.name", NotEmpty.INSTANCE),
        DISABLE_WEB_SOCKETS_CHANNEL("disable.websockets.channel"),
        TASKQUEUE_ROLES("taskqueue.roles", new FallbackValue("admin"));

        private String key;
        private Value value;

        private Feature(String key) {
            this(key, Boolean.TRUE.toString());
        }

        private Feature(String key, String value) {
            this(key, new DefaultValue(value));
        }

        private Feature(String key, Value value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    private static final CountingMapThreadLocal<Feature> temps = new CountingMapThreadLocal<>();

    private final Properties properties;
    private final Map<Feature, Boolean> values = new ConcurrentHashMap<>();

    private Compatibility(Properties properties) {
        this.properties = properties;
    }

    /**
     * Get instance per key.
     *
     * @return compatibility instance
     */
    public static Compatibility getInstance() {
        return getInstance(SimpleKey.withClassloader(Compatibility.class));
    }

    /**
     * Get instance per key.
     *
     * @param key the key
     * @return compatibility instance
     */
    public static Compatibility getInstance(Key<Compatibility> key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key!");
        }

        ComponentRegistry registry = ComponentRegistry.getInstance();
        Compatibility compatibility = registry.getComponent(key);
        if (compatibility == null) {
            throw new IllegalStateException(String.format("No Compatibility found, key = [%s], env = [%s], dump:%s", key, AppIdFactory.getAppId() + "," + AppIdFactory.getModule(), registry.dump(key.getAppId())));
        }
        return compatibility;
    }

    /**
     * Read Compatibility, not cached!
     *
     * @param cl the classloader
     * @return compatibility
     */
    public static Compatibility readCompatibility(ClassLoader cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Null classloader!");
        }

        return readCompatibility(cl.getResourceAsStream(FILENAME));
    }

    /**
     * Read Compatibility, not cached!
     *
     * @param is the input stream
     * @return compatibility
     */
    public static Compatibility readCompatibility(InputStream is) {
        final Properties properties = new Properties();
        properties.putAll(System.getProperties());
        properties.putAll(ComponentRegistry.getInstance().getComponent(Keys.CONFIGURATION));

        try {
            if (is != null) {
                try {
                    properties.load(is);
                } finally {
                    is.close();
                }
            }
            return new Compatibility(properties);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading Compatibility.", e);
        }
    }

    public boolean isEnabled(Feature feature) {
        return temps.hasValue(feature) || isEnabledInternal(Feature.ENABLE_ALL) || isEnabledInternal(feature);
    }

    public String getValue(Feature feature) {
        return properties.getProperty(feature.key);
    }

    public Object toObject(Feature feature) {
        final String string = getValue(feature);
        return feature.value.transform(string);
    }

    protected boolean isEnabledInternal(Feature feature) {
        Boolean result = values.get(feature);
        if (result == null) {
            final String value = properties.getProperty(feature.key);
            result = (value != null && feature.value.match(value));
            values.put(feature, result);
        }
        return result;
    }

    /**
     * Temp enable feature.
     *
     * @param feature the feature
     */
    public static void enable(Feature feature) {
        temps.add(feature);
    }

    /**
     * Disable feature.
     *
     * @param feature the feature
     */
    public static void disable(Feature feature) {
        temps.remove(feature);
    }

    private static interface Value {
        boolean match(String value);
        Object transform(String value);
    }

    private static abstract class AbstractValue implements Value {
        public Object transform(String value) {
            return value;
        }
    }

    private static class DefaultValue extends AbstractValue {
        private String value;

        private DefaultValue(String value) {
            this.value = value;
        }

        public boolean match(String v) {
            return value.equalsIgnoreCase(v);
        }

        public String toString() {
            return value;
        }
    }

    private static class RegexpValue extends AbstractValue {
        private Pattern pattern;

        private RegexpValue(String value) {
            this.pattern = Pattern.compile(value);
        }

        public boolean match(String v) {
            return pattern.matcher(v).matches();
        }

        public String toString() {
            return pattern.toString();
        }
    }

    private static class NotEmpty extends AbstractValue {
        private static final Value INSTANCE = new NotEmpty();

        public boolean match(String value) {
            return (value != null && value.length() > 0);
        }
    }

    private static class IntegerValue extends AbstractValue {
        private int defaultValue;

        private IntegerValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean match(String value) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (Exception e) {
                return  false;
            }
        }

        public Object transform(String string) {
            return (string != null) ? Integer.parseInt(string) : defaultValue;
        }

        public String toString() {
            return String.valueOf(defaultValue);
        }
    }

    private static class FallbackValue extends NotEmpty {
        private String defaultValue;

        private FallbackValue(String value) {
            defaultValue = value;
        }

        public Object transform(String value) {
            return (value != null) ? value : defaultValue;
        }
    }
}
