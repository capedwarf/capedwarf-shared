package org.jboss.capedwarf.shared.config;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class FilePattern implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Pattern regex;
    private final String pattern;

    public FilePattern(String pattern) {
        if (!pattern.startsWith("/")) {
            pattern = "/" + pattern;
        }
        String regexPattern = pattern.replaceAll("([^A-Za-z0-9\\-_/])", "\\\\$1")
            .replaceAll("\\\\\\*\\\\\\*", ".*")
            .replaceAll("\\\\\\*", "[^/]*");
        this.regex = Pattern.compile(regexPattern);
        this.pattern = pattern;
    }

    public boolean matches(String path) {
        return regex.matcher(path).matches();
    }

    @Override
    public String toString() {
        return "FilePattern{" +
            "pattern='" + pattern + '\'' +
            ", regex=" + regex +
            '}';
    }
}
