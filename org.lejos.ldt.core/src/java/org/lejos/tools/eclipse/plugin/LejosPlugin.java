package org.lejos.tools.eclipse.plugin;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class LejosPlugin extends AbstractUIPlugin {

    // static attributes

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
     * @param descriptor
     *            the plugin descriptor
     */
    public LejosPlugin() {
        super();
        plugin = this;
        try {
            resourceBundle = ResourceBundle
                    .getBundle("org.lejos.tools.eclipse.plugin.LejosPluginResources");
        } catch (MissingResourceException x) {
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
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    // static methods

    /**
     * Returns the shared instance.
     * 
     * @return the default lejos plugin
     */
    public static LejosPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the workspace instance.
     * 
     * @return the default workspace
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the lejos prefernces.
     * 
     * @return the lejos preferences bound to this plugin
     */
    public static LejosPreferences getPreferences() {
        return plugin.preferences;
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not
     * found.
     * 
     * @param key
     *            the resource key
     * @return a resource string for the given key
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = LejosPlugin.getDefault().getResourceBundle();
        try {
            if (bundle != null) {
                return bundle.getString(key);
            } else {
                return key;
            }
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * debugs some message, if plugin debugging is enabled.
     * 
     * @param msg
     *            the debug message
     */
    public static void debug(String msg) {
        if (plugin.isDebugging()) {
            System.out.println(LejosPlugin.class.getName() + ": " + msg);
        }
    }

    /**
     * debugs a <oode>Throwable</code>, if plugin debugging is enabled.
     * 
     * @param t
     *            the <oode>Throwable</code>
     */
    public static void debug(Throwable t) {
        if (plugin.isDebugging()) {
            System.out.println(LejosPlugin.class.getName()
                    + ": Exception occured: " + t.toString());
        }
    }
}