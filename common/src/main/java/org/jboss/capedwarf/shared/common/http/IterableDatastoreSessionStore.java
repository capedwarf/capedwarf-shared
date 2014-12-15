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

import static com.google.apphosting.runtime.SessionManagerUtil.deserialize;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.apphosting.runtime.SessionData;
import com.google.apphosting.runtime.jetty9.DatastoreSessionStore;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class IterableDatastoreSessionStore extends DatastoreSessionStore implements IterableSessionStore {
    static final String SESSION_ENTITY_TYPE = "_ah_SESSION";
    static final String EXPIRES_PROP = "_expires";
    static final String VALUES_PROP = "_values";

    public Map<String, SessionData> getAllSessions() {
        return getAllSessionsInternal();
    }

    static Map<String, SessionData> getAllSessionsInternal() {
        final String originalNamespace = NamespaceManager.get();
        NamespaceManager.set("");
        try {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery pq = ds.prepare(new Query(SESSION_ENTITY_TYPE));
            Map<String, SessionData> sessions = new HashMap<>();
            for (Entity entity : pq.asIterable()) {
                sessions.put(entity.getKey().getName(), createSessionFromEntity(entity));
            }
            return sessions;
        } finally {
            NamespaceManager.set(originalNamespace);
        }
    }

    @SuppressWarnings("unchecked")
    static SessionData createSessionFromEntity(Entity entity) {
        SessionData data = new SessionData();
        data.setExpirationTime((Long) entity.getProperty(EXPIRES_PROP));

        Blob valueBlob = (Blob) entity.getProperty(VALUES_PROP);
        data.setValueMap((Map<String, Object>) deserialize(valueBlob.getBytes()));
        return data;
    }
}
