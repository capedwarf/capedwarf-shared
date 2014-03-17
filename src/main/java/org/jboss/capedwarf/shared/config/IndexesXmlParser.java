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
public class IndexesXmlParser {
    private static final String DATASTORE_INDEXES = "datastore-indexes";
    private static final String DATASTORE_INDEX = "datastore-index";

    private static final String KIND = "kind";
    private static final String ANCESTOR = "ancestor";
    private static final String SOURCE = "source";

    private static final String PROPERTY = "property";
    private static final String NAME = "name";
    private static final String DIRECTION = "direction";

    public static IndexesXml parse(InputStream is) {
        final IndexesXml indexesXml = new IndexesXml();

        if (is == null) {
            return indexesXml;
        }

        try {
            Document doc = XmlUtils.parseXml(is);

            Element root = doc.getDocumentElement();
            if (DATASTORE_INDEXES.equals(root.getTagName()) == false) {
                throw new CapedwarfConfigException("datastore-indexes.xml does not contain <datastore-indexes>");
            }

            String autoGenerate = XmlUtils.getAttribute(root, "autoGenerate");
            if (autoGenerate != null) {
                indexesXml.setAutoGenerate(Boolean.parseBoolean(autoGenerate));
            }

            NodeList list = root.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (DATASTORE_INDEX.equals(element.getTagName())) {
                        parseIndexTag(indexesXml, element);
                    }
                }
            }
            return indexesXml;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new CapedwarfConfigException("Could not parse WEB-INF/datastore-indexes.xml", e);
        }
    }

    private static void parseIndexTag(IndexesXml indexesXml, Element indexElt) {
        String kind = getAttribute(indexElt, KIND);
        boolean ancestor = Boolean.parseBoolean(getAttribute(indexElt, ANCESTOR));
        String source = getAttribute(indexElt, SOURCE);
        IndexesXml.Index index = new IndexesXml.Index(kind, ancestor, source);
        for (Element propElt : XmlUtils.getChildren(indexElt, PROPERTY)) {
            String name = getAttribute(propElt, NAME);
            String direction = getAttribute(propElt, DIRECTION);
            index.addProperty(new IndexesXml.Property(name, direction));
        }
        indexesXml.addIndex(index);
    }

    private static String getAttribute(Element elt, String name) {
        String atttribute = XmlUtils.getAttribute(elt, name);
        if (atttribute == null) {
            throw new IllegalArgumentException("Attribute is null: " + name);
        }
        return atttribute;
    }
}
