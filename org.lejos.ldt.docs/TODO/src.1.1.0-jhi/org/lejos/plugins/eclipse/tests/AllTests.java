package org.lejos.plugins.eclipse.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * ALl tests for the leJOS plugin.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.lejos.plugins.eclipse");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(LejosAdaptorNativeTest.class));
		//$JUnit-END$
		return suite;
	}
}
