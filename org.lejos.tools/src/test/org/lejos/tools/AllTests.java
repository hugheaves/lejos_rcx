package org.lejos.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.lejos.tools.api.FactoryConfigurationErrorTest;
import org.lejos.tools.api.ToolsetExceptionTest;
import org.lejos.tools.api.ToolsetFactoryTest;
import org.lejos.tools.impl.AbstractToolsetImplTest;
import org.lejos.tools.impl.ConsoleProgressMonitorToolsetImplTest;
import org.lejos.tools.impl.NullProgressMonitorToolsetImplTest;
import org.lejos.tools.impl.RuntimeToolsetImplTest;
import org.lejos.tools.main.LejosCompileTest;
import org.lejos.tools.main.LejosLinkTest;

/**
 * Overall test suite for lejos tools.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.lejos.tools");
        //$JUnit-BEGIN$
        suite.addTestSuite(FactoryConfigurationErrorTest.class);
        suite.addTestSuite(ToolsetExceptionTest.class);
        suite.addTestSuite(ToolsetFactoryTest.class);
        suite.addTestSuite(NullProgressMonitorToolsetImplTest.class);
        suite.addTestSuite(ConsoleProgressMonitorToolsetImplTest.class);
        suite.addTestSuite(AbstractToolsetImplTest.class);
        suite.addTestSuite(RuntimeToolsetImplTest.class);
        suite.addTestSuite(LejosCompileTest.class);
        suite.addTestSuite(LejosLinkTest.class);
        //$JUnit-END$
        return suite;
    }
}