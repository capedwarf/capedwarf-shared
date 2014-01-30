package org.jboss.capedwarf.shared.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class InboundMailAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final long DEFAULT_POLLING_INTERVAL = 60000;

    private String protocol;
    private String host;
    private String user;
    private String password;

    private String folder;
    private long pollingInterval;

    public InboundMailAccount(String protocol, String host, String user, String password, String folder, Long pollingInterval) {
        this.protocol = protocol;
        this.host = host;
        this.user = user;
        this.password = password;
        this.folder = folder;
        this.pollingInterval = pollingInterval == null ? DEFAULT_POLLING_INTERVAL : pollingInterval;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getFolder() {
        return folder;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }
}
