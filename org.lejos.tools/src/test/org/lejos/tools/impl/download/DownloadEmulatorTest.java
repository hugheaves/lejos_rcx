package org.lejos.tools.impl.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for <code>DownloadEmulator</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class DownloadEmulatorTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(new TestSuite(DownloadEmulatorTest.class));
   }

   public DownloadEmulatorTest (String name)
   {
      super(name);
   }

   public void setUp ()
   {
   //      createSourceFile ("p1.C1");
   //      compileSourceFile ("p1.C1", "p1/C1.class");
   //      linkClassFile ("p1/C1.class", "p1/C1-emulator.leJOS");
   }

   public void tearDown ()
   {

   }

   // test methods

   public void _testConstructor ()
   {
      new DownloadEmulator();
   }

   public void _testSetExecutable ()
   {
      DownloadEmulator emu = new DownloadEmulator();
      File emuLejosrun = new File("../org.lejos/os/win32/x86/emu-lejosrun.exe");
      emu.setEmulatorExecutable(emuLejosrun);
   }

   public void testHelpers () throws IOException
   {
      createSourceFile("p1.C1");
   }

   // private methods

   /**
    * Create a source file for testing purposes.
    * 
    * @param fullQualifiedClassName the name of a class including package scope.
    */
   private void createSourceFile (String fullQualifiedClassName)
      throws IOException
   {
      String packageName = fullQualifiedClassName.substring(0,
         fullQualifiedClassName.lastIndexOf('.'));
      String className = fullQualifiedClassName
         .substring(fullQualifiedClassName.lastIndexOf('.')+1);

      // create dirs
      File prjDir = File.createTempFile("junit", ".prj");
      prjDir.delete();
      File srcDir = new File(prjDir, "src");
      srcDir.mkdirs();

      File packageDir = new File(srcDir, packageName.replace('.', '/'));
      packageDir.mkdirs();

      File srcFile = new File(packageDir, className + ".java");
      OutputStream os = new FileOutputStream(srcFile);
      PrintWriter out = new PrintWriter(os);
      out.println("package " + packageName + ";");
      out.println("");
      out.println("import josx.platform.rcx.TextLCD;");
      out.println("");
      out.println("public class " + className + " {");
      out.println("");
      out.println("    public void static main(String[] args) throws InterruptedException {");
      out.println("        TextLCD.print(\"Hello\");");
      out.println("        Thread.sleep(2000);");
      out.println("        TextLCD.print(\"World\");");
      out.println("    }");
      out.println("");
      out.println("}");
      out.close();
      os.close ();
   }

   private void compileSourceFile (String fullQualifiedClassName)
   {

   }

   private void linkClassFile (String classFile, String binFile)
   {

   }
}