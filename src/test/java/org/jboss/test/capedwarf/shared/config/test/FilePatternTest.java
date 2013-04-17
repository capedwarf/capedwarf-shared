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
    public void testEmptyPath() {
        FilePattern pattern = new FilePattern("/*.html");
        assertFalse(pattern.matches(""));
    }

    @Test
    public void testSimplePath() {
        FilePattern pattern = new FilePattern("/*.html");
        assertTrue(pattern.matches("/foo.html"));
        assertFalse(pattern.matches("/foo/bar.html"));
        assertFalse(pattern.matches("/foo_html"));
    }

    @Test
    public void testDirectoryPath() {
        FilePattern pattern = new FilePattern("/**.html");
        assertTrue(pattern.matches("/foo.html"));
        assertTrue(pattern.matches("/foo/bar.html"));
        assertTrue(pattern.matches("/foo/bar/baz.html"));
        assertFalse(pattern.matches("/foo/bar/baz.txt"));
        assertFalse(pattern.matches("/foo/bar/baz_html"));
    }

    @Test
    public void testDirectoryPathWithAnyExtension() {
        FilePattern pattern = new FilePattern("/**.*");
        assertTrue(pattern.matches("/foo/bar.png"));
        assertTrue(pattern.matches("/foo.html"));
        assertTrue(pattern.matches("/foo/bar.html"));
        assertTrue(pattern.matches("/foo/bar/baz.html"));
    }

    @Test
    public void testFixedPath() {
        FilePattern pattern = new FilePattern("/favicon.ico");
        assertTrue(pattern.matches("/favicon.ico"));
        assertFalse(pattern.matches("/favicon_ico"));
        assertFalse(pattern.matches("/something.other"));
    }
}
