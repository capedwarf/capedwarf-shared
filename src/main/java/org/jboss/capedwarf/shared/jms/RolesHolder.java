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

package org.jboss.capedwarf.shared.jms;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class RolesHolder {
    private static final ThreadLocal<Set<String>> roles = new ThreadLocal<>();

    static void addRole(String role) {
        Set<String> set = roles.get();
        if (set == null) {
            set = new HashSet<>();
            roles.set(set);
        }
        set.add(role);
    }

    static void removeRole(String role) {
        Set<String> set = roles.get();
        if (set != null) {
            set.remove(role);
            if (set.isEmpty()) {
                roles.remove();
            }
        }
    }

    static void addAll(Set<String> set) {
        roles.set(set);
    }

    static void removeAll() {
        roles.remove();
    }

    public static Set<String> getRoles() {
        final Set<String> set = roles.get();
        return (set != null) ? Collections.unmodifiableSet(set) : null;
    }
}
