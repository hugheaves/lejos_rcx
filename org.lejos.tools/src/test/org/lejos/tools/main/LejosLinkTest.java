package org.lejos.tools.main;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Tests for <code>LejosLinkImpl</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosLinkTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(new TestSuite(LejosLinkTest.class));
   }

   public LejosLinkTest (String name)
   {
      super(name);
   }

   // test methods

   public void testCommandLineValid ()
   {
      NullLejosLink main = new NullLejosLink();
      int rc;

      // all situations with invalid param, will return 1
      rc = main.doMain(new String[]
      {
         "-o", "MyClass.leJOS", "MyClass"
      });
      assertEquals(0, rc);
      assertEquals(1, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);

      rc = main.doMain(new String[]
      {
         "-o", "MyClass.leJOS", "MyClass1,MyClass2,MyClass3"
      });
      assertEquals(0, rc);
      assertEquals(1, main.cl.getArgs().length);
      assertEquals("MyClass1,MyClass2,MyClass3", main.cl.getArgs()[0]);

      // with args
      rc = main.doMain(new String[]
      {
         "-o", "MyClass.leJOS", "MyClass", "arg1"
      });
      assertEquals(0, rc);
      assertEquals(2, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);
      assertEquals("arg1", main.cl.getArgs()[1]);

      rc = main.doMain(new String[]
      {
         "-o", "MyClass.leJOS", "MyClass", "arg1", "arg2", "arg3"
      });
      assertEquals(0, rc);
      assertEquals(4, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);
      assertEquals("arg1", main.cl.getArgs()[1]);
      assertEquals("arg2", main.cl.getArgs()[2]);
      assertEquals("arg3", main.cl.getArgs()[3]);

      rc = main.doMain(new String[]
      {
         "-o", "MyClass.leJOS", "MyClass1,MyClass2,MyClass3", "arg1", "arg2",
         "arg3"
      });
      assertEquals(0, rc);
      assertEquals(4, main.cl.getArgs().length);
      assertEquals("MyClass1,MyClass2,MyClass3", main.cl.getArgs()[0]);
      assertEquals("arg1", main.cl.getArgs()[1]);
      assertEquals("arg2", main.cl.getArgs()[2]);
      assertEquals("arg3", main.cl.getArgs()[3]);
   }

   public void testCommandLineInvalid ()
   {
      LejosLink main = new SilentLejosLink();
      int rc;

      // all situations with invalid param, will return 1
      rc = main.doMain(null);
      assertEquals(1, rc);
      rc = main.doMain(new String[]{});
      assertEquals(1, rc);
   }

   public void testCommandLineValidOptions ()
   {
      NullLejosLink main = new NullLejosLink();
      int rc;

      rc = main.doMain(new String[]
      {
         "-a", "-o", "MyClass.leJOS", "MyClass"
      });
      assertEquals(0, rc);
      assertTrue(main.cl.hasOption("a"));
      assertEquals(1, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);

      rc = main.doMain(new String[]
      {
         "--all", "-o", "MyClass.leJOS", "MyClass"
      });
      assertTrue(main.cl.hasOption("a"));
      assertEquals(0, rc);
      assertEquals(1, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);

      rc = main.doMain(new String[]
      {
         "-o", "MyClass.leJOS", "MyClass"
      });
      assertTrue(main.cl.hasOption("o"));
      assertEquals(main.cl.getOptionValue("o"), "MyClass.leJOS");
      assertEquals(0, rc);
      assertEquals(1, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);

      rc = main.doMain(new String[]
      {
         "--output", "MyClass.leJOS", "MyClass"
      });
      assertEquals(0, rc);
      assertTrue(main.cl.hasOption("o"));
      assertEquals(main.cl.getOptionValue("o"), "MyClass.leJOS");
      assertEquals(1, main.cl.getArgs().length);
      assertEquals("MyClass", main.cl.getArgs()[0]);
   }

   public void testCommandLineInvalidOptions ()
   {
      NullLejosLink main = new NullLejosLink();
      int rc;

      // wrong short option
      // TODO should result in an error !
      rc = main.doMain(new String[]
      {
         "-z", "MyClass"
      });
      // assertEquals (1, rc);

      // wrong long option
      // TODO should result in an error !
      rc = main.doMain(new String[]
      {
         "--blabla", "MyClass"
      });
      // assertEquals (1, rc);
   }

   // inner classes

   public static class NullLejosLink extends LejosLink
   {
      public CommandLine cl;

      public CommandLine getCommandLine (Options options, String[] args)
         throws ParseException
      {
         cl = super.getCommandLine(options, args);
         return cl;
      }

      public int executeCommandLine (CommandLine cmdLine)
      {
         // do nothing for testing here !
         return 0;
      }
   }

   public static class SilentLejosLink extends LejosLink
   {
      protected void printUsage (Options options, String cmdLineUsage)
      {
      // do nithing !!!
      }
   }
}