package org.jboss.capedwarf.shared.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class StaticFileInclude extends FilePattern {

    private String expiration;
    private Long expirationSeconds;
    private List<StaticFileHttpHeader> headers = new ArrayList<StaticFileHttpHeader>();

    public StaticFileInclude(String pattern, String expiration) {
        super(pattern);
        this.expiration = expiration;
        this.expirationSeconds = ExpirationParser.parse(expiration);
    }

    public void addHeader(StaticFileHttpHeader header) {
        headers.add(header);
    }

    public List<StaticFileHttpHeader> getHeaders() {
        return headers;
    }

    public String getExpiration() {
        return expiration;
    }

    public Long getExpirationSeconds() {
        return expirationSeconds;
    }
}
