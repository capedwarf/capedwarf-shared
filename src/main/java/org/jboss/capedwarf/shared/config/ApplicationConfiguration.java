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
