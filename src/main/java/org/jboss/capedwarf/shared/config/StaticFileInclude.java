package org.jboss.capedwarf.shared.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class StaticFileInclude extends FilePattern {

    private List<StaticFileHttpHeader> headers = new ArrayList<StaticFileHttpHeader>();

    public StaticFileInclude(String pattern) {
        super(pattern);
    }

    public void addHeader(StaticFileHttpHeader header) {
        headers.add(header);
    }

    public List<StaticFileHttpHeader> getHeaders() {
        return headers;
    }
}
