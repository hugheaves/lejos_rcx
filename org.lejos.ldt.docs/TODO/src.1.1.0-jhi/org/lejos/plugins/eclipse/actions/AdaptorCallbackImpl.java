package org.lejos.plugins.eclipse.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.lejos.plugins.eclipse.adaptors.AdaptorCallback;

/**
 * Callback class, which will be called by the 
 * adaptor class, to notify about progress and 
 * probably cancel actions.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class AdaptorCallbackImpl implements AdaptorCallback {
	
	// attributes
	
	IProgressMonitor monitor;
	
	// constructors
	
	public AdaptorCallbackImpl (IProgressMonitor aMonitor) {
		this.monitor = aMonitor;
	}
	
	// implementation of AdaptorCallback

	/**
	 * checks, whether the function has be canceled, e.g. through 
	 * the ui.
	 * 
	 * @return true, if canceled
	 */	
	public boolean isCanceled () {
		return monitor.isCanceled();
	}
	
	/**
	 * indicates the progress, with a given message,
	 * and the progress.
	 * 
	 * @param msg a message
	 * @param progress 100 means complete
	 */
	public void progress (String msg, int progress) {
		// TODO
	}
}
