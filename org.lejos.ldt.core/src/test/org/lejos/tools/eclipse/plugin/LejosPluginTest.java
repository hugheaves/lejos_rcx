package org.lejos.tools.eclipse.plugin;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.lejos.tools.eclipse.plugin.util.SimpleJavaProject;

/**
 * Tests for <code>LejosPlugin</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosPluginTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(new TestSuite(LejosPluginTest.class));
   }

   public LejosPluginTest (String name)
   {
      super(name);
   }

   private SimpleJavaProject sjp;

   protected void setUp () throws Exception
   {
      sjp = new SimpleJavaProject();
      sjp.createProject();
   }

   protected void tearDown () throws Exception
   {
      // cleanup project
      sjp.dispose();
      sjp = null;
   }

   // test methods

   public void testSetAndCheckForLeJOSNature () throws CoreException
   {
      IProject project = sjp.getJavaProject().getProject();
      LejosPlugin.addLeJOSNature(project);
      assertTrue(LejosPlugin.checkForLeJOSNature(project));
   }
}