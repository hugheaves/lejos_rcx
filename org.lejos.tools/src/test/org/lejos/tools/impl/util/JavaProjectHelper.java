package org.lejos.tools.impl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Helper class for creating java projects, without any Eclipse.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class JavaProjectHelper
{
   // attributes

   private File prjDir;
   private File javaHome = null;
   private File lejosHome = null;

   // constructors

   public JavaProjectHelper ()
   {
      try
      {
         // create dirs
         this.prjDir = File.createTempFile("junit", ".prj");
         this.prjDir.delete();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   // public methods

   public void setJavaHome (File dir)
   {
      this.javaHome = dir;
   }

   public void setLejosHome (File dir)
   {
      this.lejosHome = dir;
   }

   /**
    * Create a source file for testing purposes.
    * 
    * @param fullQualifiedClassName the name of a class including package scope.
    */
   public void createSourceFile (String fullQualifiedClassName)
      throws IOException
   {
      String packageName = fullQualifiedClassName.substring(0,
         fullQualifiedClassName.lastIndexOf('.'));
      String className = fullQualifiedClassName
         .substring(fullQualifiedClassName.lastIndexOf('.') + 1);

      // create dirs
      File srcDir = new File(this.prjDir, "src");
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
      out
         .println("    public static void main(String[] args) throws InterruptedException {");
      out.println("        TextLCD.print(\"Hello\");");
      out.println("        Thread.sleep(2000);");
      out.println("        TextLCD.print(\"World\");");
      out.println("    }");
      out.println("");
      out.println("}");
      out.close();
      os.close();
   }

   public void compileSourceFile (String fullQualifiedClassName)
      throws IOException
   {
      // create dirs
      File classesDir = new File(this.prjDir, "classes");
      classesDir.mkdirs();

      String cmdline = "";
      if (this.javaHome != null)
      {
         cmdline = this.javaHome.toString() + File.separator + "bin"
            + File.separator;
      }
      String classpath = this.lejosHome + "/lib/classes.jar";
      classpath = classpath + File.pathSeparator + this.lejosHome
         + "/lib/rcxrcxcomm.jar";
      cmdline = cmdline + "javac -g -target 1.1 -classpath " + classpath
         + " -d classes src/" + fullQualifiedClassName.replace('.', '/')
         + ".java";

      System.out.println(cmdline);
      Process compiler = Runtime.getRuntime().exec(cmdline, null, this.prjDir);
      try
      {
         Thread.sleep(3000);
      }
      catch (InterruptedException ex)
      {}
      InputStream err = compiler.getErrorStream();
      while (err.available() > 0)
      {
         int i = err.read();
         System.out.print(new Character((char) i));
      }
   }
}