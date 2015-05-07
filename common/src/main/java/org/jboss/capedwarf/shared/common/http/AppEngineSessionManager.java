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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.runtime.SessionData;
import com.google.apphosting.runtime.SessionStore;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.servlet.api.Deployment;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class AppEngineSessionManager extends AbstractSessionManager {
    private static final Logger logger = Logger.getLogger(AppEngineSessionManager.class.getName());

    private final List<SessionStore> sessionStoresInWriteOrder;
    private final List<SessionStore> sessionStoresInReadOrder;

    private static List<SessionStore> createSessionStores(boolean asyncSessionPersistence, String queueName) {
        SessionStore datastoreSessionStore = asyncSessionPersistence ?
            new DeferredLazySessionStore(queueName) :
            new DatastoreLazySessionStore();
        return Arrays.asList(datastoreSessionStore, new MemcacheLazySessionStore());
    }

    public AppEngineSessionManager(Deployment deployment, boolean asyncSessionPersistence, String queueName) {
        super(deployment);
        sessionStoresInWriteOrder = createSessionStores(asyncSessionPersistence, queueName);
        sessionStoresInReadOrder = new ArrayList<>(sessionStoresInWriteOrder);
        Collections.reverse(sessionStoresInReadOrder);
    }

    public void stop() {
        for (String key : getActiveSessions()) {
            Session session = getSession(key);
            if (session != null) {
                getSessionListeners().sessionDestroyed(session, null, SessionListener.SessionDestroyedReason.UNDEPLOY);
                deleteSession(key);
            }
        }
    }

    protected boolean sessionExists(String sessionId) {
        return (loadSession(sessionId) != null);
    }

    protected Session createSessionInternal(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig, long time) {
        return new AppEngineSession(this, sessionCookieConfig, createId(serverExchange, sessionCookieConfig), time, time);
    }

    protected Session getSessionInternal(String sessionId, SessionConfig sessionConfig) {
        SessionData data = loadSession(sessionId);
        if (data != null) {
            long time = System.currentTimeMillis();
            return new AppEngineSession(this, sessionConfig, sessionId, data, time, time);
        } else {
            return null;
        }
    }

    public Set<String> getTransientSessions() {
        return Collections.emptySet(); // none are transient
    }

    public Set<String> getActiveSessions() {
        return getAllSessions();
    }

    public Set<String> getAllSessions() {
        for (SessionStore sessionStore : sessionStoresInReadOrder) {
            Map<String, SessionData> sessions = sessionStore.getAllSessions();
            if (sessions != null) {
                Set<String> set = new HashSet<>();
                for (String key : sessions.keySet()) {
                    set.add(key);
                }
                return set;
            }
        }
        return Collections.emptySet();
    }

    SessionData createSession(String sessionId) {
        SessionData data = new SessionData();
        data.setExpirationTime(System.currentTimeMillis() + getSessionExpirationInMilliseconds());
        saveSession(sessionId, data);
        return data;
    }

    void saveSession(String sessionId, SessionData data) {
        for (SessionStore sessionStore : sessionStoresInWriteOrder) {
            try {
                sessionStore.saveSession(sessionId, data);
            } catch (SessionStore.Retryable retryable) {
                throw retryable.getCause();
            }
        }
    }

    SessionData loadSession(String sessionId) {
        SessionData data = null;
        for (SessionStore sessionStore : sessionStoresInReadOrder) {
            try {
                data = sessionStore.getSession(sessionId);
                if (data != null) {
                    break;
                }
            } catch (RuntimeException e) {
                String msg = "Exception while loading session data";
                logger.log(Level.WARNING, msg, e);
                if (ApiProxy.getCurrentEnvironment() != null) {
                    ApiProxy.log(createWarningLogRecord(msg, e));
                }
                break;
            }
        }
        if (data != null) {
            if (System.currentTimeMillis() > data.getExpirationTime()) {
                logger.fine("Session " + sessionId + " expired " + ((System.currentTimeMillis() - data.getExpirationTime()) / 1000) + " seconds ago, ignoring.");
                return null;
            }
        }
        return data;
    }

    void deleteSession(String sessionId) {
        for (SessionStore sessionStore : sessionStoresInWriteOrder) {
            sessionStore.deleteSession(sessionId);
        }
    }

    private long getSessionExpirationInMilliseconds() {
        long seconds = defaultSessionTimeout;
        if (seconds < 0) {
            return Integer.MAX_VALUE * 1000L;
        } else {
            return seconds * 1000;
        }
    }

    private ApiProxy.LogRecord createWarningLogRecord(String message, Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println(message);
        if (ex != null) {
            ex.printStackTrace(printWriter);
        }

        return new ApiProxy.LogRecord(ApiProxy.LogRecord.Level.warn, System.currentTimeMillis() * 1000, stringWriter.toString());
    }
}
