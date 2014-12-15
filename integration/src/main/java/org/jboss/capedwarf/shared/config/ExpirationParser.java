/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.shared.config;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class ExpirationParser {
    public static final int MINUTES = 60;
    public static final int HOURS = 3600;
    public static final int DAYS = 24 * 3600;

    public static Long parse(String expiration) {
        if (expiration == null || expiration.isEmpty()) {
            return null;
        }

        long seconds = 0;
        for (String token : expiration.split(" ")) {
            seconds += toSeconds(token);
        }
        return seconds;
    }

    private static long toSeconds(String expiration) {
        long number = (long) Integer.parseInt(expiration.substring(0, expiration.length() - 1));
        char unit = expiration.charAt(expiration.length() - 1);
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
                throw new IllegalStateException(String.format("Invalid time unit: %s", unit));
        }
    }
}
