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
import java.net.DatagramPacket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CapedwarfDatagramSocket extends DatagramSocketImpl implements SocketOptionsInternal {
    private final static AbstractSocketHelper helper = new CapedwarfDatagramSocketHelper();

    private final DatagramSocketImpl delegate;

    CapedwarfDatagramSocket(DatagramSocketImpl delegate) {
        this.delegate = delegate;
    }

    protected void connect(String host, int port) throws IOException {
        helper.invoke(delegate, "connect", new Class[]{String.class, Integer.TYPE}, new Object[]{host, port});
    }

    protected void disconnect() {
        helper.invokeQuiet(delegate, "disconnect");
    }

    protected int getLocalPort() {
        return helper.invokeQuiet(delegate, "getLocalPort");
    }

    protected FileDescriptor getFileDescriptor() {
        return helper.invokeQuiet(delegate, "getFileDescriptor");
    }

    // ---

    protected void create() throws SocketException {
        helper.invokeCheck(delegate, "create");
    }

    protected void bind(int lport, InetAddress laddr) throws SocketException {
        helper.invokeCheck(delegate, "bind", new Class[]{int.class, InetAddress.class}, new Object[]{lport, laddr});
    }

    protected void send(DatagramPacket p) throws IOException {
        helper.invoke(delegate, "send", new Class[]{DatagramPacket.class}, new Object[]{p});
    }

    protected int peek(InetAddress i) throws IOException {
        return helper.invoke(delegate, "peek", new Class[]{InetAddress.class}, new Object[]{i});
    }

    protected int peekData(DatagramPacket p) throws IOException {
        return helper.invoke(delegate, "peekData", new Class[]{DatagramPacket.class}, new Object[]{p});
    }

    protected void receive(DatagramPacket p) throws IOException {
        helper.invoke(delegate, "receive", new Class[]{DatagramPacket.class}, new Object[]{p});
    }

    protected void setTTL(byte ttl) throws IOException {
        helper.invoke(delegate, "setTTL", new Class[]{byte.class}, new Object[]{ttl});
    }

    protected byte getTTL() throws IOException {
        return helper.invoke(delegate, "getTTL");
    }

    protected void setTimeToLive(int ttl) throws IOException {
        helper.invoke(delegate, "setTimeToLive", new Class[]{int.class}, new Object[]{ttl});
    }

    protected int getTimeToLive() throws IOException {
        return helper.invoke(delegate, "getTimeToLive");
    }

    protected void join(InetAddress inetaddr) throws IOException {
        helper.invoke(delegate, "join", new Class[]{InetAddress.class}, new Object[]{inetaddr});
    }

    protected void leave(InetAddress inetaddr) throws IOException {
        helper.invoke(delegate, "leave", new Class[]{InetAddress.class}, new Object[]{inetaddr});
    }

    protected void joinGroup(SocketAddress mcastaddr, NetworkInterface netIf) throws IOException {
        helper.invoke(delegate, "joinGroup", new Class[]{SocketAddress.class, NetworkInterface.class}, new Object[]{mcastaddr, netIf});
    }

    protected void leaveGroup(SocketAddress mcastaddr, NetworkInterface netIf) throws IOException {
        helper.invoke(delegate, "leaveGroup", new Class[]{SocketAddress.class, NetworkInterface.class}, new Object[]{mcastaddr, netIf});
    }

    protected void close() {
        helper.invokeQuiet(delegate, "close");
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
