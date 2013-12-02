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

package org.jboss.capedwarf.shared.url;

import java.lang.Class;import java.lang.Object;
import java.net.HttpURLConnection;
import java.net.URL;

import javassist.util.proxy.MethodHandler;

import org.jboss.capedwarf.shared.bytecode.BytecodeUtils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CapedwarfURLConnectionProxyFactory {
    private static final Class<?>[] PARAM_TYPES = new Class[]{URL.class};

    private static <T extends HttpURLConnection> T wrap(Class<T> exactType, MethodHandler handler, HttpURLConnection delegate) {
        return BytecodeUtils.proxy(exactType, handler, PARAM_TYPES, new Object[]{delegate.getURL()});
    }

    static <T extends HttpURLConnection> T basic(Class<T> exactType, HttpURLConnection delegate) {
        return wrap(exactType, new DefaultURLConnectionProxyHandler(delegate), delegate);
    }

    static <T extends HttpURLConnection> T streaming(Class<T> exactType, HttpURLConnection delegate) {
        return wrap(exactType, new CapedwarfURLConnectionProxyHandler(delegate), delegate);
    }
}
