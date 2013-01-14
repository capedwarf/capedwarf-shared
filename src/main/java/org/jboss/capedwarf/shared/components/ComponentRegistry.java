package org.jboss.capedwarf.shared.components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A bit hackish aka type-unsafe registry.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings("unchecked")
public final class ComponentRegistry {
    private static final ComponentRegistry INSTANCE = new ComponentRegistry();

    private final Map<Object, Object> registry = new ConcurrentHashMap<Object, Object>();

    public static ComponentRegistry getInstance() {
        return INSTANCE;
    }

    private <T> T getValue(Map<Object, ?> map, Object slot, Class<T> type) {
        Object value = map.get(slot);
        return type.cast(value);
    }

    public <T> T getComponent(Key<T> key) {
        Object slot = key.getSlot();
        Class<T> type = key.getType();
        String appId = key.getAppId();
        if (appId == null) {
            return getValue(registry, slot, type);
        } else {
            synchronized (registry) {
                Map<Object, Object> map = (Map<Object, Object>) registry.get(appId);
                return getValue(map, slot, type);
            }
        }
    }

    public <T> void setComponent(Key<T> key, T value) {
        Object slot = key.getSlot();
        String appId = key.getAppId();
        if (appId == null) {
            registry.put(slot, value);
        } else {
            synchronized (registry) {
                Map<Object, Object> map = (Map<Object, Object>) registry.get(appId);
                if (map == null) {
                    map = new HashMap<Object, Object>();
                    registry.put(appId, map);
                }
                map.put(slot, value);
            }
        }
    }

    public void clearComponents(String appId) {
        final Map<Object, Object> map;
        synchronized (registry) {
            map = (Map<Object, Object>) registry.remove(appId);
        }
        if (map != null) {
            for (Object value : map.values()) {
                if (value instanceof ShutdownHook) {
                    ShutdownHook.class.cast(value).shutdown();
                }
            }
        }
    }
}
