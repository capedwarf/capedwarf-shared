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

package org.jboss.capedwarf.shared.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.jboss.capedwarf.shared.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CapedwarfConfigurationParser {
    public static CapedwarfConfiguration parse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return new CapedwarfConfiguration();
        }
        try {
            return tryParse(inputStream);
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static CapedwarfConfiguration tryParse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        Document doc = XmlUtils.parseXml(inputStream);
        Element documentElement = doc.getDocumentElement();

        CapedwarfConfiguration config = new CapedwarfConfiguration();

        parseAdmins(documentElement, config);
        parseXmppConfig(documentElement, config);
        parseInboundMail(documentElement, config);
        parseGlobalTimeLimit(documentElement, config);
        parseMail(documentElement, config);
        parseOAuth(documentElement, config);

        return config;
    }

    private static void parseAdmins(Element documentElement, CapedwarfConfiguration config) {
        for (Element adminElem : XmlUtils.getChildren(documentElement, "admin")) {
            config.addAdmin(XmlUtils.getBody(adminElem));
        }
    }

    private static void parseXmppConfig(Element documentElement, CapedwarfConfiguration config) {
        XmppConfiguration xmppConfig = config.getXmppConfiguration();
        Element xmppElem = XmlUtils.getChildElement(documentElement, "xmpp");
        if (xmppElem != null) {
            xmppConfig.setHost(XmlUtils.getChildElementBody(xmppElem, "host"));
            xmppConfig.setPort(Integer.parseInt(XmlUtils.getChildElementBody(xmppElem, "port")));
            xmppConfig.setUsername(XmlUtils.getChildElementBody(xmppElem, "username"));
            xmppConfig.setPassword(XmlUtils.getChildElementBody(xmppElem, "password"));
        }
    }

    private static void parseInboundMail(Element documentElement, CapedwarfConfiguration config) {
        for (Element inboundMailElem : XmlUtils.getChildren(documentElement, "inbound-mail")) {
            config.addInboundMailAccount(getInboundMailAccount(inboundMailElem));
        }
    }

    private static InboundMailAccount getInboundMailAccount(Element elem) {
        String pollingInterval = XmlUtils.getChildElementBody(elem, "pollingInterval", false);
        return new InboundMailAccount(
            XmlUtils.getChildElementBody(elem, "protocol", true),
            XmlUtils.getChildElementBody(elem, "host", true),
            XmlUtils.getChildElementBody(elem, "user", true),
            XmlUtils.getChildElementBody(elem, "password", true),
            XmlUtils.getChildElementBody(elem, "folder", true),
            pollingInterval == null ? null : Long.valueOf(pollingInterval)
        );
    }

    private static void parseGlobalTimeLimit(Element documentElement, CapedwarfConfiguration config) {
        Element globalTimeLimit = XmlUtils.getChildElement(documentElement, "global-time-limit");
        if (globalTimeLimit != null) {
            config.setCheckGlobalTimeLimit(CheckType.valueOf(XmlUtils.getBody(globalTimeLimit)));
        }
    }

    private static void parseMail(Element documentElement, CapedwarfConfiguration config) {
        Element mailElem = XmlUtils.getChildElement(documentElement, "mail");
        if (mailElem != null) {
            for (Element propertyElem : XmlUtils.getChildren(mailElem, "property")) {
                config.getMailProperties().put(XmlUtils.getAttribute(propertyElem, "name"), XmlUtils.getBody(propertyElem));
            }
        }
    }

    private static void parseOAuth(Element documentElement, CapedwarfConfiguration config) {
        Element oauthElem = XmlUtils.getChildElement(documentElement, "oauth");
        if (oauthElem != null) {
            config.getOAuthConfiguration().setClientId(XmlUtils.getChildElementBody(oauthElem, "clientId", true));
            config.getOAuthConfiguration().setClientSecret(XmlUtils.getChildElementBody(oauthElem, "clientSecret", true));
        }
    }
}
