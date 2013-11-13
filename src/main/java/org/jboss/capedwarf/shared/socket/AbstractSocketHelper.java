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

package org.jboss.capedwarf.shared.socket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketOptions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.capedwarf.shared.compatibility.Compatibility;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
abstract class AbstractSocketHelper {
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private final Map<String, Method> methods = new ConcurrentHashMap<>();

    protected <T> T invokeQuiet(Object target, String method) {
        try {
            return invoke(target, method);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected <T> T invokeCheck(Object target, String method) throws SocketException {
        try {
            return invoke(target, method);
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            throw new SocketException(e.getMessage());
        }
    }

    protected <T> T invoke(Object target, String method) throws IOException {
        return invoke(target, method, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    protected <T> T invoke(Object target, String method, Class[] types, Object[] args) throws IOException {
        return invoke(target, method, method, types, args);
    }

    protected <T> T invokeCheck(Object target, String method, Class[] types, Object[] args) throws SocketException {
        try {
            return invoke(target, method, types, args);
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            throw new SocketException(e.getMessage());
        }
    }

    protected <T> T invoke(Object target, Class<?> clazz, String method, Class[] types, Object[] args) throws IOException {
        return invoke(target, clazz, method, method, types, args);
    }

    protected abstract <T> T invoke(Object target, String key, String method, Class[] types, Object[] args) throws IOException;

    protected <T> T invoke(Object target, Class<?> clazz, String key, String method, Class[] types, Object[] args) throws IOException {
        try {
            Method m = methods.get(key);
            if (m == null) {
                m = clazz.getDeclaredMethod(method, types);
                m.setAccessible(true);
                methods.put(key, m);
            }
            //noinspection unchecked
            return (T) m.invoke(target, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof IOException) {
                throw IOException.class.cast(t);
            } else {
                throw new IllegalStateException(t);
            }
        }
    }

    // --- SocketOptions

    void setOption(SocketOptionsInternal socketImpl, int optID, Object value) throws SocketException {
        final Compatibility compatibility = Compatibility.getInstance();
        if (compatibility.isEnabled(Compatibility.Feature.ENABLE_SOCKET_OPTIONS)) {
            setOptionInternal(socketImpl, optID, value);
        } else {
            CapedwarfSocketOptions.Option option = CapedwarfSocketOptions.getOptionById(optID);
            if (option != null) {
                option.validateAndApply(socketImpl, value);
            }
        }
    }

    void setOptionInternal(SocketOptionsInternal socketImpl, int optID, Object value) throws SocketException {
        try {
            invoke(socketImpl.getDelegate(), SocketOptions.class, "setOption", new Class[]{Integer.TYPE, Object.class}, new Object[]{optID, value});
        } catch (IOException e) {
            if (e instanceof SocketException) {
                throw SocketException.class.cast(e);
            } else {
                throw new SocketException(e.getMessage());
            }
        }
    }

    Object getOption(SocketOptionsInternal socketImpl, int optID) throws SocketException {
        final Compatibility compatibility = Compatibility.getInstance();
        if (compatibility.isEnabled(Compatibility.Feature.ENABLE_SOCKET_OPTIONS)) {
            return getOptionInternal(socketImpl, optID);
        } else {
            CapedwarfSocketOptions.Option option = CapedwarfSocketOptions.getOptionById(optID);
            return (option != null) ? option.getOption(socketImpl) : null;
        }
    }

    Object getOptionInternal(SocketOptionsInternal socketImpl, int optID) throws SocketException {
        try {
            return invoke(socketImpl.getDelegate(), SocketOptions.class, "getOption", new Class[]{Integer.TYPE}, new Object[]{optID});
        } catch (IOException e) {
            if (e instanceof SocketException) {
                throw SocketException.class.cast(e);
            } else {
                throw new SocketException(e.getMessage());
            }
        }
    }
}
