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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CronXmlParser {
    private static final String CRONENTRIES_TAG = "cronentries";
    private static final String CRON_TAG = "cron";

    public static CronXml parse(InputStream is) {
        if (is == null) {
            return new CronXml();
        }

        final CronXml cronXml = new CronXml();
        try {
            Document doc = XmlUtils.parseXml(is);

            final Element documentElement = doc.getDocumentElement();
            if (CRONENTRIES_TAG.equals(documentElement.getTagName()) == false) {
                throw new CapedwarfConfigException("cron.xml does not contain <cronentries>");
            }
            NodeList list = documentElement.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (CRON_TAG.equals(element.getTagName())) {
                        parseCronTag(cronXml, element);
                    }
                }
            }
            return cronXml;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new CapedwarfConfigException("Could not parse WEB-INF/cron.xml", e);
        }
    }

    private static void parseCronTag(CronXml cronXml, Element element) {
        String url = XmlUtils.getChildElementBody(element, "url");
        String description = XmlUtils.getChildElementBody(element, "description");
        String schedule = XmlUtils.getChildElementBody(element, "schedule");
        String timezone = XmlUtils.getChildElementBody(element, "timezone");
        String target = XmlUtils.getChildElementBody(element, "target");
        CronEntry entry = new CronEntry(url, description, schedule, timezone, target);
        cronXml.addEntry(entry);
    }
}
