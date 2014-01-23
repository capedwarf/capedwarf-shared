/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.shared.endpoints;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.Key;
import org.jboss.capedwarf.shared.components.ShutdownHook;
import org.jboss.capedwarf.shared.components.SimpleKey;
import org.jboss.capedwarf.shared.servlet.CapedwarfApiProxy;
import org.jboss.capedwarf.shared.util.Utils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Converters implements ShutdownHook {
    private static interface Converter {
        Object convert(Object value);
    }

    private static abstract class AbstractConverter implements Converter {
        private final CtClassWrapper transformerClass;
        private final String methodName;

        private Object transformer;
        private Method method;

        protected AbstractConverter(CtClassWrapper transformerClass, String methodName) {
            this.transformerClass = transformerClass;
            this.methodName = methodName;
        }

        private synchronized Object getTransformer() {
            if (transformer == null) {
                try {
                    transformer = toClass(transformerClass).newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return transformer;
        }

        private synchronized Method getMethod(Object transformer) {
            if (method == null) {
                method = getMethod(transformer.getClass());
            }
            return method;
        }

        private Method getMethod(Class<?> clazz) {
            if (clazz == null) {
                throw new IllegalArgumentException(String.format("No such method '%s' on %s", methodName, transformerClass));
            }
            for (Method m : clazz.getMethods()) {
                if (methodName.equals(m.getName()) && m.getParameterTypes().length == 1 && Modifier.isPublic(m.getModifiers()) && (Modifier.isAbstract(m.getModifiers()) == false)) {
                    return m;
                }
            }
            return getMethod(clazz.getSuperclass());
        }

        public Object convert(Object value) {
            try {
                Object transformer = getTransformer();
                return getMethod(transformer).invoke(transformer, value);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e.getCause());
            }
        }
    }

    private static class ToConverter extends AbstractConverter {
        private ToConverter(CtClassWrapper transformer) {
            super(transformer, "transformTo");
        }
    }

    private static class FromConverter extends AbstractConverter {

        private FromConverter(CtClassWrapper transformer) {
            super(transformer, "transformFrom");
        }
    }

    private static final Logger log = Logger.getLogger(Converter.class.getName());

    private ClassLoader owner;
    private ClassPool pool;

    private Set<String> endpoints;
    private Set<CtClassWrapper> results;

    private Map<CtClassWrapper, Converter> fromTo;
    private Map<CtClassWrapper, Converter> toFrom;
    private Map<CtClassWrapper, CtClassWrapper> types;

    private Converters(final ClassLoader owner) {
        this.owner = owner;
    }

    private synchronized ClassPool getPool() {
        if (pool == null) {
            pool = new ClassPool() {
                @Override
                public ClassLoader getClassLoader() {
                    return owner;
                }
            };
            pool.appendClassPath(new LoaderClassPath(owner));
        }
        return pool;
    }

    public void shutdown() {
        owner = null;
        pool = null;
    }

    public static Converters getInstance() {
        return getInstance(Utils.getAppClassLoader());
    }

    public static Converters getInstance(ClassLoader cl) {
        CapedwarfApiProxy.Info info = CapedwarfApiProxy.getInfo();
        Key<Converters> key = new SimpleKey<>(info.getAppId(), info.getModule(), Converters.class);
        ComponentRegistry registry = ComponentRegistry.getInstance();
        Converters converters = new Converters(cl);
        Converters previous = registry.putIfAbsent(key, converters);
        return (previous != null) ? previous : converters;
    }

    public static void removeInstance(String appId, String module) {
        Key<Converters> key = new SimpleKey<>(appId, module, Converters.class);
        ComponentRegistry registry = ComponentRegistry.getInstance();
        registry.removeComponent(key);
    }

    public synchronized void addEndpointClass(String className) {
        if (endpoints == null) {
            endpoints = new CopyOnWriteArraySet<>();
        }
        endpoints.add(className);
    }

    public synchronized Set<String> getEndpoints() {
        return (endpoints == null) ? Collections.<String>emptySet() : Collections.unmodifiableSet(endpoints);
    }

    public void addResultType(String className) {
        addResultType(fromClassName(className));
    }

    private synchronized void addResultType(CtClassWrapper clazz) {
        if (clazz == null) {
            return;
        }
        if (results == null) {
            results = new CopyOnWriteArraySet<>();
        }
        results.add(clazz);
        addResultType(clazz.getSuperClass());
    }

    public synchronized boolean isResultType(CtClass clazz) {
        return (results != null && results.contains(new CtClassWrapper(clazz)));
    }

    public void add(String transformer) {
        try {
            addInternal(transformer);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private synchronized void addInternal(String transformer) throws Exception {
        CtClassWrapper tClass = fromClassName(transformer);

        CtMethod transformTo = findTransformTo(transformer, tClass.getCtClass());
        CtClassWrapper from = new CtClassWrapper(transformTo.getParameterTypes()[0]);
        CtClassWrapper to = new CtClassWrapper(transformTo.getReturnType());

        if (fromTo == null) {
            fromTo = new ConcurrentHashMap<>();
        }

        if (toFrom == null) {
            toFrom = new ConcurrentHashMap<>();
        }

        if (fromTo.containsKey(from)) {
            log.warning(String.format("Converter for %s already exists!", from));
        }
        if (toFrom.containsKey(to)) {
            log.warning(String.format("Converter for %s already exists!", to));
        }

        if (types == null) {
            types = new ConcurrentHashMap<>();
        }

        types.put(from, to);

        fromTo.put(from, new ToConverter(tClass));
        toFrom.put(to, new FromConverter(tClass));
    }

    public synchronized boolean hasConverter(CtClass clazz) {
        return (types != null && types.containsKey(new CtClassWrapper(clazz)));
    }

    public Class<?> traverse(Class<?> start) {
        return traverse(fromClass(start));
    }

    private Class<?> traverse(CtClassWrapper start) {
        return toClass(traverseInternal(start));
    }

    private synchronized CtClassWrapper traverseInternal(CtClassWrapper start) {
        if (types == null) {
            return start;
        }

        CtClassWrapper mapped = types.get(start);
        if (mapped != null) {
            return traverseInternal(mapped);
        } else {
            return start;
        }
    }

    public Object transformTo(Object value) {
        return convert(fromTo, value);
    }

    public Object transformFrom(Object value) {
        return convert(toFrom, value);
    }

    private synchronized Object convert(Map<CtClassWrapper, Converter> map,  Object value) {
        if (value == null) {
            return null;
        }

        if (map == null) {
            return value;
        }

        CtClassWrapper clazz = fromObject(value);
        Converter converter = map.get(clazz); // TODO match hierarchy?
        if (converter != null) {
            Object converted = converter.convert(value);
            return convert(map, converted); // recurse -- check for loop?
        } else {
            return value;
        }
    }

    private CtClassWrapper fromObject(Object value) {
        return fromClass(value.getClass());
    }

    private CtClassWrapper fromClass(Class<?> clazz) {
        return fromClassName(clazz.getName());
    }

    private CtClassWrapper fromClassName(String clazz) {
        return new CtClassWrapper(getPool().getOrNull(clazz));
    }

    private static Class<?> toClass(CtClassWrapper ctClass) {
        try {
            return ctClass.toClass();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    protected CtMethod findTransformTo(Object info, CtClass clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot find transformTo method: " + info);
        }
        try {
            for (CtMethod m : clazz.getDeclaredMethods()) {
                if ("transformTo".equals(m.getName()) && m.getParameterTypes().length == 1 && Modifier.isPublic(m.getModifiers()) && (Modifier.isAbstract(m.getModifiers()) == false)) {
                    return m;
                }
            }
            return findTransformTo(info, clazz.getSuperclass());
        } catch (NotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
