package org.jboss.capedwarf.shared.config;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class InboundMailAccount {

    private String host;
    private String user;
    private String password;

    private String folder;

    public InboundMailAccount(String host, String user, String password, String folder) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.folder = folder;
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
}
