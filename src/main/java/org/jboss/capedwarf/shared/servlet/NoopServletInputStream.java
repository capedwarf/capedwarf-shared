package org.jboss.capedwarf.shared.servlet;

import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * Noop ServletInputStream
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class NoopServletInputStream extends ServletInputStream {

    public static ServletInputStream INSTANCE = new NoopServletInputStream();

    public int read() throws IOException {
        return -1;
    }

    public boolean isFinished() {
        return true;
    }

    public boolean isReady() {
        return true;
    }

    public void setReadListener(ReadListener readListener) {
    }
}
