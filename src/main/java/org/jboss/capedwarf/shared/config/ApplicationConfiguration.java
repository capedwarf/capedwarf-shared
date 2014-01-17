package org.jboss.capedwarf.shared.config;

import java.io.Serializable;

import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.Keys;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class ApplicationConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private AppEngineWebXml appEngineWebXml;
    private CapedwarfConfiguration capedwarfConfiguration;
    private QueueXml queueXml;
    private BackendsXml backendsXml;
    private IndexesXml indexesXml;

    public ApplicationConfiguration(AppEngineWebXml appEngineWebXml, CapedwarfConfiguration capedwarfConfiguration, QueueXml queueXml, BackendsXml backendsXml, IndexesXml indexesXml) {
        this.appEngineWebXml = appEngineWebXml;
        this.capedwarfConfiguration = capedwarfConfiguration;
        this.queueXml = queueXml;
        this.backendsXml = backendsXml;
        this.indexesXml = indexesXml;
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

    public static ApplicationConfiguration getInstance() {
        return ComponentRegistry.getInstance().getComponent(Keys.APPLICATION_CONFIGURATION);
    }
}
