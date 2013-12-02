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

package org.jboss.capedwarf.shared.bytecode;

import java.lang.Class;import java.lang.ClassLoader;import java.lang.Exception;import java.lang.IllegalArgumentException;import java.lang.Object;import java.lang.Override;import java.lang.RuntimeException;import java.lang.SecurityManager;import java.lang.System;import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * Bytecode hacks.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class BytecodeUtils {
    private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
        public boolean isHandled(Method m) {
            // skip finalize methods
            return !("finalize".equals(m.getName()) && m.getParameterTypes().length == 0);
        }
    };

    public static <T> T proxy(Class<T> expected, MethodHandler handler) {
        return proxy(expected, handler, null, null);
    }

    public static <T> T proxy(Class<T> expected, MethodHandler handler, Class<?>[] paramTypes, Object[] args) {
        Class<?>[] interfaces = null;
        Class<?> superClass = null;
        if (expected.isInterface()) {
            interfaces = new Class[]{expected};
        } else {
            superClass = expected;
        }
        return proxy(expected, interfaces, superClass, handler, paramTypes, args);
    }

    public static <T> T proxy(Class<T> expected, Class<?>[] interfaces, Class<?> superClass, MethodHandler handler, Class<?>[] paramTypes, Object[] args) {
        if (expected == null)
            throw new IllegalArgumentException("Null expected class!");
        if (handler == null)
            throw new IllegalArgumentException("Null method handler!");

        final ProxyFactory factory = new InternalProxyFactory(expected.getClassLoader());
        factory.setFilter(BytecodeUtils.FINALIZE_FILTER);
        if (interfaces != null && interfaces.length > 0) {
            factory.setInterfaces(interfaces);
        }
        if (superClass != null) {
            factory.setSuperclass(superClass);
        }

        final Class<?> proxyClass = getProxyClass(factory);
        try {
            Proxy proxy;
            if (paramTypes == null || paramTypes.length == 0) {
                proxy = (Proxy) proxyClass.newInstance();
            } else {
                Constructor<?> ctor = proxyClass.getConstructor(paramTypes);
                proxy = (Proxy) ctor.newInstance(args);
            }
            proxy.setHandler(handler);
            return expected.cast(proxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Class<?> getProxyClass(ProxyFactory factory) {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null)
            return factory.createClass();
        else
            return AccessController.doPrivileged(new ClassCreator(factory));
    }

    /**
     * Privileged class creator.
     */
    protected static class ClassCreator implements PrivilegedAction<Class<?>> {
        private ProxyFactory factory;

        public ClassCreator(ProxyFactory factory) {
            this.factory = factory;
        }

        public Class<?> run() {
            return factory.createClass();
        }
    }

    private static class InternalProxyFactory extends ProxyFactory {
        private final ClassLoader classLoader;

        private InternalProxyFactory(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        protected ClassLoader getClassLoader() {
            return classLoader;
        }
    }
}
