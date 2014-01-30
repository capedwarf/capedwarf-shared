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

package org.jboss.capedwarf.shared.modules;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.Key;
import org.jboss.capedwarf.shared.components.MapKey;
import org.jboss.capedwarf.shared.components.Slot;
import org.jboss.capedwarf.shared.config.AppEngineWebXml;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ModuleInfo implements Serializable {
    private static final String PER_APP = "PER_APP";
    public static final String DEFAULT_MODULE_NAME = "default";

    private AppEngineWebXml config;
    private List<InstanceInfo> instances;

    public static void putModules(String appId) {
        ComponentRegistry registry = ComponentRegistry.getInstance();
        Key<Map<String, ModuleInfo>> key = new MapKey<>(appId, PER_APP, Slot.MODULES);
        registry.setComponent(key, new ConcurrentHashMap<String, ModuleInfo>());
    }

    public static Map<String, ModuleInfo> getModules(String appId) {
        ComponentRegistry registry = ComponentRegistry.getInstance();
        return registry.getComponent(new MapKey<String, ModuleInfo>(appId, PER_APP, Slot.MODULES));
    }

    public static ModuleInfo getModuleInfo(String appId, String module) {
        Map<String, ModuleInfo> map = getModules(appId);
        if (map == null) {
            throw new IllegalStateException(String.format("No such modules map, appId: %s", appId));
        }
        ModuleInfo info = map.get(module);
        if (info == null) {
            throw new IllegalArgumentException(String.format("No such module info, module: %s", module));
        }
        return info;
    }

    public ModuleInfo() {
        // serialization only
    }

    public ModuleInfo(AppEngineWebXml config) {
        this.config = config;
        this.instances = new CopyOnWriteArrayList<>();
    }

    public void addInstance(InstanceInfo instance) {
        instances.add(instance);
    }

    public void removeInstance(InstanceInfo instance) {
        Iterator<InstanceInfo> iter = instances.iterator();
        while (iter.hasNext()) {
            if (instance.getId().equals(iter.next().getId())) {
                iter.remove();
                return;
            }
        }
    }

    public AppEngineWebXml getConfig() {
        return config;
    }

    public InstanceInfo getInstance(int i) {
        if (i < 0 || i >= instances.size()) {
            throw new IllegalArgumentException("No such instance: " + i);
        }
        return instances.get(i);
    }

    public InstanceInfo getInstance(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Null id!");
        }

        for (InstanceInfo info : instances) {
            if (id.equals(info.getId())) {
                return info;
            }
        }

        throw new IllegalArgumentException("No such instance: " + id);
    }
}
