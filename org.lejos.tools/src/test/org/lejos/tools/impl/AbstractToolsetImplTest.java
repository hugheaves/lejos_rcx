package org.lejos.tools.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lejos.tools.api.IProgressMonitorToolset;
import org.lejos.tools.impl.AbstractToolsetImpl.Classpath;

/**
 * Tests for <code>AbstractToolsetImpl</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AbstractToolsetImplTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(
            new TestSuite(AbstractToolsetImplTest.class));
    }

    public AbstractToolsetImplTest(String name) {
        super(name);
    }

    // test methods

    public void testConstructor() {
        new MyToolset();
    }

    public void testDefaults() {
        AbstractToolsetImpl toolset = new MyToolset();
        assertFalse(toolset.getVerbose());
        assertEquals(System.out, toolset.getVerboseStream());
        IProgressMonitorToolset monitor = toolset.getProgressMonitor();
        assertTrue(monitor instanceof ConsoleProgressMonitorToolsetImpl);
    }

    public void testGettersSettes() throws FileNotFoundException, IOException {
        AbstractToolsetImpl toolset = new MyToolset();

        toolset.setVerbose(true);
        assertTrue(toolset.getVerbose());
        toolset.setVerbose(false);
        assertFalse(toolset.getVerbose());

        File f = File.createTempFile("junit", "");
        PrintStream ps = new PrintStream(new FileOutputStream(f));
        toolset.setVerboseStream(ps);
        assertEquals(ps, toolset.getVerboseStream());
        ps.close();

        IProgressMonitorToolset monitor = new NullProgressMonitorToolsetImpl();
        toolset.setProgressMonitor(monitor);
        assertTrue(monitor == toolset.getProgressMonitor());

        // cleanup generated files
        assertTrue (f.delete());
    }

    public void testClasspathValid() throws IOException {
        AbstractToolsetImpl toolset = new MyToolset();
        toolset.setVerbose(true);
        Classpath cp;
        String expected;
        InputStream is;

        // prepare test environment in tmp
        File binDirFile = File.createTempFile("junit-bin", ".dir");
        String binDir = binDirFile.toString();
        binDirFile.delete();
        binDirFile.mkdirs();
        File classXFile = new File(binDirFile, "X.class");
        classXFile.createNewFile();
        // TODO specify somewhere the directory of lejos,
        // e.g. as LEJOS_HOME as part of the toolset
        //TODO this also does NOT work, if starting with PDE JUnit
        copyFile("../org.lejos/lib/classes.jar", binDir + "/classes.jar");
        copyFile("../org.lejos/lib/jtools.jar", binDir + "/jtools.jar");

        // check for lookup using a directory
        cp = toolset.new Classpath(binDir);
        expected = binDir + File.pathSeparator;
        assertEquals(expected, cp.toString());
        assertTrue(cp.exists("X"));
        is = cp.getInputStream("X");
        assertNotNull(is);
        is.close();

        // check for lookup of zip file
        cp = toolset.new Classpath(binDir + File.separator + "classes.jar");
        expected = binDir + File.separator + "classes.jar" + File.pathSeparator;
        expected = expected.replace('/', File.separatorChar);
        assertEquals(expected, cp.toString());
        assertTrue(cp.exists("java.lang.Object"));
        is = cp.getInputStream("java.lang.Object");
        assertNotNull(is);
        is.close();

        cp = toolset.new Classpath(binDir + File.separator + "jtools.jar");
        expected = binDir + File.separator + "jtools.jar" + File.pathSeparator;
        expected = expected.replace('/', File.separatorChar);
        assertEquals(expected, cp.toString());
        assertTrue(cp.exists("js.tinyvm.ClassPath"));
        is = cp.getInputStream("js.tinyvm.ClassPath");
        assertNotNull(is);
        is.close();

        // cleanup generated files
        File f = new File(binDir + "/X.class");
        assertTrue(f.delete());
        f = new File(binDir + "/classes.jar");
        assertTrue(f.delete());
        f = new File(binDir + "/jtools.jar");
        assertTrue(f.delete());
        f = new File(binDir);
        assertTrue(f.delete());
    }

    public void testClasspathInvalid() {
        AbstractToolsetImpl toolset = new MyToolset();
        toolset.setVerbose(true);
        // set to null to disable any verbose output
        toolset.setVerboseStream(null);

        Classpath cp = toolset.new Classpath(null);
        assertEquals("", cp.toString());
        assertFalse(cp.exists(null));
        assertFalse(cp.exists(""));
        assertFalse(cp.exists("package1.package2.Class1"));

        cp = toolset.new Classpath("");
        assertEquals("", cp.toString());
        assertFalse(cp.exists(null));
        assertFalse(cp.exists(""));
        assertFalse(cp.exists("package1.package2.Class1"));

        cp = toolset.new Classpath(".");
        assertEquals("." + File.pathSeparator, cp.toString());
        assertFalse(cp.exists(null));
        assertFalse(cp.exists(""));
        assertFalse(cp.exists("package1.package2.Class1"));

        cp = toolset.new Classpath("blablab.zip");
        assertEquals("", cp.toString());
        cp = toolset.new Classpath("blablabaaa.jar");
        assertEquals("", cp.toString());
        cp = toolset.new Classpath("blablab");
        assertEquals("", cp.toString());
        cp =
            toolset.new Classpath(
                "blablab"
                    + File.pathSeparator
                    + "blabla.zip"
                    + File.pathSeparator
                    + "blablab.jar");
        assertEquals("", cp.toString());

        cp =
            toolset.new Classpath(
                "."
                    + File.pathSeparator
                    + "blablab"
                    + File.pathSeparator
                    + "blablab.zip");
        assertEquals("." + File.pathSeparator, cp.toString());
    }

    // private methods

    /**
	 * Copies a file.
	 */
    private void copyFile(String src, String dest) throws IOException {
        File srcFile = new File(src);
        File destFile = new File(dest);

        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(srcFile);
            os = new FileOutputStream(destFile);
            byte[] buff = new byte[1024];
            int count = is.read(buff);
            while (count != -1) {
                os.write(buff, 0, count);
                count = is.read(buff);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore ...
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore ...
                }
            }
        }
    }

    // inner classes

    private class MyToolset extends AbstractToolsetImpl {
        protected IProgressMonitorToolset getProgressMonitor() {
            return super.getProgressMonitor();
        }
        protected boolean getVerbose() {
            return super.getVerbose();
        }
        protected PrintStream getVerboseStream() {
            return super.getVerboseStream();
        }
    }
}
