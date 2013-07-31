/*
 *
 *  * JBoss, Home of Professional Open Source.
 *  * Copyright 2011, Red Hat, Inc., and individual contributors
 *  * as indicated by the @author tags. See the copyright.txt file in the
 *  * distribution for a full listing of individual contributors.
 *  *
 *  * This is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU Lesser General Public License as
 *  * published by the Free Software Foundation; either version 2.1 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This software is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this software; if not, write to the Free
 *  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.jboss.capedwarf.shared.config;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class AutomaticScaling extends Scaling {
    private String minIdleInstances;
    private String maxIdleInstances;
    private String minPendingLatency;
    private String maxPendingLatency;

    public Type getType() {
        return Type.AUTOMATIC;
    }

    public String getMinIdleInstances() {
        return minIdleInstances;
    }

    void setMinIdleInstances(String minIdleInstances) {
        this.minIdleInstances = minIdleInstances;
    }

    public String getMaxIdleInstances() {
        return maxIdleInstances;
    }

    void setMaxIdleInstances(String maxIdleInstances) {
        this.maxIdleInstances = maxIdleInstances;
    }

    public String getMinPendingLatency() {
        return minPendingLatency;
    }

    void setMinPendingLatency(String minPendingLatency) {
        this.minPendingLatency = minPendingLatency;
    }

    public String getMaxPendingLatency() {
        return maxPendingLatency;
    }

    void setMaxPendingLatency(String maxPendingLatency) {
        this.maxPendingLatency = maxPendingLatency;
    }
}
