package org.lejos.tools.eclipse.plugin;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.util.SimpleJavaProject;
import org.lejos.tools.impl.NullProgressMonitorToolsetImpl;

/**
 * Tests for <code>EclipseToolsetFactory</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class EclipseToolsetFacadeTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner
         .run(new TestSuite(EclipseToolsetFacadeTest.class));
   }

   public EclipseToolsetFacadeTest (String name)
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

   /**
    * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
    */
   public void testCompileJavaElements () throws ToolsetException,
      InterruptedException, MalformedURLException, CoreException, IOException
   {
      EclipseToolsetFacade facade = new EclipseToolsetFacade();
      // silent monitor for tests
      facade.setProgressMonitor(new NullProgressMonitorToolsetImpl());
      facade
         .compileJavaElement(sjp.getPackage1(), LejosPlugin.getPreferences());
   }

   public void testLinkOneCU () throws ToolsetException, InterruptedException,
      MalformedURLException, CoreException, IOException
   {
      EclipseToolsetFacade facade = new EclipseToolsetFacade();
      // disbable output for tests
      facade.setProgressMonitor(new NullProgressMonitorToolsetImpl());
      facade.linkCU(sjp.getPackage1Package2Class1CU(), LejosPlugin
         .getPreferences());
   }

   public void testLinkJavaElement () throws ToolsetException,
      InterruptedException, MalformedURLException, CoreException, IOException
   {
      EclipseToolsetFacade facade = new EclipseToolsetFacade();
      // disable output for tests
      facade.setProgressMonitor(new NullProgressMonitorToolsetImpl());
      facade.linkJavaElement(sjp.getPackage1(), LejosPlugin.getPreferences());
   }
}