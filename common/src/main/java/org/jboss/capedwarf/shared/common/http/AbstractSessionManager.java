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

package org.jboss.capedwarf.shared.common.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionIdGenerator;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionListeners;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractSessionManager implements SessionManager {
    protected final Deployment deployment;
    protected SessionIdGenerator sessionIdGenerator = new SecureRandomSessionIdGenerator();
    protected final SessionListeners sessionListeners = new SessionListeners();
    protected volatile int defaultSessionTimeout = 30 * 60;

    public AbstractSessionManager(Deployment deployment) {
        this.deployment = deployment;
    }

    SessionListeners getSessionListeners() {
        return sessionListeners;
    }

    protected abstract boolean sessionExists(String sessionId);

    protected String createId(HttpServerExchange exchange, SessionConfig config) {
        String sessionId = config.findSessionId(exchange);
        int count = 0;
        while (sessionId == null) {
            sessionId = sessionIdGenerator.createSessionId();
            if (sessionExists(sessionId)) {
                sessionId = null;
            }
            if (count++ == 100) {
                throw new IllegalStateException("Cannot generate session id!");
            }
        }
        config.setSessionId(exchange, sessionId);
        return sessionId;
    }

    public String getDeploymentName() {
        return deployment.getDeploymentInfo().getDeploymentName();
    }

    public void start() {
    }

    protected abstract Session createSessionInternal(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig, long time);

    public Session createSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        long time = System.currentTimeMillis();
        Session session = createSessionInternal(serverExchange, sessionCookieConfig, time);
        sessionListeners.sessionCreated(session, serverExchange);
        return session;
    }

    public Session getSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        return getSession(sessionCookieConfig.findSessionId(serverExchange), sessionCookieConfig);
    }

    protected Session getSession(String sessionId, SessionConfig sessionConfig) {
        if (sessionId == null) {
            return null;
        }
        return getSessionInternal(sessionId, sessionConfig);
    }

    protected abstract Session getSessionInternal(String sessionId, SessionConfig sessionConfig);

    public Session getSession(String sessionId) {
        return getSession(sessionId, null);
    }

    public void registerSessionListener(SessionListener listener) {
        sessionListeners.addSessionListener(listener);
    }

    public void removeSessionListener(SessionListener listener) {
        sessionListeners.removeSessionListener(listener);
    }

    public void setDefaultSessionTimeout(int timeout) {
        defaultSessionTimeout = timeout;
    }
}
