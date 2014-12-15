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

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CountingThreadLocal<T> {
    private final ThreadLocal<Pair<T>> counter = new ThreadLocal<>();

    /**
     * Has value.
     *
     * @return true if exists, false otherwise
     */
    public boolean hasValue() {
        return (counter.get() != null);
    }

    /**
     * Get value.
     *
     * @return value or null
     */
    public T get() {
        Pair<T> pair = counter.get();
        return (pair != null) ? pair.value : null;
    }

    /**
     * Set value.
     *
     * @param value the value
     */
    public void set(T value) {
        Pair<T> pair = counter.get();
        if (pair == null) {
            counter.set(new Pair<>(1, value));
        } else {
            pair.i++;
        }
    }

    /**
     * Remove value.
     */
    public void remove() {
        Pair<T> pair = counter.get();
        if (pair != null) {
            pair.i--;
            if (pair.i == 0) {
                counter.remove();
            }
        }
    }

    private static class Pair<V> {
        private int i;
        private V value;

        private Pair(int i, V value) {
            this.i = i;
            this.value = value;
        }
    }
}
