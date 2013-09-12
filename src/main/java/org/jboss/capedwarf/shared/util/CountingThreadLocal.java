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

package org.jboss.capedwarf.shared.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CountingThreadLocal<T> {
    private final ThreadLocal<Map<T, Integer>> tl = new ThreadLocal<>();

    /**
     * Has value.
     *
     * @param value the value
     * @return true if exists, false otherwise
     */
    public boolean hasValue(T value) {
        Map<T, Integer> values = tl.get();
        return values != null && values.containsKey(value);
    }

    /**
     * Add value.
     *
     * @param value the value
     */
    public void add(T value) {
        int count;
        Map<T, Integer> values = tl.get();
        if (values == null) {
            values = new HashMap<>();
            tl.set(values);
            count = 1;
        } else {
            Integer x = values.get(value);
            count = (x == null) ? 1 : (x + 1);
        }
        values.put(value, count);
    }

    /**
     * Remove value.
     *
     * @param value the value
     */
    public void remove(T value) {
        Map<T, Integer> values = tl.get();
        if (values != null) {
            Integer x = values.get(value);
            if (x != null) {
                if (x == 1) {
                    values.remove(value);
                    if (values.isEmpty()) {
                        tl.remove();
                    }
                } else {
                    values.put(value, x - 1);
                }
            }
        }
    }
}
