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
import org.jboss.capedwarf.shared.util.Utils;

/**
 * Capedwarf API - tie between app and AS.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class CapedwarfApiProxy {
    private static final Map<ClassLoader, Info> classLoaders = new ConcurrentHashMap<>();
    private static final ThreadLocal<ServletRequest> requests = new ThreadLocal<>();

    public static boolean isCapedwarfApp() {
        return isCapedwarfApp(Utils.getAppClassLoader());
    }

    public static boolean isCapedwarfApp(ClassLoader classLoader) {
        return classLoaders.containsKey(classLoader);
    }

    public static Info getInfo() {
        return getInfo(Utils.getAppClassLoader());
    }

    public static Info getInfo(ClassLoader cl) {
        Info info = classLoaders.get(cl);
        if (info == null) {
            throw new IllegalStateException(String.format("No info for classloader %s.", cl));
        }
        return info;
    }

    public static ServletRequest getRequest() {
        return requests.get();
    }

    public static void initialize(final ClassLoader cl, final String appId, final String module) {
        classLoaders.put(cl, new Info(appId, module));
    }

    static void initialize(final String appId, final String module, final ServletContext context) {
    }

    static void initialize(final String appId, final String module, final EmbeddedCacheManager manager) {
        // do nothing atm
    }

    public static void destroy(final ClassLoader cl) {
        classLoaders.remove(cl);
    }

    static void destroy(final String appId, final String module, final ServletContext context) {
        // do nothing atm
    }

    static void setRequest(ServletRequest request) {
        requests.set(request);
    }

    static void removeRequest() {
        requests.remove();
    }

    public static class Info {
        private String appId;
        private String module;

        private Info(String appId, String module) {
            this.appId = appId;
            this.module = module;
        }

        public String getAppId() {
            return appId;
        }

        public String getModule() {
            return module;
        }
    }
}
