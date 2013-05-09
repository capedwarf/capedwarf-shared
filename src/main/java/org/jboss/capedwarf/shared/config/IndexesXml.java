/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class IndexesXml implements Serializable {
    private static final long serialVersionUID = 1L;

    private ListMultimap<String, Index> indexes;

    public IndexesXml() {
        indexes = ArrayListMultimap.create();
    }

    void addIndex(Index index) {
        indexes.put(index.getKind(), index);
    }

    public ListMultimap<String, Index> getIndexes() {
        return ImmutableListMultimap.copyOf(indexes);
    }

    public static class Index implements Serializable {
        private static final long serialVersionUID = 1L;

        private String kind;
        private boolean ancestor;
        private String source;
        private List<Property> properties;

        public Index(String kind, boolean ancestor, String source) {
            this.kind = kind;
            this.ancestor = ancestor;
            this.source = source;
            this.properties = new ArrayList<Property>();
        }

        void addProperty(Property property) {
            if (!properties.contains(property)) {
                properties.add(property);
            }
        }

        public String getKind() {
            return kind;
        }

        public boolean isAncestor() {
            return ancestor;
        }

        public String getSource() {
            return source;
        }

        public List<Property> getProperties() {
            return Collections.unmodifiableList(properties);
        }

        public List<String> getPropertyNames() {
            List<String> list = new ArrayList<>();
            for (IndexesXml.Property property : properties) {
                list.add(property.getName());
            }
            return list;
        }

        public String getName() {
            StringBuilder sb = new StringBuilder();
            sb.append(kind);
            for (IndexesXml.Property property : properties) {
                sb.append(",").append(property.getName());
            }
            return sb.toString();
        }
    }

    public static class Property implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private String direction;

        public Property(String name, String direction) {
            this.name = name;
            this.direction = direction;
        }

        public String getName() {
            return name;
        }

        public String getDirection() {
            return direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Property property = (Property) o;

            if (!direction.equals(property.direction)) return false;
            if (!name.equals(property.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + direction.hashCode();
            return result;
        }
    }
}
