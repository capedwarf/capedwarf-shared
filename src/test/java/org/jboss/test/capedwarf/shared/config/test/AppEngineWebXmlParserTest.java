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
import org.jboss.capedwarf.shared.config.AutomaticScaling;
import org.jboss.capedwarf.shared.config.BasicScaling;
import org.jboss.capedwarf.shared.config.FilePattern;
import org.jboss.capedwarf.shared.config.InboundServices;
import org.jboss.capedwarf.shared.config.ManualScaling;
import org.jboss.capedwarf.shared.config.Scaling;
import org.jboss.capedwarf.shared.config.StaticFileInclude;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class AppEngineWebXmlParserTest {

    @Test
    public void testParse() throws Exception {
        String xml = "<appengine-web-app>" +
            "    <application>appName</application>" +
            "    <version>2</version>" +
            "    <threadsafe>true</threadsafe>" +
            "    <module>somemodule</module>" +
            "    <instance-class>B8</instance-class>" +
            "</appengine-web-app>";

        AppEngineWebXml appEngineWebXml = parse(xml);

        Assert.assertEquals("appName", appEngineWebXml.getApplication());
        Assert.assertEquals("2", appEngineWebXml.getVersion());
        Assert.assertTrue(appEngineWebXml.isThreadsafe());
        Assert.assertEquals("somemodule", appEngineWebXml.getModule());
        Assert.assertEquals("B8", appEngineWebXml.getInstanceClass());
    }

    @Test
    public void testManualScaling() throws Exception {
        String xml = "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n" +
            "  <application>simple-app</application>\n" +
            "  <module>default</module>\n" +
            "  <version>uno</version>\n" +
            "  <instance-class>B8</instance-class>\n" +
            "  <manual-scaling>\n" +
            "    <instances>5</instances>\n" +
            "  </manual-scaling>\n" +
            "</appengine-web-app>";
        AppEngineWebXml appEngineWebXml = parse(xml);

        Scaling s = appEngineWebXml.getScaling();
        Assert.assertNotNull(s);
        Assert.assertSame(Scaling.Type.MANUAL, s.getType());
        Assert.assertEquals(5, s.narrow(ManualScaling.class).getInstances());
    }

    @Test
    public void testBasicScaling() throws Exception {
        String xml = "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n" +
            "  <application>simple-app</application>\n" +
            "  <module>default</module>\n" +
            "  <version>uno</version>\n" +
            "  <instance-class>B8</instance-class>\n" +
            "  <basic-scaling>\n" +
            "    <max-instances>11</max-instances>\n" +
            "    <idle-timeout>10m</idle-timeout>\n" +
            "  </basic-scaling>\n" +
            "</appengine-web-app>";
        AppEngineWebXml appEngineWebXml = parse(xml);

        Scaling s = appEngineWebXml.getScaling();
        Assert.assertNotNull(s);
        Assert.assertSame(Scaling.Type.BASIC, s.getType());
        BasicScaling scaling = s.narrow(BasicScaling.class);
        Assert.assertEquals(11, scaling.getMaxInstances());
        Assert.assertEquals("10m", scaling.getIdleTimeout());
    }

    @Test
    public void testAutoScaling() throws Exception {
        String xml = "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n" +
            "  <application>simple-app</application>\n" +
            "  <module>default</module>\n" +
            "  <version>uno</version>\n" +
            "  <instance-class>F2</instance-class>\n" +
            "  <automatic-scaling>\n" +
            "    <min-idle-instances>5</min-idle-instances>\n" +
            "    <max-idle-instances>automatic</max-idle-instances>\n" +
            "    <min-pending-latency>automatic</min-pending-latency>\n" +
            "    <max-pending-latency>30ms</max-pending-latency>\n" +
            "  </automatic-scaling>\n" +
            "</appengine-web-app>";
        AppEngineWebXml appEngineWebXml = parse(xml);

        Scaling s = appEngineWebXml.getScaling();
        Assert.assertNotNull(s);
        Assert.assertSame(Scaling.Type.AUTOMATIC, s.getType());
        AutomaticScaling scaling = s.narrow(AutomaticScaling.class);
        Assert.assertEquals("5", scaling.getMinIdleInstances());
        Assert.assertEquals("automatic", scaling.getMaxIdleInstances());
        Assert.assertEquals("automatic", scaling.getMinPendingLatency());
        Assert.assertEquals("30ms", scaling.getMaxPendingLatency());
    }

    @Test
    public void testStaticFiles() throws Exception {
        String xml = "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n" +
            "    <application>capedwarf-test</application>\n" +
            "    <version>1</version>\n" +
            "    <threadsafe>true</threadsafe>\n" +
            "    <static-files>\n" +
            "        <include path=\"/**.png\" />\n" +
            "        <include path=\"/**.jpg\" expiration=\"1d 2h 3m 4s\"/>\n" +
            "        <exclude path=\"/**.jpg\" />\n" +
            "    </static-files>\n" +
            "</appengine-web-app>\n";
        AppEngineWebXml appEngineWebXml = parse(xml);

        List<StaticFileInclude> includes = appEngineWebXml.getStaticFileIncludes();
        Assert.assertEquals(2, includes.size());
        Assert.assertEquals("1d 2h 3m 4s", includes.get(1).getExpiration());
        Assert.assertEquals(Long.valueOf(93784), includes.get(1).getExpirationSeconds());
        List<FilePattern> excludes = appEngineWebXml.getStaticFileExcludes();
        Assert.assertEquals(1, excludes.size());
    }

    @Test
    public void testSystemProperties() throws Exception {
        String xml = "<appengine-web-app>" +
            "    <application>appName</application>" +
            "    <version>2</version>" +
            "    <system-properties>" +
            "        <property name=\"foo\" value=\"bar\"/>" +
            "    </system-properties>" +
            "</appengine-web-app>";

        parse(xml);

        assertEquals("bar", System.getProperty("foo"));
    }

    @Test
    public void testInboundServices() throws Exception {
        String xml = "<appengine-web-app>" +
            "    <application>appName</application>" +
            "    <version>2</version>" +
            "    <inbound-services>" +
            "        <service>mail</service>" +
            "    </inbound-services>" +
            "</appengine-web-app>";

        AppEngineWebXml aewx = parse(xml);

        assertNotNull(aewx.getInboundServices());
        assertNotNull(aewx.getInboundServices().getServices());
        assertEquals(1, aewx.getInboundServices().getServices().size());
        assertEquals(InboundServices.Service.mail, aewx.getInboundServices().getServices().iterator().next());
        assertTrue(aewx.isInboundServiceEnabled(InboundServices.Service.mail));
        assertFalse(aewx.isInboundServiceEnabled(InboundServices.Service.channel_presence));
    }

    @Test
    public void testPublicRoot() throws Exception {
        String xml = "<appengine-web-app>" +
            "    <application>appName</application>" +
            "    <version>2</version>" +
            "    <public-root>/static</public-root>" +
            "</appengine-web-app>";

        AppEngineWebXml aewx = parse(xml);
        assertEquals("/static", aewx.getPublicRoot());
    }

    private static AppEngineWebXml parse(String xml) throws Exception {
        return AppEngineWebXmlParser.parse(new ByteArrayInputStream(xml.getBytes()));
    }
}
