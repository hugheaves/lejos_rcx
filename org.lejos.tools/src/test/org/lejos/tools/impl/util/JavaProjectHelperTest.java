package org.lejos.tools.impl.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for <code>JavaProjectHelper</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class JavaProjectHelperTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(new TestSuite(JavaProjectHelperTest.class));
   }

   public JavaProjectHelperTest (String name)
   {
      super(name);
   }

   // test methods

   public void testCreateSourceFile () throws IOException
   {
      JavaProjectHelper helper = new JavaProjectHelper();
      helper.createSourceFile("p1.C1");
   }

   public void testCOmpile () throws IOException
   {
      JavaProjectHelper helper = new JavaProjectHelper();
      helper.setJavaHome(new File("J:/j2sdk1.4.2_03"));
      helper.setLejosHome(new File("../org.lejos").getAbsoluteFile());
      helper.createSourceFile("p1.p2.C1");
      helper.compileSourceFile("p1.p2.C1");
   }
}