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

package org.jboss.capedwarf.shared.datastore;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.hibernate.search.backend.spi.Work;
import org.hibernate.search.backend.spi.WorkType;
import org.infinispan.query.backend.SearchWorkCreator;
import org.jboss.capedwarf.shared.util.Utils;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CapedwarfMultiSearchWorkCreator implements SearchWorkCreator<Object> {
    private Map<ClassLoader, SearchWorkCreator> delegates = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    protected synchronized SearchWorkCreator<Object>getDelegate() {
        ClassLoader tccl = Utils.getAppClassLoader();
        SearchWorkCreator<Object> delegate = delegates.get(tccl);
        if (delegate == null) {
            delegate = Utils.newInstance(SearchWorkCreator.class, tccl, "org.jboss.capedwarf.datastore.CapedwarfSearchWorkCreator");
            delegates.put(tccl, delegate);
        }
        return delegate;
    }

    public Collection<Work> createPerEntityTypeWorks(Class<Object> entityType, WorkType workType) {
        return getDelegate().createPerEntityTypeWorks(entityType, workType);
    }

    public Collection<Work> createPerEntityWorks(Object entity, Serializable id, WorkType workType) {
        return getDelegate().createPerEntityWorks(entity, id, workType);
    }
}

