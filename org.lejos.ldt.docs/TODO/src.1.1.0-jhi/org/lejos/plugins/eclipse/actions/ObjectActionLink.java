package org.lejos.plugins.eclipse.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.lejos.plugins.eclipse.LejosPlugin;

/**
 * Object action delegate to link a leJOS program.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class ObjectActionLink extends AbstractAction implements IObjectActionDelegate {

	// attributes

	private IWorkbenchPart fWorkbenchPart;
	private ISelection fSelection;

	// constructors

	public ObjectActionLink() {
	}

	// Implementation of IObjectActionDelegate

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fWorkbenchPart = targetPart;
	}

	// Implementation of IActionDelegate

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

	public void run(IAction action) {
		LejosPlugin.debug(String.valueOf(fSelection));
		IStructuredSelection selection = (IStructuredSelection) fSelection;

		Object obj = selection.getFirstElement();
		if (! (obj instanceof IJavaElement)) {
			LejosPlugin.debug("selected object == null");
			return;
		}
		IJavaElement javaElem = (IJavaElement) obj;
		ICompilationUnit[] cus = collectLinkClasses(javaElem);

		Shell shell = fWorkbenchPart.getSite().getShell();
		if (cus.length > 0) {
			try {
				// and now run for all compilation units
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				IRunnableWithProgress worker = new LinkWorker(cus);
				dialog.run (true, true, worker);
			} catch (InvocationTargetException e) {
				LejosPlugin.debug (e);
			} catch (InterruptedException e) {
				LejosPlugin.debug (e);
			}
		} else {
			MessageDialog.openWarning(
				shell,
				"Linking leJOS applications", 
				"No applications with main found");
		}
		
	}
}
