package org.lejos.tools.api;

import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lejos.tools.impl.ConsoleProgressMonitorToolsetImpl;

/**
 * Tests for <code>ToolsetFactory</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class ToolsetFactoryTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(ToolsetFactoryTest.class));
    }

    public ToolsetFactoryTest(String name) {
        super(name);
    }

    // test methods

    public void testSingleton() {
        ToolsetFactory fac1 = ToolsetFactory.newInstance();
        ToolsetFactory fac2 = ToolsetFactory.newInstance();
        // currently, no singleton implemented
        assertTrue(fac1 != fac2);
    }

    public void testNewToolsets() throws ToolsetException {
        ToolsetFactory fac = ToolsetFactory.newInstance();
        IProgressMonitorToolset progress = fac.newProgressMonitor();
        assertNotNull(progress);
        IRuntimeToolset rt = fac.newRuntimeToolset();
        assertNotNull(rt);
        IDevelopmentToolset dev = fac.newDevelopmentToolset();
        assertNotNull(dev);
    }

    public void testFactory() {
        ToolsetFactory fac;

        Properties props = new Properties();
        // uses the default
        fac = ToolsetFactory.newInstance(props);
        assertNotNull(fac);
        assertEquals(ToolsetFactory.class, fac.getClass());

        // uses the standard implementation
        props.setProperty(ToolsetFactory.FACTORY_CLASS_PROPERTY,
                ToolsetFactory.class.getName());
        fac = ToolsetFactory.newInstance(props);
        assertEquals(ToolsetFactory.class, fac.getClass());

        // uses a specific factory implementation
        props.setProperty(ToolsetFactory.FACTORY_CLASS_PROPERTY,
                MyToolsetFactory.class.getName());
        fac = ToolsetFactory.newInstance(props);
        assertEquals(MyToolsetFactory.class, fac.getClass());

        // specified a non existing class
        props.setProperty(ToolsetFactory.FACTORY_CLASS_PROPERTY,
                "java.lang.Balbalbalab");
        try {
            fac = ToolsetFactory.newInstance(props);
            fail("Oops, exception expected");
        } catch (FactoryConfigurationError ex) {
            // will be expected here
        }

        // specified a class with wrong type
        props.setProperty(ToolsetFactory.FACTORY_CLASS_PROPERTY,
                "java.lang.Object");
        try {
            fac = ToolsetFactory.newInstance(props);
            fail("Oops, exception expected");
        } catch (FactoryConfigurationError ex) {
            // will be expected here
        }

        // specified a class with no public access
        props.setProperty(ToolsetFactory.FACTORY_CLASS_PROPERTY,
                MyToolsetFactoryInvalid.class.getName());
        try {
            fac = ToolsetFactory.newInstance(props);
            fail("Oops, exception expected");
        } catch (FactoryConfigurationError ex) {
            // will be expected here
        }
    }

    public void testNewProgressMonitor() throws ToolsetException {
        IProgressMonitorToolset pm;

        Properties props = new Properties();
        // uses the default
        pm = ToolsetFactory.newInstance(props).newProgressMonitor();
        assertNotNull(pm);
        assertEquals(ConsoleProgressMonitorToolsetImpl.class, pm.getClass());

        // uses the standard implementation
        props.setProperty(ToolsetFactory.PROGRESS_MONITOR_CLASS_PROPERTY,
                ConsoleProgressMonitorToolsetImpl.class.getName());
        pm = ToolsetFactory.newInstance(props).newProgressMonitor();
        assertEquals(ConsoleProgressMonitorToolsetImpl.class, pm.getClass());

        // uses the specific implementation
        props.setProperty(ToolsetFactory.PROGRESS_MONITOR_CLASS_PROPERTY,
                MyProgressMonitor.class.getName());
        pm = ToolsetFactory.newInstance(props).newProgressMonitor();
        assertEquals(MyProgressMonitor.class, pm.getClass());

        // specified a non existing class
        props.setProperty(ToolsetFactory.PROGRESS_MONITOR_CLASS_PROPERTY,
                "java.lang.Balbalbalab");
        try {
            pm = ToolsetFactory.newInstance(props).newProgressMonitor();
            fail("Oops, exception expected");
        } catch (ToolsetException ex) {
            // will be expected here
        }

        // specified a class with wrong type
        props.setProperty(ToolsetFactory.PROGRESS_MONITOR_CLASS_PROPERTY,
                "java.lang.Object");
        try {
            pm = ToolsetFactory.newInstance(props).newProgressMonitor();
            fail("Oops, exception expected");
        } catch (ToolsetException ex) {
            // will be expected here
        }

        // specified a class with no public access
        props.setProperty(ToolsetFactory.PROGRESS_MONITOR_CLASS_PROPERTY,
                MyProgressMonitorInvalid.class.getName());
        try {
            pm = ToolsetFactory.newInstance(props).newProgressMonitor();
            fail("Oops, exception expected");
        } catch (ToolsetException ex) {
            // will be expected here
        }
    }

    // inner classes

    // make static, to give Reflection a chance to create
    // without context of outer class
    public static class MyToolsetFactory extends ToolsetFactory {
    }

    private class MyToolsetFactoryInvalid extends ToolsetFactory {
        // make class private to test access
        public MyToolsetFactoryInvalid() {
        }
    }

    // make static, to give Reflection a chance to create
    // without context of outer class
    public static class MyProgressMonitor extends
            ConsoleProgressMonitorToolsetImpl {
    }

    private class MyProgressMonitorInvalid extends
            ConsoleProgressMonitorToolsetImpl {
        // make class private to test access
        public MyProgressMonitorInvalid() {
        }
    }
}