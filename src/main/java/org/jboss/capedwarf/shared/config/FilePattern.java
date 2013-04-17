package org.jboss.capedwarf.shared.config;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class FilePattern implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Pattern regex;

    public FilePattern(String pattern) {
        String regexPattern = pattern.replaceAll("([^A-Za-z0-9\\-_/])", "\\\\$1")
            .replaceAll("\\\\\\*\\\\\\*", ".*")
            .replaceAll("\\\\\\*", "[^/]*");
        this.regex = Pattern.compile(regexPattern);
    }

    public boolean matches(String path) {
        return regex.matcher(path).matches();
    }
}
