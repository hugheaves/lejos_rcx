package org.lejos.tools.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * Common functionality for all toolsets used here.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public abstract class AbstractToolsetImpl
{

   // attributes

   /** verbose flag */
   private boolean verbose = false;

   /** stream to print our verbose messages */
   private PrintStream verboseStream = System.out;

   /**
    * the progress monitor to be used.
    * <p>
    * Will be initialized with a console progress monitor.
    * </p>
    */
   private IProgressMonitorToolset progressMonitor = new ConsoleProgressMonitorToolsetImpl();

   // implementation of the IRuntimeToolset and IDevelopmentToolset parts

   /**
    * Sets the verbose option for the whole toolset. If set to true, some useful
    * messages will be printed out.
    * 
    * <p>
    * The default is verbose = off.
    * </p>
    * 
    * @param onOff true for verbose equals on
    */
   public void setVerbose (boolean onOff)
   {
      this.verbose = onOff;
   }

   /**
    * Sets the output writer to be used for printing out verbose mnessages.
    * 
    * <p>
    * If not set, <code>System.out</code> will be used.
    * </p>
    * 
    * @param aStream the output stream
    */
   public void setVerboseStream (PrintStream aStream)
   {
      this.verboseStream = aStream;
   }

   /**
    * Sets the progress monitor to be used.
    * 
    * <p>
    * If not specified, a console based progress monitor will be used.
    * </p>
    * 
    * @param aProgressMonitor the progress monitor
    */
   public void setProgressMonitor (IProgressMonitorToolset aProgressMonitor)
   {
      this.progressMonitor = aProgressMonitor;
   }

   // protected methods

   /**
    * Gets the verbose option for the whole toolset.
    * 
    * <p>
    * The default is verbose = off.
    * </p>
    * 
    * @return true for verbose equals on
    */
   protected boolean getVerbose ()
   {
      return this.verbose;
   }

   /**
    * Gets the output writer to be used for printing out verbose mnessages.
    * 
    * <p>
    * If not set, <code>System.out</code> will be used.
    * </p>
    * 
    * @return the output stream
    */
   protected PrintStream getVerboseStream ()
   {
      return this.verboseStream;
   }

   /**
    * Get the current progress monitor.
    * 
    * @return the progress monitor
    */
   protected IProgressMonitorToolset getProgressMonitor ()
   {
      return this.progressMonitor;
   }

   /**
    * Prints out a verbose message
    * 
    * @param msg the message to print out
    */
   protected void printVerbose (String msg)
   {
      if ((this.verbose) && (this.verboseStream != null))
      {
         this.verboseStream.println(msg);
      }
   }

   // inner classes

   /**
    * Helper class to represent a classpath and lookup of classes based on this
    * classpath.
    * 
    * <p>
    * The implementation is based on lejos class <code>js.tools.ClassPath</code>.
    * </p>
    * 
    * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
    */
   public class Classpath
   {

      // attributes

      /** the classpath entries */
      private List entries;

      // constructor

      /**
       * Creates a new classpath based on a given string.
       * 
       * <p>
       * Will parse the classpath, and check for a valid format.
       * </p>
       * 
       * @param aClasspathString the classpath as a string
       */
      public Classpath (String aClasspathString)
      {
         // we hold all entries in an ordered list
         this.entries = new ArrayList();

         if (aClasspathString == null)
         {
            printVerbose("Warning: classpath is null");
            return;
         }
         // now parse the entries
         StringTokenizer parser = new StringTokenizer(aClasspathString,
            File.pathSeparator);
         while (parser.hasMoreTokens())
         {
            String entry = parser.nextToken();

            // parse zip and jars specially
            if (isZipJarEntry(entry))
            {
               try
               {
                  ZipFile zipFile = new ZipFile(entry);
                  entries.add(entry);
                  zipFile.close();
               }
               catch (ZipException e)
               {
                  printVerbose("Warning: Can't open zip/jar file: " + entry);
               }
               catch (IOException e)
               {
                  printVerbose("Warning: Can't open zip/jar file: " + entry);
               }
            }
            else
            {
               File dir = new File(entry);
               if (!dir.isDirectory())
               {
                  printVerbose("Warning: dir is not a valid directory: "
                     + entry);
               }
               else
               {
                  entries.add(entry);
               }
            }
         }
      }

      // public methods

      /**
       * Checks, whether a class exists within the classpath.
       * 
       * @param fqcn a full qualified class name, e.g "package1.package2.Class1"
       * @return true, if found on classpath
       */
      public boolean exists (String fqcn)
      {
         if (fqcn == null)
         {
            return false;
         }
         String className = fqcn.replace('.', '/') + ".class";
         Iterator iter = this.entries.iterator();
         while (iter.hasNext())
         {
            String entry = (String) iter.next();
            if (isZipJarEntry(entry))
            {

               ZipFile zipFile = null;
               try
               {
                  zipFile = new ZipFile(entry);
                  ZipEntry zipEntry = zipFile.getEntry(className);
                  if (zipEntry != null)
                  {
                     return true;
                  }
               }
               catch (IOException ex)
               {
                  // ignore
               }
               finally
               {
                  if (zipFile != null)
                  {
                     try
                     {
                        zipFile.close();
                     }
                     catch (IOException e)
                     {
                        // ignore in finally
                     }
                  }
               }
            }
            else if (isDirectory((entry)))
            {
               File dir = new File(entry);
               File classFile = new File(dir, className);
               if (classFile.exists())
               {
                  return true;
               }
            }
            else
            {
               printVerbose("Warning: Invalid entry in Classpath (Internal error)");
            }
         }
         // default: not found
         return false;
      }

      /**
       * Gets the input stream for a given class
       * 
       * <p>
       * Returns a stream to a file or a zip entry.
       * </p>
       * 
       * @param fqcn a full qualified class name, e.g "package1.package2.Class1"
       * @return an input stream or null, if not found
       * @throws IOException will be raised if input stream can not be opened
       */
      public InputStream getInputStream (String fqcn) throws IOException
      {
         String className = fqcn.replace('.', '/') + ".class";
         Iterator iter = this.entries.iterator();
         while (iter.hasNext())
         {
            String entry = (String) iter.next();
            if (isZipJarEntry(entry))
            {
               ZipFile zipFile = new ZipFile(entry);
               ZipEntry zipEntry = zipFile.getEntry(className);
               if (zipEntry != null)
               {
                  InputStream is = zipFile.getInputStream(zipEntry);
                  zipFile.close();
                  return is;
               }
            }
            else if (isDirectory(entry))
            {
               File dir = new File(entry);
               File classFile = new File(dir, className);
               if (classFile.exists())
               {
                  InputStream is = new FileInputStream(classFile);
                  return is;
               }
            }
            else
            {
               printVerbose("Warning: Invalid entry in Classpath (Internal error)");
            }
         }
         // default: not found
         return null;
      }

      /**
       * Default presentation of a classpath.
       * 
       * @return a string representation of a classpath
       */
      public String toString ()
      {
         StringBuffer sbuf = new StringBuffer();
         Iterator iter = this.entries.iterator();
         while (iter.hasNext())
         {
            String entry = (String) iter.next();
            sbuf.append(entry);
            sbuf.append(File.pathSeparator);
         }
         return sbuf.toString();
      }

      // private methods

      /**
       * Checks, whether an entry is a zip or a jar file.
       * 
       * @param entry the entry
       * @return true, if the entry ends with ".zip" or ".jar"
       */
      private boolean isZipJarEntry (String entry)
      {
         if (entry.endsWith(".zip") || entry.endsWith(".jar"))
         {
            return true;
         }
         return false;
      }

      /**
       * Checks, whether an entry is a directory
       * 
       * @param entry the entry
       * @return true, if the entry is a directory
       */
      private boolean isDirectory (String entry)
      {
         File f = new File(entry);
         return f.isDirectory();
      }
   }
}