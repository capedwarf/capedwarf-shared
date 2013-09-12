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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class QueueXml implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String INTERNAL = "CAPEDWARF-INTERNAL";
    public static final String DEFAULT = "default";

    private static final Queue INTERNAL_QUEUE = new Queue(INTERNAL, Mode.PUSH);
    private static final Queue DEFAULT_QUEUE = new Queue(DEFAULT, Mode.PUSH);

    private Map<String, Queue> queues;

    public QueueXml() {
        queues = new HashMap<>();
        queues.put(INTERNAL, INTERNAL_QUEUE);
        queues.put(DEFAULT, DEFAULT_QUEUE);
    }

    void addQueue(String name, Mode mode) {
        queues.put(name, new Queue(name, mode));
    }

    public Map<String, Queue> getQueues() {
        return Collections.unmodifiableMap(queues);
    }

    public static class Queue implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private Mode mode;

        private Queue(String name, Mode mode) {
            this.name = name;
            if (mode == null)
                mode = Mode.PUSH;
            this.mode = mode;
        }

        public String getName() {
            return name;
        }

        public Mode getMode() {
            return mode;
        }
    }

    public static enum Mode {
        PULL, PUSH
    }
}
