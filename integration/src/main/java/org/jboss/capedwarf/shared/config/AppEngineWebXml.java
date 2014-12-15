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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.capedwarf.shared.modules.ModuleInfo;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class AppEngineWebXml implements Serializable {
    private static final long serialVersionUID = 1L;

    private String application;
    private String version;
    private boolean threadsafe;
    private String module = ModuleInfo.DEFAULT_MODULE_NAME;
    private InboundServices inboundServices;
    private String instanceClass;
    private Scaling scaling;
    private String publicRoot;

    private SessionType sessionType = SessionType.WILDFLY;
    private String sessionPersistenceQueueName = QueueXml.DEFAULT;

    private List<StaticFileInclude> staticFileIncludes;
    private List<FilePattern> staticFileExcludes;
    private List<AdminConsolePage> adminConsolePages;

    public AppEngineWebXml() {
        this.staticFileIncludes = new ArrayList<>();
        this.staticFileExcludes = new ArrayList<>();
        this.adminConsolePages = new ArrayList<>();
    }

    public static AppEngineWebXml override(AppEngineWebXml original, String application) {
        original.setApplication(application);
        return original;
    }

    public String getApplication() {
        return application;
    }

    void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public boolean isThreadsafe() {
        return threadsafe;
    }

    void setThreadsafe(boolean threadsafe) {
        this.threadsafe = threadsafe;
    }

    public String getModule() {
        return module;
    }

    void setModule(String module) {
        if (module != null) {
            this.module = module;
        }
    }

    public InboundServices getInboundServices() {
        return inboundServices;
    }

    void setInboundServices(InboundServices inboundServices) {
        this.inboundServices = inboundServices;
    }

    public boolean isInboundServiceEnabled(InboundServices.Service service) {
        return (inboundServices != null && inboundServices.getServices().contains(service));
    }

    public String getInstanceClass() {
        return instanceClass;
    }

    void setInstanceClass(String instanceClass) {
        this.instanceClass = instanceClass;
    }

    public Scaling getScaling() {
        return scaling;
    }

    void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }

    public List<StaticFileInclude> getStaticFileIncludes() {
        return Collections.unmodifiableList(staticFileIncludes);
    }

    void addStaticFileInclude(StaticFileInclude include) {
        staticFileIncludes.add(include);
    }

    public List<FilePattern> getStaticFileExcludes() {
        return Collections.unmodifiableList(staticFileExcludes);
    }

    void addStaticFileExclude(FilePattern exclude) {
        staticFileExcludes.add(exclude);
    }

    public String getPublicRoot() {
        return publicRoot;
    }

    public void setPublicRoot(String publicRoot) {
        this.publicRoot = publicRoot;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getSessionPersistenceQueueName() {
        return sessionPersistenceQueueName;
    }

    void setSessionPersistenceQueueName(String sessionPersistenceQueueName) {
        this.sessionPersistenceQueueName = sessionPersistenceQueueName;
    }

    void addAdminConsolePage(String name, String url) {
        adminConsolePages.add(new AdminConsolePage(name, url));
    }

    public List<AdminConsolePage> getAdminConsolePages() {
        return Collections.unmodifiableList(adminConsolePages);
    }
}
