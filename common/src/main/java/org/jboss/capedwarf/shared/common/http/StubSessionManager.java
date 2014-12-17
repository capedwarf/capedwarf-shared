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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.undertow.UndertowMessages;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class StubSessionManager extends AbstractSessionManager {
    private static final Set<String> ignoredPackages;

    private final ConcurrentMap<String, StubSession> sessions = new ConcurrentHashMap<>();

    static {
        ignoredPackages = new HashSet<>();
        ignoredPackages.add("org.apache.jasper.");
        ignoredPackages.add("org.jboss.weld.");
    }

    public StubSessionManager(Deployment deployment) {
        super(deployment);
    }

    private static boolean isIgnored() {
        StackTraceElement[] elts = Thread.currentThread().getStackTrace();
        for (StackTraceElement elt : elts) {
            String className = elt.getClassName();
            for (String ip : ignoredPackages) {
                if (className.startsWith(ip)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void throwException() {
        if (isIgnored()) {
            return;
        }
        throw new RuntimeException("Session support is not enabled in appengine-web.xml.  "
            + "To enable sessions, put <sessions-enabled>true</sessions-enabled> in that "
            + "file.  Without it, getSession() is allowed, but manipulation of session"
            + " attributes is not.");
    }

    public void stop() {
        for (StubSession session : sessions.values()) {
            getSessionListeners().sessionDestroyed(session, null, SessionListener.SessionDestroyedReason.UNDEPLOY);
        }
        sessions.clear();
    }

    protected boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    protected Session createSessionInternal(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig, long time) {
        return new StubSession(this, sessionCookieConfig, createId(serverExchange, sessionCookieConfig), time, time);
    }

    protected Session getSessionInternal(String sessionId, SessionConfig sessionConfig) {
        return sessions.get(sessionId);
    }

    public Set<String> getTransientSessions() {
        return getAllSessions();
    }

    public Set<String> getActiveSessions() {
        return getAllSessions();
    }

    public Set<String> getAllSessions() {
        return new HashSet<>(sessions.keySet());
    }

    private static class StubSession implements Session {
        private final StubSessionManager sessionManager;
        private final SessionConfig sessionConfig;
        private final String id;
        private final long created;
        private long accessed;
        private int maxInactiveInterval;

        public StubSession(StubSessionManager sessionManager, SessionConfig sessionConfig, String id, long created, long accessed) {
            this.sessionManager = sessionManager;
            this.sessionConfig = sessionConfig;
            this.id = id;
            this.created = created;
            this.accessed = accessed;
        }

        public String getId() {
            return id;
        }

        public void requestDone(HttpServerExchange serverExchange) {
            accessed = System.currentTimeMillis();
        }

        public long getCreationTime() {
            return created;
        }

        public long getLastAccessedTime() {
            return accessed;
        }

        public void setMaxInactiveInterval(int interval) {
            this.maxInactiveInterval = interval;
        }

        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        public Object getAttribute(String name) {
            return null;
        }

        public Set<String> getAttributeNames() {
            return Collections.emptySet();
        }

        public Object setAttribute(String name, Object value) {
            throwException();
            return null;
        }

        public Object removeAttribute(String name) {
            throwException();
            return null;
        }

        public void invalidate(HttpServerExchange exchange) {
            StubSession session = sessionManager.sessions.remove(id);
            if (session == null) {
                throw UndertowMessages.MESSAGES.sessionAlreadyInvalidated();
            }
            sessionManager.getSessionListeners().sessionDestroyed(session, exchange, SessionListener.SessionDestroyedReason.INVALIDATED);
            if (exchange != null) {
                sessionConfig.clearSession(exchange, id);
            }
        }

        public SessionManager getSessionManager() {
            return sessionManager;
        }

        public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
            throwException();
            return null;
        }
    }
}
