package org.lejos.plugins.eclipse.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.lejos.plugins.eclipse.LejosPlugin;

/**
 * Action to link a leJOS program.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class LejosLinkAction
	extends AbstractAction
	implements IEditorActionDelegate {

	// attributes

	private ISelection fSelection;
	private IEditorPart editor;

	// constructors

	public LejosLinkAction() {
	}

	// Implementation of IActionDelegate

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}

	public void run(IAction action) {
		Shell shell = this.editor.getSite().getShell();
		ICompilationUnit cu;
		try {
			cu = LejosPlugin.getDefault().getCurrentCompilationUnit();
		} catch (IOException e) {
			LejosPlugin.debug(e);
			return;
		}
		if (hasMain(cu)) {
			IRunnableWithProgress worker = new LinkWorker(cu);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			try {
				dialog.run(true, true, worker);
			} catch (InvocationTargetException e1) {
				LejosPlugin.debug(e1);
				return;
			} catch (InterruptedException e1) {
				LejosPlugin.debug(e1);
				return;
			}
		} else {
			MessageDialog.openWarning(
				shell,
				"Linking leJOS applications",
				"No applications with main found");
		}
	}

	// implementation of IEditorActionDelegate

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}
}
