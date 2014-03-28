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

package org.jboss.test.capedwarf.shared.config.test;


import java.io.ByteArrayInputStream;
import java.util.List;

import org.jboss.capedwarf.shared.config.CronEntry;
import org.jboss.capedwarf.shared.config.CronXml;
import org.jboss.capedwarf.shared.config.CronXmlParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CronXmlParserTest {

    @Test
    public void testParse() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<cronentries>\n" +
            "  <cron>\n" +
            "    <url>/recache</url>\n" +
            "    <description>Repopulate the cache every 2 minutes</description>\n" +
            "    <schedule>every 2 minutes</schedule>\n" +
            "  </cron>\n" +
            "  <cron>\n" +
            "    <url>/weeklyreport</url>\n" +
            "    <description>Mail out a weekly report</description>\n" +
            "    <schedule>every monday 08:30</schedule>\n" +
            "    <timezone>America/New_York</timezone>\n" +
            "  </cron>\n" +
            "  <cron>\n" +
            "    <url>/weeklyreport</url>\n" +
            "    <description>Mail out a weekly report</description>\n" +
            "    <schedule>every monday 08:30</schedule>\n" +
            "    <timezone>America/New_York</timezone>\n" +
            "    <target>version-2</target>\n" +
            "  </cron>\n" +
            "</cronentries>";

        CronXml cronXml = CronXmlParser.parse(new ByteArrayInputStream(xml.getBytes()));
        Assert.assertNotNull(cronXml);

        List<CronEntry> entries = cronXml.getEntries();
        Assert.assertNotNull(entries);
        Assert.assertEquals(3, entries.size());

        CronEntry ce1 = entries.get(0);
        Assert.assertEquals("/recache", ce1.getUrl());
        Assert.assertEquals("Repopulate the cache every 2 minutes", ce1.getDescription());
        Assert.assertEquals("every 2 minutes", ce1.getSchedule());

        CronEntry ce2 = entries.get(1);
        Assert.assertEquals("/weeklyreport", ce2.getUrl());
        Assert.assertEquals("Mail out a weekly report", ce2.getDescription());
        Assert.assertEquals("every monday 08:30", ce2.getSchedule());
        Assert.assertEquals("America/New_York", ce2.getTimezone());

        CronEntry ce3 = entries.get(2);
        Assert.assertEquals("/weeklyreport", ce3.getUrl());
        Assert.assertEquals("Mail out a weekly report", ce3.getDescription());
        Assert.assertEquals("every monday 08:30", ce3.getSchedule());
        Assert.assertEquals("America/New_York", ce3.getTimezone());
        Assert.assertEquals("version-2", ce3.getTarget());
    }
}
