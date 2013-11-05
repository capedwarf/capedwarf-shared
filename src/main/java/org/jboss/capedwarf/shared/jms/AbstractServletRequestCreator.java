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

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create ServletRequest.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractServletRequestCreator implements ServletRequestCreator {
    public void prepare(HttpServletRequest request, String appId) {
    }

    public void finish() {
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) {
        return isStatus2xx(response);
    }

    /**
     * Very simple matching, TODO fix.
     */
    protected boolean match(String mapping, String path) {
        while(mapping.endsWith("*")) {
            mapping = mapping.substring(0, mapping.length() - 1);
        }
        return path.startsWith(mapping);
    }

    protected String getServletPath(ServletContext context, String path) {
        Map<String, ? extends ServletRegistration> map = context.getServletRegistrations();
        for (ServletRegistration sr : map.values()) {
            for (String mapping : sr.getMappings()) {
                if (match(mapping, path)) {
                    return mapping;
                }
            }
        }
        throw new IllegalArgumentException("Could not find a match for path: " + path);
    }

    protected static boolean isStatus2xx(HttpServletResponse response) {
        return isStatusInRange(response, 200, 299);
    }

    protected static boolean isStatusInRange(HttpServletResponse response, int minIncluded, int maxInclued) {
        return minIncluded <= response.getStatus() && response.getStatus() <= maxInclued;
    }
}
