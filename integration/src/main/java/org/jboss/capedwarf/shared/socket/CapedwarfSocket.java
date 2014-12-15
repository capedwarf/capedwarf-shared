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
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CapedwarfSocket extends SocketImpl implements SocketOptionsInternal {
    private static final AbstractSocketHelper helper = new CapedwarfSocketHelper();

    private final SocketImpl delegate;

    CapedwarfSocket(SocketImpl delegate) {
        this.delegate = delegate;
    }

    Object invoke(String method) throws IOException {
        return helper.invoke(delegate, method);
    }

    protected void create(boolean stream) throws IOException {
        helper.invoke(delegate, "create", new Class[]{Boolean.TYPE}, new Object[]{stream});
    }

    protected void connect(String host, int port) throws IOException {
        helper.invoke(delegate, "connect1", "connect", new Class[]{String.class, Integer.TYPE}, new Object[]{host, port});
    }

    protected void connect(InetAddress address, int port) throws IOException {
        helper.invoke(delegate, "connect2", "connect", new Class[]{InetAddress.class, Integer.TYPE}, new Object[]{address, port});
    }

    protected void connect(SocketAddress address, int timeout) throws IOException {
        helper.invoke(delegate, "connect3", "connect", new Class[]{SocketAddress.class, Integer.TYPE}, new Object[]{address, timeout});
    }

    protected void bind(InetAddress host, int port) throws IOException {
        helper.invoke(delegate, "bind", new Class[]{InetAddress.class, Integer.TYPE}, new Object[]{host, port});
    }

    protected void listen(int backlog) throws IOException {
        helper.invoke(delegate, "listen", new Class[]{Integer.TYPE}, new Object[]{backlog});
    }

    protected void accept(SocketImpl s) throws IOException {
        helper.invoke(delegate, "accept", new Class[]{SocketImpl.class}, new Object[]{s});
    }

    protected InputStream getInputStream() throws IOException {
        return new CapedwarfSocketInputStream(this);
    }

    protected OutputStream getOutputStream() throws IOException {
        return new CapedwarfSocketOutputStream(this);
    }

    protected int available() throws IOException {
        return helper.invoke(delegate, "available", new Class[0], new Object[0]);
    }

    protected void close() throws IOException {
        helper.invoke(delegate, "close", new Class[0], new Object[0]);
    }

    protected void sendUrgentData(int data) throws IOException {
        helper.invoke(delegate, "sendUrgentData", new Class[]{Integer.TYPE}, new Object[]{data});
    }

    @Override
    protected void shutdownInput() throws IOException {
        helper.invoke(delegate, "shutdownInput");
    }

    @Override
    protected void shutdownOutput() throws IOException {
        helper.invoke(delegate, "shutdownOutput");
    }

    @Override
    protected FileDescriptor getFileDescriptor() {
        return helper.invokeQuiet(delegate, "getFileDescriptor");
    }

    @Override
    protected InetAddress getInetAddress() {
        return helper.invokeQuiet(delegate, "getInetAddress");
    }

    @Override
    protected int getPort() {
        return helper.invokeQuiet(delegate, "getPort");
    }

    @Override
    protected boolean supportsUrgentData() {
        return helper.invokeQuiet(delegate, "supportsUrgentData");
    }

    @Override
    protected int getLocalPort() {
        return helper.invokeQuiet(delegate, "getLocalPort");
    }

    @Override
    protected void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        try {
            helper.invoke(delegate, "setPerformancePreferences", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE}, new Object[]{connectionTime, latency, bandwidth});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setOption(int optID, Object value) throws SocketException {
        helper.setOption(this, optID, value);
    }

    public Object getOption(int optID) throws SocketException {
        return helper.getOption(this, optID);
    }

    public Object getDelegate() {
        return delegate;
    }

    public void setOptionInternal(int optID, Object value) throws SocketException {
        helper.setOptionInternal(this, optID, value);
    }

    public Object getOptionInternal(int optID) throws SocketException {
        return helper.getOptionInternal(this, optID);
    }
}
