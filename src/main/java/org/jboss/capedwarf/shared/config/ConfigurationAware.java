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

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class ConfigurationAware {
    protected AppEngineWebXml appEngineWebXml;
    protected CapedwarfConfiguration capedwarfConfiguration;
    protected QueueXml queueXml;
    protected BackendsXml backendsXml;
    protected IndexesXml indexesXml;

    // hack around passing config instances

    protected static final ThreadLocal<AppEngineWebXml> appEngineWebXmlTL = new ThreadLocal<AppEngineWebXml>();
    protected static final ThreadLocal<CapedwarfConfiguration> capedwarfConfigurationTL = new ThreadLocal<CapedwarfConfiguration>();
    protected static final ThreadLocal<QueueXml> queueXmlTL = new ThreadLocal<QueueXml>();
    protected static final ThreadLocal<BackendsXml> backendsTL = new ThreadLocal<BackendsXml>();
    protected static final ThreadLocal<IndexesXml> indexesTL = new ThreadLocal<IndexesXml>();

    protected ConfigurationAware() {
    }

    protected ConfigurationAware(AppEngineWebXml appEngineWebXml, CapedwarfConfiguration capedwarfConfiguration, QueueXml queueXml, BackendsXml backendsXml, IndexesXml indexesXml) {
        this.appEngineWebXml = appEngineWebXml;
        this.capedwarfConfiguration = capedwarfConfiguration;
        this.queueXml = queueXml;
        this.backendsXml = backendsXml;
        this.indexesXml = indexesXml;
    }

    protected void initialize() {
        appEngineWebXml = appEngineWebXmlTL.get();
        capedwarfConfiguration = capedwarfConfigurationTL.get();
        queueXml = queueXmlTL.get();
        backendsXml = backendsTL.get();
        indexesXml = indexesTL.get();
    }

    public static void setAppEngineWebXml(AppEngineWebXml appEngineWebXml) {
        if (appEngineWebXml != null)
            appEngineWebXmlTL.set(appEngineWebXml);
        else
            appEngineWebXmlTL.remove();
    }

    public static void setCapedwarfConfiguration(CapedwarfConfiguration capedwarfConfiguration) {
        if (capedwarfConfiguration != null)
            capedwarfConfigurationTL.set(capedwarfConfiguration);
        else
            capedwarfConfigurationTL.remove();
    }

    public static void setQueueXml(QueueXml queueXml) {
        if (queueXml != null)
            queueXmlTL.set(queueXml);
        else
            queueXmlTL.remove();
    }

    public static void setBackendsXml(BackendsXml backends) {
        if (backends != null)
            backendsTL.set(backends);
        else
            backendsTL.remove();
    }

    public static void setIndexesXml(IndexesXml indexes) {
        if (indexes != null)
            indexesTL.set(indexes);
        else
            indexesTL.remove();
    }
}
