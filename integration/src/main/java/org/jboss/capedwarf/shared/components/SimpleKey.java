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

package org.jboss.capedwarf.shared.components;

/**
 * Simple component key.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleKey<T> extends BaseKey<T> {
    protected static String toName(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Null type!");
        }
        return type.getName();
    }

    public static <T> Key<T> withClassloader(Class<T> type) {
        return new SimpleKey<>(ClassloaderAppIdFactory.INSTANCE, type);
    }

    public SimpleKey(Class<T> type) {
        super(ClassloaderAppIdFactory.INSTANCE, toName(type), type);
    }

    public SimpleKey(String appId, String module, Class<T> type) {
        super(appId, module, toName(type), type);
    }

    public SimpleKey(AppIdFactory appIdFactory, Class<T> type) {
        super(appIdFactory, toName(type), type);
    }
}