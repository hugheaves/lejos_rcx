package org.lejos.tools.impl;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.api.ToolsetException;

/**
 * Tests for <code>RuntimeToolsetImpl</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class RuntimeToolsetImplTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(new TestSuite(RuntimeToolsetImplTest.class));
   }

   public RuntimeToolsetImplTest (String name)
   {
      super(name);
   }

   // test methods

   public void testConstructor ()
   {
      new RuntimeToolsetImpl();
   }

   // TODO test does not work, until Class1 will be created AND compiled
   // correctly
   public void _testLinkValidFiles () throws ToolsetException, IOException
   {
      IRuntimeToolset toolset = new RuntimeToolsetImpl();
      File tmpFile = File.createTempFile("junit", "");
      File tmpDir = new File(tmpFile.getParent());
      tmpFile.delete();
      File packageDir = new File(tmpDir + "/package1/package2");
      packageDir.mkdirs();
      tmpFile = new File(packageDir + "/Class1.class");
      tmpFile.createNewFile();
      String classpath = tmpDir.toString();

      File outputFile = File.createTempFile("junit", ".leJOS");

      toolset.link(outputFile, IRuntimeToolset.LINK_METHOD_ALL, true,
         classpath, new String[]
         {
            "package1.package2.Class1"
         }, null);
      // cleanup all tmp files after test
      tmpFile.delete();
      packageDir.delete();
      packageDir.getParentFile().delete();
      outputFile.delete();
   }

   public void testLinkInvalidFiles () throws ToolsetException
   {
      IRuntimeToolset toolset = new RuntimeToolsetImpl();
      toolset.setProgressMonitor(new NullProgressMonitorToolsetImpl());
      try
      {
         toolset.link(null, 0, true, null, null, null);
         fail("Oops, exception expected");
      }
      catch (ToolsetException ex)
      {
         // will be expected
      }
      try
      {
         toolset.link(new File("C:\\output_blabla\\blabla.leJOS"),
            IRuntimeToolset.LINK_METHOD_ALL, true, null, new String[]
            {
               new String("input_blabla.Blbalba")
            }, null);
         fail("Oops, exception expected");
      }
      catch (ToolsetException ex)
      {
         // will be expected
      }
      // TODO add more tests for readonly files, partially invalid files ...
   }
}