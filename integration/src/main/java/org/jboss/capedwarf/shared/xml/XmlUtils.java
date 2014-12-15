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

package org.jboss.capedwarf.shared.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.util.StringPropertyReplacer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class XmlUtils {
    public static Document parseXml(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputStream);
        doc.getDocumentElement().normalize();
        return doc;
    }

    public static String getChildElementBody(Element element, String tagName) {
        return getChildElementBody(element, tagName, true);
    }

    public static String getChildElementBody(Element element, String tagName, boolean required) {
        Element elt = getChildElement(element, tagName, required);
        return (elt != null) ? getBody(elt) : null;
    }

    public static Element getChildElement(Element parent, String tagName) {
        return getChildElement(parent, tagName, false);
    }

    public static Element getChildElement(Element parent, String tagName, boolean required) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes == null || nodes.getLength() == 0) {
            if (required) {
                throw new IllegalStateException(String.format("Missing tag %s in element %s.", tagName, parent));
            } else {
                return null;
            }
        }
        return (Element) nodes.item(0);
    }

    public static String replace(String value) {
        return StringPropertyReplacer.replaceProperties(value);
    }

    public static String getAttribute(Element element, String name) {
        String value = element.getAttribute(name);
        return replace(value);
    }

    public static String getBody(Element element) {
        NodeList nodes = element.getChildNodes();
        if (nodes == null || nodes.getLength() == 0)
            return null;

        Node firstNode = nodes.item(0);
        if (firstNode == null)
            return null;

        String nodeValue = firstNode.getNodeValue();
        return replace(nodeValue);
    }

    public static List<Element> getChildren(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);

        List<Element> elements = new ArrayList<>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            elements.add((Element) nodes.item(i));
        }
        return elements;
    }
}
