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

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public final class URLHack {
    private static final Field hadlers;
    private static final Field streamHandlerLock;

    static {
        try {
            hadlers = URL.class.getDeclaredField("handlers");
            hadlers.setAccessible(true);

            streamHandlerLock = URL.class.getDeclaredField("streamHandlerLock");
            streamHandlerLock.setAccessible(true);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private URLHack() {
    }

    public static URLStreamHandler removeHandler(String protocol) {
        try {
            final Object lock = streamHandlerLock.get(null);
            synchronized (lock) {
                return removeHandlerNoLock(protocol);
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public static URLStreamHandler removeHandlerNoLock(String protocol) {
        try {
            Map<String, URLStreamHandler> map = (Map<String, URLStreamHandler>) hadlers.get(null);
            return map.remove(protocol);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T inLock(Callable<T> callable) {
        try {
            final Object lock = streamHandlerLock.get(null);
            synchronized (lock) {
                return callable.call();
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
