package org.jboss.capedwarf.shared.config;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class ExpirationParser {

    public static final int MINUTES = 60;
    public static final int HOURS = 3600;
    public static final int DAYS = 24 * 3600;

    public Long parse(String expiration) {
        if (expiration == null || expiration.isEmpty()) {
            return null;
        }

        long seconds = 0;
        for (String token : expiration.split(" ")) {
            seconds += toSeconds(token);
        }
        return seconds;
    }

    private Long toSeconds(String expiration) {
        long number = (long) Integer.parseInt(expiration.substring(0, expiration.length() - 1));
        char unit = expiration.charAt(expiration.length()-1);
        switch (unit) {
            case 's':
                return number;
            case 'm':
                return number * MINUTES;
            case 'h':
                return number * HOURS;
            case 'd':
                return number * DAYS;
            default:
                return null;
        }
    }
}
