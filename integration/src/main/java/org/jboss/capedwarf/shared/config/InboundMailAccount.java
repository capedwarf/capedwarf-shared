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

import java.io.Serializable;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class InboundMailAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final long DEFAULT_POLLING_INTERVAL = 60000;

    private String protocol;
    private String host;
    private String user;
    private String password;

    private String folder;
    private long pollingInterval;

    public InboundMailAccount(String protocol, String host, String user, String password, String folder, Long pollingInterval) {
        this.protocol = protocol;
        this.host = host;
        this.user = user;
        this.password = password;
        this.folder = folder;
        this.pollingInterval = pollingInterval == null ? DEFAULT_POLLING_INTERVAL : pollingInterval;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getFolder() {
        return folder;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }
}
