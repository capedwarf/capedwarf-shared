package org.jboss.test.capedwarf.shared.config.test;

import org.jboss.capedwarf.shared.config.FilePattern;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
