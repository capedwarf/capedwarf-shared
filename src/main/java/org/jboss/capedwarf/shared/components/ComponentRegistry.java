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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bit hackish aka type-unsafe registry.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings("unchecked")
public final class ComponentRegistry {
    private static final ComponentRegistry INSTANCE = new ComponentRegistry();

    private final ConcurrentMap<String, Lock> locks = new ConcurrentHashMap<>();
    private final ConcurrentMap<Object, Object> registry = new ConcurrentHashMap<>();

    public static ComponentRegistry getInstance() {
        return INSTANCE;
    }

    private Lock lock(String appId) {
        Lock lock = new ReentrantLock();
        final Lock previous = locks.putIfAbsent(appId, lock);
        if (previous != null) {
            lock = previous;
        }
        lock.lock();
        return lock;
    }

    static <T> Object toCacheableKey(Key<T> key) {
        return key.getModule() + "_" + key.getSlot();
    }

    private static Object toSlot(Key<?> key) {
        if (key instanceof CacheableKey) {
            return CacheableKey.class.cast(key).getCacheableKey();
        } else {
            return toCacheableKey(key);
        }
    }

    private <T> T getValue(Map<Object, ?> map, Object slot, Class<T> type) {
        if (map != null) {
            Object value = map.get(slot);
            return type.cast(value);
        } else {
            return null;
        }
    }

    public <T> T getComponent(Key<T> key) {
        Object slot = toSlot(key);
        Class<T> type = key.getType();
        String appId = key.getAppId();
        if (appId == null) {
            return getValue(registry, slot, type);
        } else {
            final Lock lock = lock(appId);
            try {
                Map<Object, Object> map = (Map<Object, Object>) registry.get(appId);
                return getValue(map, slot, type);
            } finally {
                lock.unlock();
            }
        }
    }

    public <T> T putIfAbsent(Key<T> key, T value) {
        Object slot = toSlot(key);
        Class<T> type = key.getType();
        String appId = key.getAppId();
        if (appId == null) {
            Object result = registry.putIfAbsent(slot, value);
            return type.cast(result);
        } else {
            final Lock lock = lock(appId);
            try {
                T previous = null;
                Map<Object, Object> map = (Map<Object, Object>) registry.get(appId);
                if (map == null) {
                    map = new HashMap<>();
                    registry.put(appId, map);
                    map.put(slot, value);
                } else {
                    previous = getValue(map, slot, type);
                    if (previous == null) {
                        map.put(slot, value);
                    }
                }
                return previous;
            } finally {
                lock.unlock();
            }
        }
    }

    public <T> void setComponent(Key<T> key, T value) {
        Object slot = toSlot(key);
        String appId = key.getAppId();
        if (appId == null) {
            registry.put(slot, value);
        } else {
            final Lock lock = lock(appId);
            try {
                Map<Object, Object> map = (Map<Object, Object>) registry.get(appId);
                if (map == null) {
                    map = new HashMap<>();
                    registry.put(appId, map);
                }
                map.put(slot, value);
            } finally {
                lock.unlock();
            }
        }
    }

    public <T> void removeComponent(Key<T> key) {
        Object slot = toSlot(key);
        String appId = key.getAppId();
        if (appId == null) {
            registry.remove(slot);
        } else {
            final Lock lock = lock(appId);
            try {
                Map<Object, Object> map = (Map<Object, Object>) registry.get(appId);
                if (map != null) {
                    map.remove(slot);
                    if (map.isEmpty()) {
                        registry.remove(appId);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void clearComponents(String appId) {
        // clear registry
        final Map<Object, Object> map;
        final Lock lock = lock(appId);
        try {
            map = (Map<Object, Object>) registry.remove(appId);
        } finally {
            lock.unlock();
        }
        // clear locks
        locks.remove(appId);
        // shutdown components
        if (map != null) {
            for (Object value : map.values()) {
                if (value instanceof ShutdownHook) {
                    ShutdownHook.class.cast(value).shutdown();
                }
            }
        }
    }

    public String dump(String appId) {
        if (appId != null) {
            Object map = registry.get(appId);
            return (map != null) ? String.valueOf(map) : String.format("[Emtpty:%s]", appId);
        } else {
            return "[Cannot dump global components]";
        }
    }
}
