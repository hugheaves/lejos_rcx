package org.lejos.tools.eclipse;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.lejos.tools.eclipse.plugin.EclipseToolsetFacadeTest;
import org.lejos.tools.eclipse.plugin.EclipseUtilitiesTest;

/**
 * Overall test suite for the whole Eclipse implementation.
 * 
 * <p>
 * These tests have to be started using the PDE JUnit environment. This can be
 * started using "Run -> JUnit Plugin Test" within Eclipse.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class AllTests
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(AllTests.suite());
   }

   public static Test suite ()
   {
      TestSuite suite = new TestSuite("Test for org.lejos.tools.eclipse");
      //$JUnit-BEGIN$
      suite.addTestSuite(EclipseUtilitiesTest.class);
      suite.addTestSuite(EclipseToolsetFacadeTest.class);
      //$JUnit-END$
      return suite;
   }
}