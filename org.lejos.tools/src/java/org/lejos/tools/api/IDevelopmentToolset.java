package org.lejos.tools.api;

import java.io.PrintStream;

/**
 * This is the common interface to access the leJOS
 * development toolset.
 * 
 * <p>Will (in future) support services like
 * <code>GenerateConstants</code>, etc.</p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public interface IDevelopmentToolset {

	/**
	 * Sets ths verbose option for the whole toolset.
	 * If set to true, some useful messages will be printed out.
	 * 
	 * @param onOff true for verbose=on
	 */
	void setVerbose (boolean onOff);
	
	/**
	 * Sets ths output writer to be used for 
	 * printing out verbose mnessages.
	 * 
	 * <p>If not set, <code>System.out</code>
	 * will be used.</p>
	 * 
	 * @param out the output stream
	 */
	void setVerboseStream (PrintStream out);
	
	/**
	 * Sets the progress monitor to be used.
	 * 
	 * <p>
	 * If not specified, a console based progress monitor will be used.
	 * </p>
	 * 
	 * @param progressMonitor the progress monitor
	 */
	void setProgressMonitor(IProgressMonitorToolset progressMonitor);

	// TODO ENH Support CodePackager
	// TODO ENH Support GenerateConstants
}
