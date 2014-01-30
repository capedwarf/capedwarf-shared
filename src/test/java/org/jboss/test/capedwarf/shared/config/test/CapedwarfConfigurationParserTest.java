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

package org.jboss.test.capedwarf.shared.config.test;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.jboss.capedwarf.shared.config.CapedwarfConfiguration;
import org.jboss.capedwarf.shared.config.CapedwarfConfigurationParser;
import org.jboss.capedwarf.shared.config.CheckType;
import org.jboss.capedwarf.shared.config.InboundMailAccount;
import org.jboss.capedwarf.shared.config.XmppConfiguration;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 */
public class CapedwarfConfigurationParserTest {

    @Test
    public void testParseSingleAdmin() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <admin>admin1@email.com</admin>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);

        Assert.assertEquals(1, config.getAdmins().size());
        Assert.assertEquals("admin1@email.com", config.getAdmins().iterator().next());
    }

    private CapedwarfConfiguration parseConfig(String xml) throws Exception {
        CapedwarfConfiguration configuration = CapedwarfConfigurationParser.parse(new ByteArrayInputStream(xml.getBytes()));
        testSerializable(configuration);
        return configuration;
    }

    private void testSerializable(Object object) throws Exception {
        Assert.assertNotNull(object);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(object);
        }
        Object clone;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            clone = in.readObject();
        }
        Assert.assertTrue(object.getClass().isInstance(clone));
    }

    @Test
    public void testParseMultipleAdmins() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <admin>admin1@email.com</admin>" +
                "    <admin>admin2@email.com</admin>" +
                "    <admin>admin3@email.com</admin>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);

        Assert.assertEquals(3, config.getAdmins().size());
        assertTrue(config.getAdmins().contains("admin1@email.com"));
        assertTrue(config.getAdmins().contains("admin2@email.com"));
        assertTrue(config.getAdmins().contains("admin3@email.com"));
    }

    @Test
    public void testParseXmppConfiguration() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <xmpp>" +
                "        <host>xmppHost</host>" +
                "        <port>1234</port>" +
                "        <username>xmppUser</username>" +
                "        <password>xmppPass</password>" +
                "    </xmpp>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        XmppConfiguration xmppConfig = config.getXmppConfiguration();

        Assert.assertEquals("xmppHost", xmppConfig.getHost());
        Assert.assertEquals(1234, xmppConfig.getPort());
        Assert.assertEquals("xmppUser", xmppConfig.getUsername());
        Assert.assertEquals("xmppPass", xmppConfig.getPassword());
    }

    @Test
    public void testParseMailConfiguration() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <inbound-mail>" +
                "        <protocol>imaps</protocol>" +
                "        <host>localhost</host>" +
                "        <user>MailUser</user>" +
                "        <password>MailPass</password>" +
                "        <folder>SomeFolder</folder>" +
                "    </inbound-mail>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        List<InboundMailAccount> mailAccounts = config.getInboundMailAccounts();
        Assert.assertNotNull(mailAccounts);
        Assert.assertEquals(1, mailAccounts.size());

        InboundMailAccount ima = mailAccounts.get(0);
        Assert.assertEquals("imaps", ima.getProtocol());
        Assert.assertEquals("localhost", ima.getHost());
        Assert.assertEquals("MailUser", ima.getUser());
        Assert.assertEquals("MailPass", ima.getPassword());
        Assert.assertEquals("SomeFolder", ima.getFolder());
        Assert.assertEquals(60000L, ima.getPollingInterval());
    }

    @Test
    public void testGlobaltTimeLimit() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "   <global-time-limit>YES</global-time-limit>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        Assert.assertEquals(CheckType.YES, config.getCheckGlobalTimeLimit());
    }
}
