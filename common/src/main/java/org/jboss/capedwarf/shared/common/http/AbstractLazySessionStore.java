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

import java.util.Map;

import com.google.apphosting.runtime.SessionData;
import com.google.apphosting.runtime.SessionStore;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractLazySessionStore implements SessionStore {
    private volatile SessionStore delegate;

    protected abstract SessionStore createDelegate();

    private SessionStore getDelegate() {
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    delegate = createDelegate();
                }
            }
        }
        return delegate;
    }

    public Map<String, SessionData> getAllSessions() {
        return getDelegate().getAllSessions();
    }

    public SessionData getSession(String key) {
        return getDelegate().getSession(key);
    }

    public void saveSession(String key, SessionData sessionData) throws SessionStore.Retryable {
        getDelegate().saveSession(key, sessionData);
    }

    public void deleteSession(String key) {
        getDelegate().deleteSession(key);
    }
}
