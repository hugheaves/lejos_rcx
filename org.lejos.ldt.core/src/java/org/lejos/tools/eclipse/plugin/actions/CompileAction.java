package org.lejos.tools.eclipse.plugin.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.lejos.tools.eclipse.plugin.*;

/**
 * represents a compile action for object for the leJOS plugin.
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz</a>
 */
public class CompileAction
	implements IObjectActionDelegate, IWorkbenchWindowActionDelegate {

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    // fields
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
	/** the current selection */
	private ISelection fSelection = null;

	/** an workbench part */
	private IWorkbenchPart fWorkbenchPart = null;

	/** a workbench window */
	private IWorkbenchWindow fWorkbenchWindow = null;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	// implementations of <IWorkbenchWindowActionDelegate>,
    // <IObjectActionDelegate> and <IActionDelegate>
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Disposes this action delegate. The implementor should unhook any
	 * references to itself so that garbage collection can occur.
	 */
	public void dispose() {
		// nothing to dispose of here
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Stores the current workbench window for later usage.
	 * @param aWindow the window that provides the context for this delegate
	 */
	public void init(IWorkbenchWindow aWindow) {
		this.fWorkbenchWindow = aWindow;
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the active part for the delegate
	 * @param action the action proxy that handles presentation portion of the
	 *            action
	 * @param targetPart the new part target
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.fWorkbenchPart = targetPart;
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * runs the compile action
	 * <p>
	 * This method will check, whether there are compilation units selected. If
	 * not, a message will be displayed, who indicates "nosource files" available.
	 * If there are compilation units, the compile will be started for each of them.
	 * </p>
	 * 
	 * @param action the action proxy that handles the presentation portion of
	 *            the action
	 */
	public void run(IAction action) {
		// is there a structured selection?
		if (!(this.fSelection instanceof IStructuredSelection)) {
			return;
		} // if
        // get selection
		final IJavaElement[] elems = getSelectedJavaElements(this.fSelection);
        // some sources selected that can be compiled?
		final EclipseToolsetFacade facade = new EclipseToolsetFacade();
		try {
			int n = facade.countCU(elems);
			if (n == 0) {
                // no sources found
				MessageDialog.openInformation(
					null,
					null,
                    // TODO internationalization - use resorce bundle here 
					"No java source files to compile found. "
						+ "Please check whether the selected elements "
						+ "have a \"main(String[])\" method.");
			} else {
				// create a shell based on the current workbench window
				Shell shell = null;
				if (this.fWorkbenchPart != null) {
					shell = this.fWorkbenchPart.getSite().getShell();
				} //if
				if (this.fWorkbenchWindow != null) {
					shell = this.fWorkbenchWindow.getShell();
				} //if
                // create progress monitor
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				facade.setProgressMonitor(
					new EclipseProgressMonitorToolsetImpl(dialog));
				// run dialog
				dialog.run(false, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
						try {
                            // compile elements
							facade.compileJavaElements(elems,LejosPlugin.getPreferences());
						} catch (Exception ex) {
							ex.printStackTrace();
                            // show error
                            // TODO write to console
                            //TODO invalid source file in package tree
                            MessageDialog.openInformation(null, null,
                                    // TODO internationalization - use resorce bundle here 
                                    "error: " + ex.getMessage());
                        } // catch
					} // run
				}); // new IRunnableWithProgress
			} // run
		} catch (JavaModelException ex) {
			MessageDialog.openError(null,null,
                // TODO internationalization - use resorce bundle here 
            "Internal error: Could not determine the sources to compile");
		} catch (InvocationTargetException ex) {
            // something went wrong
			MessageDialog.openError(null,null,
                // TODO internationalization - use resorce bundle here 
                "Internal error: (" + ex.toString() + ") occured");
		} catch (InterruptedException ex) {
			MessageDialog.openError(null,null,
				"Internal error: (" + ex.toString() + ") occured");
		} // catch
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * selection changed
	 * 
	 * <p>
	 * accepted selections: of types
	 * <ul>
	 * <li>@{link org.eclipse.jdt.core.IJavaElement#PACKAGE_FRAGMENT_ROOT}
	 * </li>
	 * <li>@{link org.eclipse.jdt.core.IJavaElement#PACKAGE_FRAGMENT}</li>
	 * <li>@{link org.eclipse.jdt.core.IJavaElement#COMPILATION_UNIT}</li>
	 * <li>@{link org.eclipse.jdt.core.IJavaElement#TYPE}</li>
	 * </ul>
     * only.
	 * </p>
	 * @param action the action proxy that handles presentation portion of the
	 *            action
	 * @param aSelection the current selection, or <code>null</code> if there
	 *            is no selection.
	 */
	public void selectionChanged(IAction action, ISelection aSelection) {
		this.fSelection = aSelection;
		// we requires a structured selection
		if (!(this.fSelection instanceof IStructuredSelection)) {
			return;
		} //if
		IJavaElement[] elems = getSelectedJavaElements(this.fSelection);
		// now check for valid types
		for (int i = 0; i < elems.length; i++) {
			IJavaElement elem = elems[i];
			boolean enabled = true;
			// all other types are INVALID
			if (!(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
				&& !(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
				&& !(elem.getElementType() == IJavaElement.COMPILATION_UNIT)
				&& !(elem.getElementType() == IJavaElement.TYPE)) {
				enabled = false;
			} // if
			action.setEnabled(enabled);
		} // for
	}

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	// private methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Get structured selections from the current selection.
	 * @param aSelection the current selection
	 * @return an array with all java elements selected. Is always not null. If
	 *         no valid structured selections are available, an array with size =
	 *         0 will be returned.
	 */
	private IJavaElement[] getSelectedJavaElements(ISelection aSelection) {
		IStructuredSelection structured = (IStructuredSelection) aSelection;
		Object[] oElems = structured.toArray();
		// copy into type safe array
		IJavaElement[] elems = new IJavaElement[oElems.length];
		for (int i = 0; i < oElems.length; i++) {
			elems[i] = (IJavaElement) oElems[i];
		} //for
		return elems;
	}
}
