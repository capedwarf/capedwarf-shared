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

package org.jboss.capedwarf.shared.components;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.mail.Session;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.http.client.HttpClient;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jgroups.JChannel;

/**
 * Component key.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class Keys {
    // Global keys

    // Tx
    public static final Key<TransactionManager> TM = new GlobalKey<TransactionManager>(TransactionManager.class);
    public static final Key<UserTransaction> USER_TX = new GlobalKey<UserTransaction>(UserTransaction.class);
    // Threading
    public static final Key<ExecutorService> EXECUTOR_SERVICE = new GlobalKey<ExecutorService>(ExecutorService.class);
    public static final Key<ThreadFactory> THREAD_FACTORY = new GlobalKey<ThreadFactory>(ThreadFactory.class);
    // JGroups
    public static final Key<JChannel> CHANNEL = new GlobalKey<JChannel>(JChannel.class);
    // Infinispan
    public static final Key<EmbeddedCacheManager> CACHE_MANAGER = new GlobalKey<EmbeddedCacheManager>(EmbeddedCacheManager.class);
    // JMS
    public static final Key<Queue> QUEUE = new GlobalKey<Queue>(Queue.class);
    public static final Key<ConnectionFactory> CONNECTION_FACTORY = new GlobalKey<ConnectionFactory>(ConnectionFactory.class);
    // Mail
    public static final Key<Session> MAIL_SESSION = new GlobalKey<Session>(Session.class);
    // HttpClient
    public static final Key<HttpClient> HTTP_CLIENT = new GlobalKey<HttpClient>(HttpClient.class);
}
