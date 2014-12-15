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

package org.jboss.capedwarf.shared.config;

import java.io.Serializable;

import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.SimpleKey;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class ApplicationConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AppEngineWebXml appEngineWebXml;
    private final CapedwarfConfiguration capedwarfConfiguration;
    private final QueueXml queueXml;
    private final BackendsXml backendsXml;
    private final IndexesXml indexesXml;
    private final CronXml cronXml;

    public ApplicationConfiguration(AppEngineWebXml appEngineWebXml, CapedwarfConfiguration capedwarfConfiguration, QueueXml queueXml, BackendsXml backendsXml, IndexesXml indexesXml, CronXml cronXml) {
        this.appEngineWebXml = appEngineWebXml;
        this.capedwarfConfiguration = capedwarfConfiguration;
        this.queueXml = queueXml;
        this.backendsXml = backendsXml;
        this.indexesXml = indexesXml;
        this.cronXml = cronXml;
    }

    public AppEngineWebXml getAppEngineWebXml() {
        return appEngineWebXml;
    }

    public CapedwarfConfiguration getCapedwarfConfiguration() {
        return capedwarfConfiguration;
    }

    public QueueXml getQueueXml() {
        return queueXml;
    }

    public BackendsXml getBackendsXml() {
        return backendsXml;
    }

    public IndexesXml getIndexesXml() {
        return indexesXml;
    }

    public CronXml getCronXml() {
        return cronXml;
    }

    public static ApplicationConfiguration getInstance() {
        return ComponentRegistry.getInstance().getComponent(new SimpleKey<>(ApplicationConfiguration.class));
    }
}
