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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketOptions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.capedwarf.shared.compatibility.Compatibility;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CapedwarfSocket extends SocketImpl {
    private static final Map<String, Method> methods = new ConcurrentHashMap<>();

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private final SocketImpl delegate;

    CapedwarfSocket(SocketImpl delegate) {
        this.delegate = delegate;
    }

    protected <T> T invoke(String method) throws IOException {
        return invoke(method, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    protected <T> T invoke(String method, Class[] types, Object[] args) throws IOException {
        return invoke(method, method, types, args);
    }

    protected <T> T invoke(Class<?> clazz, String method, Class[] types, Object[] args) throws IOException {
        return invoke(clazz, method, method, types, args);
    }

    protected <T> T invoke(String key, String method, Class[] types, Object[] args) throws IOException {
        return invoke(SocketImpl.class, key, method, types, args);
    }

    protected <T> T invoke(Class<?> clazz, String key, String method, Class[] types, Object[] args) throws IOException {
        try {
            Method m = methods.get(key);
            if (m == null) {
                m = clazz.getDeclaredMethod(method, types);
                m.setAccessible(true);
                methods.put(key, m);
            }
            //noinspection unchecked
            return (T) m.invoke(delegate, args);
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

    protected void create(boolean stream) throws IOException {
        invoke("create", new Class[]{Boolean.TYPE}, new Object[]{stream});
    }

    protected void connect(String host, int port) throws IOException {
        invoke("connect1", "connect", new Class[]{String.class, Integer.TYPE}, new Object[]{host, port});
    }

    protected void connect(InetAddress address, int port) throws IOException {
        invoke("connect2", "connect", new Class[]{InetAddress.class, Integer.TYPE}, new Object[]{address, port});
    }

    protected void connect(SocketAddress address, int timeout) throws IOException {
        invoke("connect3", "connect", new Class[]{SocketAddress.class, Integer.TYPE}, new Object[]{address, timeout});
    }

    protected void bind(InetAddress host, int port) throws IOException {
        invoke("bind", new Class[]{InetAddress.class, Integer.TYPE}, new Object[]{host, port});
    }

    protected void listen(int backlog) throws IOException {
        invoke("listen", new Class[]{Integer.TYPE}, new Object[]{backlog});
    }

    protected void accept(SocketImpl s) throws IOException {
        invoke("accept", new Class[]{SocketImpl.class}, new Object[]{s});
    }

    protected InputStream getInputStream() throws IOException {
        return new CapedwarfSocketInputStream(this);
    }

    protected OutputStream getOutputStream() throws IOException {
        return new CapedwarfSocketOutputStream(this);
    }

    protected int available() throws IOException {
        return invoke("available", new Class[0], new Object[0]);
    }

    protected void close() throws IOException {
        invoke("close", new Class[0], new Object[0]);
    }

    protected void sendUrgentData(int data) throws IOException {
        invoke("sendUrgentData", new Class[]{Integer.TYPE}, new Object[]{data});
    }

    public void setOption(int optID, Object value) throws SocketException {
        final Compatibility compatibility = Compatibility.getInstance();
        if (compatibility.isEnabled(Compatibility.Feature.ENABLE_SOCKET_OPTIONS)) {
            setOptionInternal(optID, value);
        } else {
            CapedwarfSocketOptions.Option option = CapedwarfSocketOptions.getOptionById(optID);
            if (option != null) {
                option.validateAndApply(this, value);
            }
        }
    }

    protected void setOptionInternal(int optID, Object value) throws SocketException {
        try {
            invoke(SocketOptions.class, "setOption", new Class[]{Integer.TYPE, Object.class}, new Object[]{optID, value});
        } catch (IOException e) {
            if (e instanceof SocketException) {
                throw SocketException.class.cast(e);
            } else {
                throw new SocketException(e.getMessage());
            }
        }
    }

    public Object getOption(int optID) throws SocketException {
        final Compatibility compatibility = Compatibility.getInstance();
        if (compatibility.isEnabled(Compatibility.Feature.ENABLE_SOCKET_OPTIONS)) {
            return getOptionInternal(optID);
        } else {
            CapedwarfSocketOptions.Option option = CapedwarfSocketOptions.getOptionById(optID);
            return (option != null) ? option.getOption(this) : null;
        }
    }

    protected Object getOptionInternal(int optID) throws SocketException {
        try {
            return invoke(SocketOptions.class, "getOption", new Class[]{Integer.TYPE}, new Object[]{optID});
        } catch (IOException e) {
            if (e instanceof SocketException) {
                throw SocketException.class.cast(e);
            } else {
                throw new SocketException(e.getMessage());
            }
        }
    }

    @Override
    protected void shutdownInput() throws IOException {
        invoke("shutdownInput");
    }

    @Override
    protected void shutdownOutput() throws IOException {
        invoke("shutdownOutput");
    }

    @Override
    protected FileDescriptor getFileDescriptor() {
        try {
            return invoke("getFileDescriptor");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected InetAddress getInetAddress() {
        try {
            return invoke("getInetAddress");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int getPort() {
        try {
            return invoke("getPort");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean supportsUrgentData() {
        try {
            return invoke("supportsUrgentData");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int getLocalPort() {
        try {
            return invoke("getLocalPort");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        try {
            invoke("setPerformancePreferences", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[] {connectionTime, latency, bandwidth});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
