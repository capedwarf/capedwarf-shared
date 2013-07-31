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
import java.util.List;

import org.jboss.capedwarf.shared.config.AppEngineWebXml;
import org.jboss.capedwarf.shared.config.AppEngineWebXmlParser;
import org.jboss.capedwarf.shared.config.IndexesXml;
import org.jboss.capedwarf.shared.config.IndexesXmlParser;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class IndexesXmlParserTest {

    @Test
    public void testParse() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<datastore-indexes\n" +
            "  autoGenerate=\"false\">\n" +
            "    <datastore-index kind=\"Employee\" ancestor=\"false\">\n" +
            "        <property name=\"lastName\" direction=\"asc\" />\n" +
            "        <property name=\"hireDate\" direction=\"desc\" />\n" +
            "    </datastore-index>\n" +
            "    <datastore-index kind=\"Project\" ancestor=\"false\">\n" +
            "        <property name=\"dueDate\" direction=\"asc\" />\n" +
            "    </datastore-index>\n" +
            "</datastore-indexes>";

        IndexesXml indexesXml = IndexesXmlParser.parse(new ByteArrayInputStream(xml.getBytes()));

        Assert.assertFalse(indexesXml.isAutoGenerate());
        Assert.assertEquals(2, indexesXml.getIndexes().size());
        List<IndexesXml.Index> ei = indexesXml.getIndexes().get("Employee");
        Assert.assertEquals(1, ei.size());
        IndexesXml.Index index = ei.get(0);
        Assert.assertFalse(index.isAncestor());
        List<IndexesXml.Property> properties = index.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(2, properties.size());
        Assert.assertEquals("lastName", properties.get(0).getName());
        Assert.assertEquals("hireDate", properties.get(1).getName());
    }
}
