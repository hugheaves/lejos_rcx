package org.lejos.tools.eclipse.plugin;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.lejos.tools.eclipse.plugin.preferences.LejosPreferences;

/**
 * The main plugin class to be used in the desktop.
 */
public class LejosPlugin extends AbstractUIPlugin
{

   // static attributes

   /** the nature id. */
   public static final String LEJOS_NATURE = "org.lejos.ldt.core.lejosNature";

   /** the builder id. */
   public static final String LEJOS_BUILDER = "org.lejos.ldt.core.lejosBuilder";

   /** The shared instance. */
   private static LejosPlugin plugin;

   // attributes

   /** the Resource bundle. */
   private ResourceBundle resourceBundle;

   /** the lejos preferences. */
   private LejosPreferences preferences;

   // constructors

   /**
    * The constructor.
    * 
    * @param descriptor the plugin descriptor
    */
   public LejosPlugin ()
   {
      super();
      plugin = this;
      try
      {
         resourceBundle = ResourceBundle
            .getBundle("org.lejos.tools.eclipse.plugin.LejosPluginResources");
      }
      catch (MissingResourceException x)
      {
         resourceBundle = null;
      }

      // handle preferences. very simple for the moment
      // TODO extend using plugin store
      this.preferences = new LejosPreferences();
   }

   // public methods

   /**
    * Returns the plugin's resource bundle.
    * 
    * @return a resource bundle
    */
   public ResourceBundle getResourceBundle ()
   {
      return resourceBundle;
   }

   // static methods

   /**
    * Returns the shared instance.
    * 
    * @return the default lejos plugin
    */
   public static LejosPlugin getDefault ()
   {
      return plugin;
   }

   /**
    * Returns the id of this plugin.
    * 
    * @return the plugin id
    */
   public static String getId ()
   {
      return getDefault().getBundle().getSymbolicName();
   }

   /**
    * Returns the workspace instance.
    * 
    * @return the default workspace
    */
   public static IWorkspace getWorkspace ()
   {
      return ResourcesPlugin.getWorkspace();
   }

   /**
    * Returns the lejos prefernces.
    * 
    * @return the lejos preferences bound to this plugin
    */
   public static LejosPreferences getPreferences ()
   {
      return plugin.preferences;
   }

   /**
    * Returns the string from the plugin's resource bundle, or 'key' if not
    * found.
    * 
    * @param key the resource key
    * @return a resource string for the given key
    */
   public static String getResourceString (String key)
   {
      ResourceBundle bundle = LejosPlugin.getDefault().getResourceBundle();
      try
      {
         if (bundle != null)
         {
            return bundle.getString(key);
         }
         else
         {
            return key;
         }
      }
      catch (MissingResourceException e)
      {
         return key;
      }
   }

   /**
    * debugs some message, if plugin debugging is enabled.
    * 
    * @param msg the debug message
    */
   public static void debug (String msg)
   {
      if (plugin.isDebugging())
      {
         System.out.println(LejosPlugin.class.getName() + ": " + msg);
      }
   }

   /**
    * debugs a <oode>Throwable</code>, if plugin debugging is enabled.
    * 
    * @param t the <oode>Throwable</code>
    */
   public static void debug (Throwable t)
   {
      if (plugin.isDebugging())
      {
         System.out.println(LejosPlugin.class.getName()
            + ": Exception occured: " + t.toString());
      }
   }

   /**
    * sets a project's nature to "leJOS project"
    * 
    * @param IProject the project
    * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
    * @throws CoreException
    */
   public static void addLeJOSNature (IProject aProject)
   {
      try
      {
         IProjectDescription description = aProject.getDescription();
         String[] natures = description.getNatureIds();
         String[] newNatures = new String[natures.length + 1];
         System.arraycopy(natures, 0, newNatures, 0, natures.length);
         newNatures[natures.length] = LEJOS_NATURE;
         description.setNatureIds(newNatures);
         aProject.getProject().setDescription(description, null);
      }
      catch (CoreException e)
      {
         LejosPlugin.debug(e);
         e.printStackTrace();
      }
   }

   /**
    * checks a project for leJOS nature
    * 
    * @param IProject the project
    * @return boolean true, if the project has leJOS nature
    * @throws CoreException
    * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
    */
   public static boolean checkForLeJOSNature (IProject aProject)
      throws CoreException
   {
      // check project's natures
      IProjectDescription description = aProject.getDescription();
      String[] natures = description.getNatureIds();
      for (int i = 0; i < natures.length; i++)
      {
         if (natures[i].equals(LEJOS_NATURE))
            return true;
      }
      return false;
   }
}