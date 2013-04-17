package org.jboss.capedwarf.shared.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class StaticFileHttpHeader implements Serializable {
    private static final long serialVersionUID = 1L;

    private String headerName;
    private String headerValue;

    public StaticFileHttpHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
