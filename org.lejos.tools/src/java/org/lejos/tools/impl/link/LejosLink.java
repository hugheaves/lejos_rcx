package org.lejos.tools.impl.link;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import js.tinyvm.Binary;
import js.tinyvm.BinaryReport;
import js.tinyvm.ClassPath;
import js.tinyvm.io.BEByteWriter;
import js.tinyvm.io.ByteWriter;
import js.tinyvm.io.LEByteWriter;

import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.impl.AbstractToolsetImpl.Classpath;

/**
 * Implements the link within the lejos environment.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosLink
{

   // attributes

   /**
    * the method to link.
    * 
    * @see IRuntimeToolset#LINK_METHOD_ALL
    * @see IRuntimeToolset#LINK_METHOD_OPTIMIZING
    */
   private int linkMethod;

   /**
    * the byte order for the linkage.
    * 
    * @see IRuntimeToolset#BYTE_ORDER_BIG_ENDIAN
    * @see IRuntimeToolset#BYTE_ORDER_LITTLE_ENDIAN
    */
   private int byteOrder;

   /** the plain js.tools helper classes */
   private Binary binary;

   // constructor

   /**
    * Default constructor
    */
   public LejosLink ()
   {
      this.linkMethod = IRuntimeToolset.LINK_METHOD_UNKNOWN;
      this.byteOrder = IRuntimeToolset.BYTE_ORDER_UNKNOWN;
   }

   // public methods

   /**
    * Set the used link method.
    * 
    * <p>
    * Will validate the link method against the supported link methods. If not
    * valid, an IllegalArgumentException will be raised.
    * </p>
    * 
    * @see IRuntimeToolset#LINK_METHOD_ALL
    * @see IRuntimeToolset#LINK_METHOD_OPTIMIZING
    * @param aLinkMethod the link method
    */
   public void setLinkMethod (int aLinkMethod)
   {
      // validate link method
      if ((aLinkMethod != IRuntimeToolset.LINK_METHOD_ALL)
         && (aLinkMethod != IRuntimeToolset.LINK_METHOD_OPTIMIZING))
      {
         throw new IllegalArgumentException("Illegal link method specified: "
            + String.valueOf(aLinkMethod));
      }
      this.linkMethod = aLinkMethod;
   }

   /**
    * Set the byte order to be used for linking.
    * 
    * <p>
    * Will validate the byte order against the supported byte orders. If not
    * valid, an IllegalArgumentException will be raised.
    * </p>
    * 
    * @see IRuntimeToolset#BYTE_ORDER_BIG_ENDIAN
    * @see IRuntimeToolset#BYTE_ORDER_LITTLE_ENDIAN
    * @param aByteOrder the byte order
    */
   public void setByteOrder (int aByteOrder)
   {
      // validate byte order
      if ((aByteOrder != IRuntimeToolset.BYTE_ORDER_BIG_ENDIAN)
         && (aByteOrder != IRuntimeToolset.BYTE_ORDER_LITTLE_ENDIAN))
      {
         throw new IllegalArgumentException("Wrong byte order: "
            + String.valueOf(aByteOrder));
      }
      this.byteOrder = aByteOrder;
   }

   /**
    * Prepare the link, reading the classes, extracting necessary information.
    * 
    * @param classFiles the list with classfiles
    * @param args optionally, any arguments
    * @param classpath the classpath to be used
    * @throws ToolsetException will be raised in any error case
    * 
    * TODO args will not be supported for the moment
    */
   public void prepareCompiledClasses (String[] classFiles, String[] args,
      Classpath classpath) throws ToolsetException
   {

      // we cannot link more than 256 classes
      if (classFiles.length > 255)
      {
         throw new ToolsetException("Too many entry classes: max is 255 !");
      }

      // TODO for the moment: Convert from our classpath to the old one
      ClassPath oldClasspath;
      try
      {
         oldClasspath = new ClassPath(classpath.toString());
      }
      catch (Exception ex)
      {
         throw new ToolsetException("Error with classpath \""
            + classpath.toString() + "\": " + ex.getMessage());
      }
      // TODO temporarily, convert to a vector
      for (int i = 0; i < classFiles.length; i++)
      {
         classFiles[i] = classFiles[i].replace('.', '/');
         // the old classes require a "package1/package2/Class1" style
      }
      boolean linkAll = (this.linkMethod == IRuntimeToolset.LINK_METHOD_ALL);
      // check, whether the specified classes really have a main
      try
      {
         this.binary = Binary.createFromClosureOf(classFiles, oldClasspath, linkAll);
      }
      catch (Exception ex)
      {
         throw new ToolsetException("Error with creating binary closure: "
            + ex.getMessage());
      }
      for (int i = 0; i < classFiles.length; i++)
      {
         // use convention package1/package2/Class1
         if (!this.binary.hasMain(classFiles[i]))
         {
            throw new ToolsetException("Class " + classFiles[i].replace('/', '.')
               + " does not have a " + "static void main(String[]) method.");
         }
      }
   }

   /**
    * Creates the binary and signature file.
    * 
    * <p>
    * It will create the output files based on the previous
    * <code>prepareCompiledClasses()</code>.
    * </p>
    * 
    * @param outputFile the output file to be created
    * @param signatureFile the signature file to be created
    * @throws ToolsetException will be raised in any error case
    */
   public void createBinaryFile (File outputFile, File signatureFile)
      throws ToolsetException
   {
      OutputStream os = null;
      try
      {
         // now create the output stream
         OutputStream osUnbuffered = new FileOutputStream(outputFile);
         os = new BufferedOutputStream(osUnbuffered, 4096);
         ByteWriter writer = null;
         if (this.byteOrder == IRuntimeToolset.BYTE_ORDER_BIG_ENDIAN)
         {
            writer = new BEByteWriter(os);
         }
         else
         {
            writer = new LEByteWriter(os);
         }

         // now dump the binaries into the stream
         this.binary.dump(writer);
         BinaryReport report = new BinaryReport(this.binary);
         Writer signatureWriter = new FileWriter(signatureFile);
         report.report(signatureWriter);
         signatureWriter.close();
         os.close();
      }
      catch (Exception ex)
      {
         throw new ToolsetException(
            "Some error occured during creating binary file: "
               + ex.getMessage());
      }
      finally
      {
         if (os != null)
         {
            try
            {
               os.close();
            }
            catch (IOException ex)
            {
               // ignore in finally
            }
         }
      }
   }
}