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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.jboss.capedwarf.shared.config.FilePattern;
import org.junit.Test;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public class FilePatternTest {

    @Test
    public void testShallowExtensionPattern() {
        FilePattern pattern = new FilePattern("*.html");
        assertPatternMatches(pattern, "/foo.html", "/bar.html");
        assertPatternDoesNotMatch(pattern, "", "/", "/foo.txt", "/foo/bar.html", "/foo_html");
    }

    @Test
    public void testShallowExtensionPatternWithLeadingSlash() {
        FilePattern pattern = new FilePattern("/*.html");
        assertPatternMatches(pattern, "/foo.html", "/bar.html");
        assertPatternDoesNotMatch(pattern, "", "/", "/foo.txt", "/foo/bar.html", "/foo_html");
    }

    @Test
    public void testShallowExtensionPatternInSubdir() {
        FilePattern pattern = new FilePattern("foo/*.html");
        assertPatternMatches(pattern, "/foo/bar.html");
        assertPatternDoesNotMatch(pattern, "", "/", "/foo.txt", "/foo.html", "/foo/bar/baz.html");
    }

    @Test
    public void testShallowExtensionPatternInSubdirWithLeadingSlash() {
        FilePattern pattern = new FilePattern("/foo/*.html");
        assertPatternMatches(pattern, "/foo/bar.html");
        assertPatternDoesNotMatch(pattern, "", "/", "/foo.txt", "/foo.html", "/foo/bar/baz.html");
    }

    @Test
    public void testDeepExtensionPattern() {
        FilePattern pattern = new FilePattern("**.html");
        assertPatternMatches(pattern, "/foo.html", "/foo/bar.html", "/foo/bar/baz.html");
        assertPatternDoesNotMatch(pattern, "", "/", "/foo.txt", "/foo/bar/baz.txt", "/foo/bar/baz_html");
    }

    @Test
    public void testDeepExtensionPatternWithLeadingSlash() {
        FilePattern pattern = new FilePattern("/**.html");
        assertPatternMatches(pattern, "/foo.html", "/foo/bar.html", "/foo/bar/baz.html");
        assertPatternDoesNotMatch(pattern, "", "/", "/foo.txt", "/foo/bar/baz.txt", "/foo/bar/baz_html");
    }

    @Test
    public void testDeepPath() {
        FilePattern pattern = new FilePattern("**");
        assertPatternMatches(pattern, "/", "/foo.html", "/foo/bar.png", "/foo/bar.html", "/foo/bar/baz.html");
    }

    @Test
    public void testDeepPathWithLeadingSlash() {
        FilePattern pattern = new FilePattern("/**");
        assertPatternMatches(pattern, "/", "/foo.html", "/foo/bar.png", "/foo/bar.html", "/foo/bar/baz.html");
    }

    @Test
    public void testDeepPathInSubdir() {
        FilePattern pattern = new FilePattern("foo/**");
        assertPatternMatches(pattern, "/foo/bar.png", "/foo/bar.html", "/foo/bar/baz.html");
        assertPatternDoesNotMatch(pattern, "/", "/foo.html", "/baz/foo.html", "/baz/foo/bar.html");
    }

    @Test
    public void testDeepPathInSubdirWithLeadingSlash() {
        FilePattern pattern = new FilePattern("/foo/**");
        assertPatternMatches(pattern, "/foo/bar.png", "/foo/bar.html", "/foo/bar/baz.html");
        assertPatternDoesNotMatch(pattern, "/", "/foo.html", "/baz/foo.html", "/baz/foo/bar.html");
    }

    @Test
    public void testDeepPathAndAnyExtension() {
        FilePattern pattern = new FilePattern("**.*");
        assertPatternMatches(pattern, "/foo.html", "/foo/bar.png", "/foo/bar.html", "/foo/bar/baz.html");
    }

    @Test
    public void testDeepPathAndAnyExtensionWithLeadingSlash() {
        FilePattern pattern = new FilePattern("/**.*");
        assertPatternMatches(pattern, "/foo.html", "/foo/bar.png", "/foo/bar.html", "/foo/bar/baz.html");
    }

    @Test
    public void testFixedPath() {
        FilePattern pattern = new FilePattern("/favicon.ico");
        assertPatternMatches(pattern, "/favicon.ico");
        assertPatternDoesNotMatch(pattern, "/favicon_ico", "/something.other");
    }


    private void assertPatternMatches(FilePattern pattern, String... paths) {
        for (String path : paths) {
            assertTrue("Expected pattern " + pattern + " to match path " + path + ", but it did not", pattern.matches(path));
        }
    }

    private void assertPatternDoesNotMatch(FilePattern pattern, String... paths) {
        for (String path : paths) {
            assertFalse("Expected pattern " + pattern + " not to match path " + path + ", but it did", pattern.matches(path));
        }
    }
}
