package org.lejos.tools.eclipse.plugin;

import org.lejos.tools.api.IRuntimeToolset;

/**
 * The <code>LejosPreferences</code> holds all default and user settings.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class LejosPreferences {

	/**
	 * Get the lejos home directory
	 * 
	 * TODO must be implemented. Return file instead ?
	 * 
	 * @return the lejos home, installation directory
	 */
	public String getLejosHome() {
		return null;
	}

	/**
	 * Get the currently selected link method.
	 * 
	 * @see IRuntimeToolset#LINK_METHOD_ALL
	 * @see IRuntimeToolset#LINK_METHOD_OPTIMIZING
	 * @return a link method
	 */
	public int getLinkMethod() {
		return IRuntimeToolset.LINK_METHOD_OPTIMIZING;
	}

	/**
	 * Get the extension for all binary files
	 * 
	 * @return ".leJOS"
	 */
	public String getExtensionBinary() {
		return ".leJOS";
	}

	/**
	 * Get the extension for the signature file
	 * 
	 * @return ".leJOS.signature"
	 */
	public String getExtensionSignature() {
		return ".leJOS.signature";
	}

	/**
	 * The flag, whether signature files have to be created or not.
	 * 
	 * @return currently, fix returns true
	 */
	public boolean isCreateSignatureFile() {
		return true;
	}

	/**
	 * Get the default classpath entries
	 * 
	 * @return an array with all classpath entries
	 */
	public String[] getDefaultClasspathEntries() {
		return new String[] {
			"lib/classes.zip",
			"lib/rcxrcxcomm.jar" };
	}
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * delivers the path to tools.jar of SUN's compiler
     * @return Path the path to tools.jar of SUN's compiler
   * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz</a>
   */     
//    public Path getCompilerJarPath() {
//        LejosPlugin.getDefault().getWorkbench().
//    } // getCompilerJarPath()
    
}
