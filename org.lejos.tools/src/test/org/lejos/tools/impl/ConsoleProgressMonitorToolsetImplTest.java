package org.lejos.tools.impl;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * Tests for <code>ConsoleProgressMonitorToolsetImpl</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class ConsoleProgressMonitorToolsetImplTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(
            new TestSuite(ConsoleProgressMonitorToolsetImplTest.class));
    }

    public ConsoleProgressMonitorToolsetImplTest(String name) {
        super(name);
    }

    // test methods

    public void testConstructor() {
        new ConsoleProgressMonitorToolsetImpl();
    }

    public void testTasks() {
        IProgressMonitorToolset progress =
            new SilentConsoleProgressMonitor();
        progress.beginTask("Doing something", 100);
        progress.worked(25);
        progress.worked(50);
        progress.worked(75);
        progress.worked(100);
        progress.done();
    }

    public void testMultipleUsage() {
        IProgressMonitorToolset progress = new SilentConsoleProgressMonitor();
        progress.beginTask("Doing something", 100);
        progress.done();
        try {
            progress.beginTask("Should not work", 10);
            fail("Oops, exception expected");
        } catch (IllegalStateException ex) {
            // will be expected here
        }
    }

    public void testCancel() {
        IProgressMonitorToolset progress =
            new ConsoleProgressMonitorToolsetImpl();
        assertFalse(progress.isCanceled());
        progress.setCanceled(true);
        assertTrue(progress.isCanceled());
        progress.setCanceled(false);
        assertFalse(progress.isCanceled());
    }

    // inner classes

    public static class SilentConsoleProgressMonitor
        extends ConsoleProgressMonitorToolsetImpl {
        protected void print(String aMsg) {
            // do nothing !!!
        }
    }
}
