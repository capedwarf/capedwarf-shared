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

package org.jboss.capedwarf.shared.undertow;

import javax.servlet.ServletContext;

import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.capedwarf.shared.components.ComponentRegistry;
import org.jboss.capedwarf.shared.components.Key;
import org.jboss.capedwarf.shared.components.SimpleKey;
import org.jboss.capedwarf.shared.config.ApplicationConfiguration;
import org.jboss.capedwarf.shared.config.ConfigurationAware;
import org.kohsuke.MetaInfServices;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@MetaInfServices
public class CapedwarfServletExtension implements ServletExtension {
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext context) {
        // grab configuration
        ApplicationConfiguration configuration = ConfigurationAware.getApplicationConfiguration();
        String appId = configuration.getAppEngineWebXml().getApplication();
        String module = configuration.getAppEngineWebXml().getModule();

        // register servlet context as early as possible
        Key<ServletContext> key = new SimpleKey<>(appId, module, ServletContext.class);
        ComponentRegistry.getInstance().setComponent(key, context);

        // allow for proxy wrappers
        deploymentInfo.setAllowNonStandardWrappers(true);
    }
}
