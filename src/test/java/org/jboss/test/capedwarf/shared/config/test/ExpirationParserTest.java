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


import org.jboss.capedwarf.shared.config.ExpirationParser;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:marko.luksa@gmail.com">Marko Luksa</a>
 */
public class ExpirationParserTest {

    @Test
    public void testNull() throws Exception {
        assertEquals(null, parse(null));
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals(null, parse(""));
    }

    @Test
    public void testSingleToken() throws Exception {
        assertEquals(Long.valueOf(1), parse("1s"));
        assertEquals(Long.valueOf(60), parse("1m"));
        assertEquals(Long.valueOf(3600), parse("1h"));
        assertEquals(Long.valueOf(24 * 3600), parse("1d"));
    }

    @Test
    public void testTwoTokens() throws Exception {
        assertEquals(Long.valueOf(61), parse("1m 1s"));
        assertEquals(Long.valueOf(3660), parse("1h 1m"));
    }

    @Test
    public void testMultipleTokens() throws Exception {
        assertEquals(Long.valueOf(93784), parse("1d 2h 3m 4s"));
    }

    private Long parse(String expiration) {
        return new ExpirationParser().parse(expiration);
    }
}
