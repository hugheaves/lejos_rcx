package org.lejos.tools.eclipse.plugin.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.lejos.tools.api.IRuntimeToolset;
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
      "usb", "com1", "com2", "com3", "com4"
   };
   private static final String[] D_PORTS_LINUX = new String[]
   {
      "usb", "/dev/ttyS0", "/dev/ttyS1", "/dev/ttyS2", "/dev/ttyS3"
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
   public String[] getDefaultClasspathEntries ()
   {
      return new String[]
      {
         "lib/classes.jar", "lib/rcxcomm.jar"
      };
   }

   public String[] getClientClasspathEntries ()
   {
      return new String[]
      {
         "lib/pcrcxcomm.jar"
      };
   }

   //
   // protected interface
   // 

   /**
    * Get preference store.
    */
   protected static IPreferenceStore getPreferenceStore ()
   {
      return LejosPlugin.getDefault().getPreferenceStore();
   }
}