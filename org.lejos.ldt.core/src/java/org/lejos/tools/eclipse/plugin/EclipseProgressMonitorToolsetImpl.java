package org.lejos.tools.eclipse.plugin;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * The <code>EclipseProgressMonitorToolsetImpl</code> provides the
 * implementation of a progress monitor within the Eclipse environment.
 * 
 * <p>
 * The implementation is based on the existig Eclipse classes.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class EclipseProgressMonitorToolsetImpl
	implements IProgressMonitorToolset {

	// attributes

	/** a dialog which will be poped up */
	private ProgressMonitorDialog dialog;

	// constructor

	/**
	 * Default constructor.
	 * 
	 * <p>
	 * Will create a dialog object.
	 * </p>
	 */
	public EclipseProgressMonitorToolsetImpl() {
		Shell shell = new Shell();
		this.dialog = new ProgressMonitorDialog(shell);
		this.dialog.open();
	}

	/**
	 * Constructor for a given dialog.
	 * 
	 * <p>
	 * Will create shell. Dialog will be reused.
	 * </p>
	 * 
	 * @param aDialog the dialog to reuse 
	 */
	public EclipseProgressMonitorToolsetImpl(ProgressMonitorDialog aDialog) {
		this.dialog = aDialog;
	}

	/**
	 * Get the created dialog object.
	 * 
	 * @return the dialog object
	 */
	public Dialog getDialog() {
		return this.dialog;
	}

	/**
	 * Implementazion of <code>beginTask()</code>.
	 * 
	 * <p>
	 * Will be redirected to the Eclipse progress monitor, which is attached to
	 * the progress monitor dialog.
	 * </p>
	 * 
	 * @param name the name of the task
	 * @param totalWork the total work for this task
	 */
	public void beginTask(String name, int totalWork) {
		this.dialog.getProgressMonitor().beginTask(name, totalWork);
	}

	/**
	 * Implementazion of <code>done()</code>.
	 * 
	 * <p>
	 * Will be redirected to the Eclipse progress monitor, which is attached to
	 * the progress monitor dialog.
	 * </p>
	 */
	public void done() {
		this.dialog.getProgressMonitor().done();
	}

	/**
	 * Implementazion of <code>isCanceled()</code>.
	 * 
	 * <p>
	 * Will be redirected to the Eclipse progress monitor, which is attached to
	 * the progress monitor dialog.
	 * </p>
	 * 
	 * @return true, if this progress monitor has been canceled.
	 */
	public boolean isCanceled() {
		return this.dialog.getProgressMonitor().isCanceled();
	}

	/**
	 * Implementazion of <code>setCanceled()</code>.
	 * 
	 * <p>
	 * Will be redirected to the Eclipse progress monitor, which is attached to
	 * the progress monitor dialog.
	 * </p>
	 * 
	 * @param value true, if progress monitor has be canceled
	 */
	public void setCanceled(boolean value) {
		this.dialog.getProgressMonitor().setCanceled(value);
	}

	/**
	 * Implementazion of <code>worked()</code>.
	 * 
	 * <p>
	 * Will be redirected to the Eclipse progress monitor, which is attached to
	 * the progress monitor dialog.
	 * </p>
	 * 
	 * @param work the currently part of work been done
	 */
	public void worked(int work) {
		this.dialog.getProgressMonitor().worked(work);
	}
}
