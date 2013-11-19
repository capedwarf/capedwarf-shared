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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.DatagramSocketImpl;
import java.net.DatagramSocketImplFactory;
import java.net.MulticastSocket;
import java.net.SocketImpl;
import java.net.SocketImplFactory;

import org.jboss.capedwarf.shared.compatibility.Compatibility;
import org.jboss.capedwarf.shared.components.AppIdFactory;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CapedwarfSocketFactory implements SocketImplFactory, DatagramSocketImplFactory {
    public static final CapedwarfSocketFactory INSTANCE = new CapedwarfSocketFactory();

    private CapedwarfSocketFactory() {
    }

    SocketImpl createDelegate() {
        try {
            Class<?> clazz = getClass().getClassLoader().loadClass("java.net.SocksSocketImpl");
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (SocketImpl) ctor.newInstance();
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    DatagramSocketImpl createDatagramDelegate() {
        try {
            Class<?> clazz = getClass().getClassLoader().loadClass("java.net.DefaultDatagramSocketImplFactory");
            Method method = clazz.getDeclaredMethod("createDatagramSocketImpl", Boolean.TYPE);
            method.setAccessible(true);
            return (DatagramSocketImpl) method.invoke(null, isInvokedFromMulticastSocket());
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private static boolean isInvokedFromMulticastSocket() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().equals(MulticastSocket.class.getName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIgnoreCapedwarfSockets() {
        return (AppIdFactory.hasAppId() == false || Compatibility.getInstance().isEnabled(Compatibility.Feature.IGNORE_CAPEDWARF_SOCKETS));
    }

    public SocketImpl createSocketImpl() {
        final SocketImpl delegate = createDelegate();

        if (isIgnoreCapedwarfSockets()) {
            return delegate;
        }

        return new CapedwarfSocket(delegate);
    }

    public DatagramSocketImpl createDatagramSocketImpl() {
        final DatagramSocketImpl delegate = createDatagramDelegate();

        if (isIgnoreCapedwarfSockets()) {
            return delegate;
        }

        return new CapedwarfDatagramSocket(delegate);
    }
}
