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

import java.util.Set;

import com.google.apphosting.runtime.SessionData;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class AppEngineSession implements Session {
    private final AppEngineSessionManager sessionManager;
    private final SessionConfig sessionConfig;
    private String id;
    private SessionData sessionData;
    private final long created;
    private long accessed;

    private boolean dirty;

    public AppEngineSession(AppEngineSessionManager sessionManager, SessionConfig sessionConfig, String id, long created, long accessed) {
        this.sessionManager = sessionManager;
        this.sessionConfig = sessionConfig;
        this.id = id;
        this.sessionData = sessionManager.createSession(id);
        this.created = created;
        this.accessed = accessed;
    }

    public AppEngineSession(AppEngineSessionManager sessionManager, SessionConfig sessionConfig, String id, SessionData sessionData, long created, long accessed) {
        this.sessionManager = sessionManager;
        this.sessionConfig = sessionConfig;
        this.id = id;
        this.sessionData = sessionData;
        this.created = created;
        this.accessed = accessed;
    }

    void updateAccessed() {
        accessed = System.currentTimeMillis();
    }

    boolean isDirty() {
        return dirty;
    }

    void save() {
        sessionManager.saveSession(id, sessionData);
    }

    public String getId() {
        return id;
    }

    public void requestDone(HttpServerExchange serverExchange) {
        // TODO
    }

    public long getCreationTime() {
        return created;
    }

    public long getLastAccessedTime() {
        return accessed;
    }

    public void setMaxInactiveInterval(int interval) {
        sessionData.setExpirationTime(accessed + interval);
        dirty = true;
    }

    public int getMaxInactiveInterval() {
        return (int) (sessionData.getExpirationTime() - accessed);
    }

    public Object getAttribute(String name) {
        return sessionData.getValueMap().get(name);
    }

    public Set<String> getAttributeNames() {
        return sessionData.getValueMap().keySet();
    }

    public Object setAttribute(String name, Object value) {
        Object put = sessionData.getValueMap().put(name, value);
        if (put != null) {
            sessionManager.getSessionListeners().attributeUpdated(this, name, value, put);
        } else {
            sessionManager.getSessionListeners().attributeAdded(this, name, value);
        }
        dirty = true;
        return put;
    }

    public Object removeAttribute(String name) {
        Object remove = sessionData.getValueMap().remove(name);
        sessionManager.getSessionListeners().attributeRemoved(this, name, remove);
        dirty = true;
        return remove;
    }

    public void invalidate(HttpServerExchange exchange) {
        sessionManager.deleteSession(id);
        sessionManager.getSessionListeners().sessionDestroyed(this, exchange, SessionListener.SessionDestroyedReason.INVALIDATED);
        if (exchange != null && sessionConfig != null) {
            sessionConfig.clearSession(exchange, id);
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
        String oldId = id;
        id = sessionManager.createId(exchange, config);
        save();
        sessionManager.getSessionListeners().sessionIdChanged(this, oldId);
        dirty = true;
        return id;
    }
}
