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

package org.jboss.capedwarf.shared.util;

import java.util.concurrent.Future;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;

/**
 * Simple utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class Utils {
    protected static ModuleClassLoader unwrap(final ClassLoader input) {
        ClassLoader cl = input;
        while (cl != null && (cl instanceof ModuleClassLoader == false)) {
            cl = cl.getParent();
        }
        if (cl == null) {
            throw new IllegalArgumentException("No ModuleClassLoader in hierarchy?! - " + input);
        }

        return ModuleClassLoader.class.cast(cl);
    }

    public static ClassLoader getTCCL() {
        return SecurityActions.getTCCL();
    }

    public static ClassLoader setTCCL(ClassLoader cl) {
        return SecurityActions.setTCCL(cl);
    }

    public static ModuleClassLoader getAppClassLoader() {
        // unwrap -- might be WebEnvCL
        return unwrap(getTCCL());
    }

    public static <T> T newInstance(Class<T> expectedType, String className) {
        return newInstance(expectedType, getAppClassLoader(), className);
    }

    public static <T> T newInstance(Class<T> expectedType, ClassLoader cl, String className) {
        try {
            Object instance = cl.loadClass(className).newInstance();
            return expectedType.cast(instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Module toModule(ClassLoader cl) {
        return unwrap(cl).getModule();
    }

    public static Module toModule() {
        return getAppClassLoader().getModule();
    }

    /**
     * To RuntimeException.
     *
     * @param t exception
     * @return t if t is RuntimeException already, else wrap t into RuntimeException
     */
    public static RuntimeException toRuntimeException(Throwable t) {
        return (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }

    /**
     * Check value for null, return default if true.
     *
     * @param value the value to check
     * @param defaultValue the default value
     * @return value if not null, default otherwise
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    /**
     * Quiet future get.
     * Wrap exception into runtime exception.
     *
     * @param future the future
     * @return future's get result
     */
    public static <R> R quietGet(Future<R> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw toRuntimeException(e);
        }
    }
}
