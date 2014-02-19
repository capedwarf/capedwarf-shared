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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.capedwarf.shared.servlet.AbstractHttpServletRequest;

/**
 * Create ServletRequest.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractServletRequestCreator implements ServletRequestCreator {
    private static final String DEFAULT = "default";

    public void prepare(HttpServletRequest request, String appId, String module) {
    }

    public void finish() {
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) {
        return isStatus2xx(response);
    }

    protected void applyPaths(ServletContext context, AbstractHttpServletRequest request, String path) {
        final int p = path.indexOf("?");

        String queryString = ((p < 0) || (p == path.length() - 1)) ? null : path.substring(p + 1);
        request.setQueryString(queryString);

        if (p >= 0) {
            path = path.substring(0, p);
        }

        request.setPath(path);

        String servletPath = getServletPath(context, path);
        request.setServletPath(servletPath);

        String pathInfo = path.substring(servletPath.length());
        if ((pathInfo.length() == 0 || (pathInfo.length() == 1 && pathInfo.charAt(0) == '/')) == false) {
            request.setPathInfo(addStartSlash(pathInfo));
        }

    }

    /**
     * Very simple matching, TODO fix.
     */
    protected String match(String mapping, String path) {
        boolean star = false;
        while(mapping.endsWith("*")) {
            star = true;
            mapping = mapping.substring(0, mapping.length() - 1);
        }
        if (star && mapping.endsWith("/")) {
            mapping = mapping.substring(0, mapping.length() - 1);
        }
        return (path.startsWith(mapping) ? mapping : null);
    }

    protected String getServletPath(ServletContext context, String path) {
        Map<String, ? extends ServletRegistration> map = context.getServletRegistrations();
        Set<String> longestMatch = new TreeSet<>(Collections.reverseOrder());
        for (Map.Entry<String, ? extends ServletRegistration> entry : map.entrySet()) {
            if (DEFAULT.equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            for (String mapping : entry.getValue().getMappings()) {
                final String sp = match(mapping, path);
                if (sp != null) {
                    longestMatch.add(sp);
                }
            }
        }
        if (longestMatch.isEmpty()) {
            throw new IllegalArgumentException("Could not find a match for path: " + path);
        } else {
            return longestMatch.iterator().next();
        }
    }

    protected static boolean isStatus2xx(HttpServletResponse response) {
        return isStatusInRange(response, 200, 299);
    }

    protected static boolean isStatusInRange(HttpServletResponse response, int minIncluded, int maxInclued) {
        return minIncluded <= response.getStatus() && response.getStatus() <= maxInclued;
    }

    protected static String addStartSlash(String path) {
        if (path.startsWith("/") == false) {
            path = "/" + path;
        }
        return path;
    }

    protected static String removeEndSlash(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
