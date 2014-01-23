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

import javax.jms.Message;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Create ServletRequest.
 *
 * Note: instances of this class are cached, hence not stateless.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ServletRequestCreator extends ResponseValidator {
    /**
     * Create mock http servlet request, for async tasks.
     *
     * @param context the servlet context
     * @param message the message
     * @return new http servlet request
     * @throws Exception for any error
     */
    HttpServletRequest createServletRequest(ServletContext context, Message message) throws Exception;

    /**
     * Prepare, before dispatch.
     *
     * @param request the request
     * @param appId the app Id
     * @param module the module
     */
    void prepare(HttpServletRequest request, String appId, String module);

    /**
     * Finish, after dispatch.
     */
    void finish();
}
