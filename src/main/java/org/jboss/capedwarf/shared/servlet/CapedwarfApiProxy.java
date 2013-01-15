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

package org.jboss.capedwarf.shared.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.Key;
import org.jboss.capedwarf.shared.components.SimpleKey;
import org.jboss.capedwarf.shared.util.Utils;

/**
 * Capedwarf API - tie between app and AS.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class CapedwarfApiProxy {
    private static final Map<ClassLoader, String> classLoaders = new ConcurrentHashMap<ClassLoader, String>();
    private static final ThreadLocal<ServletRequest> requests = new ThreadLocal<ServletRequest>();

    public static boolean isCapedwarfApp(ClassLoader classLoader) {
        return classLoaders.containsKey(classLoader);
    }

    public static boolean isCapedwarfApp() {
        return isCapedwarfApp(Utils.getAppClassLoader());
    }

    public static String getAppId() {
        return classLoaders.get(Utils.getAppClassLoader());
    }

    public static ServletRequest getRequest() {
        return requests.get();
    }

    static void initialize(final String appId, final ServletContext context) {
        Key<ServletContext> key = new SimpleKey<ServletContext>(ServletContext.class, appId);
        ComponentRegistry.getInstance().setComponent(key, context);
        classLoaders.put(Utils.getAppClassLoader(), appId);
    }

    static void initialize(final String appId, final EmbeddedCacheManager manager) {
        // do nothing atm
    }

    static void destroy(final String appId, final ServletContext context) {
        classLoaders.remove(Utils.getAppClassLoader());
    }

    static void setRequest(ServletRequest request) {
        requests.set(request);
    }

    static void removeRequest() {
        requests.remove();
    }
}
