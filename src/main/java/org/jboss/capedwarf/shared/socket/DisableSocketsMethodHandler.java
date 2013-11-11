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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import org.jboss.capedwarf.shared.compatibility.Compatibility;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class DisableSocketsMethodHandler implements MethodHandler {
    protected abstract Object getDelegate();

    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        Compatibility.enable(Compatibility.Feature.IGNORE_CAPEDWARF_SOCKETS);
        try {
            return invokeInternal(self, thisMethod, proceed, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } finally {
            Compatibility.disable(Compatibility.Feature.IGNORE_CAPEDWARF_SOCKETS);
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected Object invokeInternal(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return thisMethod.invoke(getDelegate(), args);
    }
}
