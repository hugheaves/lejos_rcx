package org.lejos.tools.eclipse.plugin.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * The <code>LejosPreferences</code> holds all default and user settings.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosPreferences
{
   public static final String P_PORT = "org.lejos.port";
   public static final int D_PORT = 0;
   public static final String P_FASTMODE = "org.lejos.fastmode";
   public static final boolean D_FASTMODE = false;

   private static final String[] D_PORTS_WIN = new String[]
   {
      "usb", "usb1", "usb2", "usb3", "usb4", "com1", "com2", "com3", "com4"
   };
   private static final String[] D_PORTS_LINUX = new String[]
   {
      "usb", "usb0", "usb1", "usb2", "usb3", "/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3"
   };

   //
   // tower properties
   //

   /**
    * Get all possible tower ports.
    */
   public static String[] getPorts ()
   {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.startsWith("win"))
      {
         return D_PORTS_WIN;
      }
      else if (os.startsWith("linux"))
      {
         return D_PORTS_LINUX;
      }
      else
      {
         return new String[]
         {
            "<unknown>"
         };
      }
   }

   /**
    * Get tower port.
    */
   public static String getPort ()
   {
      return getPorts()[getPortNumber()];
   }

   /**
    * Get tower port index.
    */
   public static int getPortNumber ()
   {
      int result = getPreferenceStore().getInt(P_PORT);
      if (result >= getPorts().length)
      {
         result = 0;
      }

      assert result >= 0 && result < getPorts().length: "Postcondition: result >= 0 && result < getPorts().length";
      return result;
   }

   /**
    * Set tower port index.
    */
   public static void setPortNumber (int port)
   {
      assert port < getPorts().length: "Precondition: port < getPorts().length";
      getPreferenceStore().setValue(P_PORT, port);
   }

   /**
    * Get fast mode.
    */
   public static boolean isFastMode ()
   {
      return getPreferenceStore().getBoolean(P_FASTMODE);
   }

   /**
    * Set fast mode.
    */
   public static void setFastMode (boolean fastMode)
   {
      getPreferenceStore().setValue(P_FASTMODE, fastMode);
   }

   //
   // general properties
   //

   /**
    * Get the lejos home directory
    * 
    * TODO must be implemented. Return file instead ?
    * 
    * @return the lejos home, installation directory
    */
   public String getLejosHome ()
   {
      return null;
   }

   /**
    * Get the currently selected link method.
    * 
    * @see IRuntimeToolset#LINK_METHOD_ALL
    * @see IRuntimeToolset#LINK_METHOD_OPTIMIZING
    * @return a link method
    */
   public int getLinkMethod ()
   {
      return IRuntimeToolset.LINK_METHOD_OPTIMIZING;
   }

   /**
    * Get the extension for all binary files
    * 
    * @return ".leJOS"
    */
   public String getExtensionBinary ()
   {
      return ".leJOS";
   }

   /**
    * Get the extension for the signature file
    * 
    * @return ".leJOS.signature"
    */
   public String getExtensionSignature ()
   {
      return ".leJOS.signature";
   }

   /**
    * The flag, whether signature files have to be created or not.
    * 
    * @return currently, fix returns true
    */
   public boolean isCreateSignatureFile ()
   {
      return true;
   }

   /**
    * Get the default classpath entries
    * 
    * @return an array with all classpath entries
    */
   public String[] getRCXClasspathEntriesString ()
   {
      return new String[]
      {
         "lib/classes.jar", "lib/rcxcomm.jar"
      };
   }

   /**
    * Get an array with CLASSPATH entries for an leJOS RCX project.
    * 
    * @return an arry with classpath entries
    */
   public IClasspathEntry[] getRCXClasspathEntries ()
   {
      // classes.jar, with sources attached
      IPath theJar = EclipseUtilities.findFileInPlugin("org.lejos",
         "lib/classes.jar");
      IClasspathEntry theClassesJarEntry = null;
      if (theJar != null)
      {
         theClassesJarEntry = JavaCore.newLibraryEntry(theJar, EclipseUtilities
            .findFileInPlugin("org.lejos", "src/classes-src.zip"),
            new Path("/"));
      }
      else
      {
         LejosPlugin
            .debug("Could not create a classpath entry for classes.jar");
      }

      // rcxcomm.jar, with sources attached
      theJar = EclipseUtilities
         .findFileInPlugin("org.lejos", "lib/rcxcomm.jar");
      IClasspathEntry theRcxcommJar = null;
      if (theJar != null)
      {
         theRcxcommJar = JavaCore.newLibraryEntry(theJar, EclipseUtilities
            .findFileInPlugin("org.lejos", "src/rcxcomm-src.zip"),
            new Path("/"));
      }
      else
      {
         LejosPlugin
            .debug("Could not create a classpath entry for rcxcomm.jar");
      }

      // create array in required size, and fill dependent of result
      IClasspathEntry[] theEntries;
      if ((theClassesJarEntry != null) && (theRcxcommJar != null))
      {
         theEntries = new IClasspathEntry[2];
         theEntries[0] = theClassesJarEntry;
         theEntries[1] = theRcxcommJar;
      }
      else if (theClassesJarEntry != null)
      {
         theEntries = new IClasspathEntry[1];
         theEntries[0] = theClassesJarEntry;
      }
      else if (theRcxcommJar != null)
      {
         theEntries = new IClasspathEntry[1];
         theEntries[0] = theRcxcommJar;
      }
      else
      {
         theEntries = new IClasspathEntry[0];
      }
      return theEntries;
   }

   public String[] getClientClasspathEntriesString ()
   {
      return new String[]
      {
         "lib/pcrcxcomm.jar"
      };
   }

   /**
    * Get an array with CLASSPATH entries for an leJOS client project.
    * 
    * @return an arry with classpath entries
    */
   public IClasspathEntry[] getClientClasspathEntries ()
   {
      // pcrcxcomm.jar, with sources attached
      IPath theJar = EclipseUtilities.findFileInPlugin("org.lejos",
         "lib/pcrcxcomm.jar");
      IClasspathEntry theEntry = null;
      if (theJar != null)
      {
         theEntry = JavaCore.newLibraryEntry(theJar, EclipseUtilities
            .findFileInPlugin("org.lejos", "src/pcrcxcomm-src.zip"), new Path(
            "/"));
      }
      else
      {
         LejosPlugin
            .debug("Could not create a classpath entry for pcrcxcomm.jar");
      }

      // create array in required size, and fill dependent of result
      IClasspathEntry[] theEntries;
      if (theEntry != null)
      {
         theEntries = new IClasspathEntry[1];
         theEntries[0] = theEntry;
      }
      else
      {
         theEntries = new IClasspathEntry[0];
      }
      return theEntries;
   }

   // protected interface

   /**
    * Get preference store.
    */
   protected static IPreferenceStore getPreferenceStore ()
   {
      return LejosPlugin.getDefault().getPreferenceStore();
   }
}