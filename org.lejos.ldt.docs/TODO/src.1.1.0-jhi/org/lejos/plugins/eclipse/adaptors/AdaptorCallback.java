package org.lejos.plugins.eclipse.adaptors;

/**
 * Callback class, which will be called by the 
 * adaptor class, to notify about progress and 
 * probably cancel actions.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public interface AdaptorCallback {

	/**
	 * checks, whether the function has be canceled, e.g. through 
	 * the ui.
	 * 
	 * @return true, if canceled
	 */	
	boolean isCanceled ();
	
	/**
	 * indicates the progress, with a given message,
	 * and the progress.
	 * 
	 * @param msg a message
	 * @param progress 100 means complete
	 */
	void progress (String msg, int progress);
}
