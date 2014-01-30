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
 * Component key.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractKey<T> implements CacheableKey<T> {
    private final AppIdFactory appIdFactory;
    private final Object slot;
    private final Object key;

    public AbstractKey(final String appId, final String module, Object slot) {
        this(new AppIdFactory() {
            public String appId() {
                return appId;
            }

            public String module() {
                return module;
            }
        }, slot);
    }

    public AbstractKey(AppIdFactory appIdFactory, Object slot) {
        this.appIdFactory = appIdFactory;
        this.slot = slot;
        this.key = ComponentRegistry.toCacheableKey(this);
    }

    public String getAppId() {
        return appIdFactory.appId();
    }

    public String getModule() {
        return appIdFactory.module();
    }

    public Object getSlot() {
        return slot;
    }

    public Object getCacheableKey() {
        return key;
    }

    @Override
    public String toString() {
        return String.format("appId:%s, module:%s, slot:%s", getAppId(), getModule(), getSlot());
    }
}