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
import java.util.Properties;

import org.jboss.capedwarf.shared.config.CapedwarfConfiguration;
import org.jboss.capedwarf.shared.config.CapedwarfConfigurationParser;
import org.jboss.capedwarf.shared.config.CheckType;
import org.jboss.capedwarf.shared.config.InboundMailAccount;
import org.jboss.capedwarf.shared.config.OAuthConfiguration;
import org.jboss.capedwarf.shared.config.XmppConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        assertEquals(1, config.getAdmins().size());
        assertEquals("admin1@email.com", config.getAdmins().iterator().next());
    }

    private CapedwarfConfiguration parseConfig(String xml) throws Exception {
        CapedwarfConfiguration configuration = CapedwarfConfigurationParser.parse(new ByteArrayInputStream(xml.getBytes()));
        testSerializable(configuration);
        return configuration;
    }

    private void testSerializable(Object object) throws Exception {
        assertNotNull(object);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(object);
        }
        Object clone;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            clone = in.readObject();
        }
        assertTrue(object.getClass().isInstance(clone));
    }

    @Test
    public void testParseMultipleAdmins() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <admin>admin1@email.com</admin>" +
                "    <admin>admin2@email.com</admin>" +
                "    <admin>admin3@email.com</admin>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);

        assertEquals(3, config.getAdmins().size());
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

        assertEquals("xmppHost", xmppConfig.getHost());
        assertEquals(1234, xmppConfig.getPort());
        assertEquals("xmppUser", xmppConfig.getUsername());
        assertEquals("xmppPass", xmppConfig.getPassword());
    }

    @Test
    public void testParseInboundMailConfiguration() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <inbound-mail>" +
                "        <protocol>imaps</protocol>" +
                "        <host>localhost</host>" +
                "        <user>MailUser</user>" +
                "        <password>MailPass</password>" +
                "        <folder>SomeFolder</folder>" +
                "        <pollingInterval>5000</pollingInterval>" +
                "    </inbound-mail>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        List<InboundMailAccount> mailAccounts = config.getInboundMailAccounts();
        assertNotNull(mailAccounts);
        assertEquals(1, mailAccounts.size());

        InboundMailAccount ima = mailAccounts.get(0);
        assertEquals("imaps", ima.getProtocol());
        assertEquals("localhost", ima.getHost());
        assertEquals("MailUser", ima.getUser());
        assertEquals("MailPass", ima.getPassword());
        assertEquals("SomeFolder", ima.getFolder());
        assertEquals(5000L, ima.getPollingInterval());
    }

    @Test
    public void testParseMailConfiguration() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "    <mail>" +
                "        <property name=\"mail.transport.protocol\">smtp</property>" +
                "        <property name=\"mail.smtp.auth\">true</property>" +
                "        <property name=\"mail.smtp.starttls.enable\">true</property>" +
                "        <property name=\"mail.smtp.host\">smtp.gmail.com</property>" +
                "        <property name=\"mail.smtp.port\">587</property>" +
                "        <property name=\"mail.smtp.user\">user</property>" +
                "        <property name=\"mail.smtp.password\">password</property>" +
                "    </mail>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        Properties properties = config.getMailProperties();
        assertNotNull(properties);
        assertEquals(7, properties.size());

        assertEquals("smtp", properties.get("mail.transport.protocol"));
        assertEquals("true", properties.get("mail.smtp.auth"));
        assertEquals("smtp.gmail.com", properties.get("mail.smtp.host"));
        assertEquals("587", properties.get("mail.smtp.port"));
        assertEquals("user", properties.get("mail.smtp.user"));
        assertEquals("password", properties.get("mail.smtp.password"));
    }

    @Test
    public void testGlobalTimeLimit() throws Exception {
        String xml = "<capedwarf-web-app>" +
                "   <global-time-limit>YES</global-time-limit>" +
                "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        assertEquals(CheckType.YES, config.getCheckGlobalTimeLimit());
    }

    @Test
    public void testSystemPropertyReplacement() throws Exception {
        String xml = "<capedwarf-web-app>" +
            "   <global-time-limit>${cd.gtl:DYNAMIC}</global-time-limit>" +
            "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        assertEquals(CheckType.DYNAMIC, config.getCheckGlobalTimeLimit());

        System.setProperty("cd.gtl", "YES");
        try {
            config = parseConfig(xml);
            assertEquals(CheckType.YES, config.getCheckGlobalTimeLimit());
        } finally {
            System.clearProperty("cd.gtl");
        }
    }

    @Test
    public void testParseOAuth() throws Exception {
        String xml = "<capedwarf-web-app>" +
            "    <oauth>" +
            "        <clientId>clientId123</clientId>" +
            "        <clientSecret>clientSecret123</clientSecret>" +
            "    </oauth>" +
            "</capedwarf-web-app>";

        CapedwarfConfiguration config = parseConfig(xml);
        OAuthConfiguration oauth = config.getOAuthConfiguration();
        assertNotNull(oauth);
        assertEquals("clientId123", oauth.getClientId());
        assertEquals("clientSecret123", oauth.getClientSecret());
    }
}
