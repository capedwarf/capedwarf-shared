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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 */
public class CapedwarfConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<String> admins = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private XmppConfiguration xmppConfiguration = new XmppConfiguration();
    private List<InboundMailAccount> inboundMailAccounts = new ArrayList<>();

    private Properties mailProperties = new Properties();

    private CheckType checkGlobalTimeLimit = CheckType.NO;

    public CapedwarfConfiguration() {
    }

    protected CapedwarfConfiguration(Set<String> admins, List<InboundMailAccount> inboundMailAccounts) {
        this.admins.addAll(admins);
        this.inboundMailAccounts = inboundMailAccounts;
    }

    void addAdmin(String email) {
        admins.add(email.toLowerCase());
    }

    public Set<String> getAdmins() {
        return Collections.unmodifiableSet(admins);
    }

    public boolean isAdmin(String email) {
        return admins.contains(email.toLowerCase());
    }

    public XmppConfiguration getXmppConfiguration() {
        return xmppConfiguration;
    }

    public List<InboundMailAccount> getInboundMailAccounts() {
        return Collections.unmodifiableList(inboundMailAccounts);
    }

    void addInboundMailAccount(InboundMailAccount account) {
        inboundMailAccounts.add(account);
    }

    public Properties getMailProperties() {
        return mailProperties;
    }

    public CheckType getCheckGlobalTimeLimit() {
        return checkGlobalTimeLimit;
    }

    void setCheckGlobalTimeLimit(CheckType checkGlobalTimeLimit) {
        this.checkGlobalTimeLimit = checkGlobalTimeLimit;
    }
}
