package org.lejos.plugins.eclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.lejos.plugins.eclipse.LejosPreference;

/**
 * @author Baldus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PreferenceAction implements IEditorActionDelegate {

	private IEditorPart editor;
	
	public PreferenceAction() {
	}
	
	public void run(IAction action)  {
		PreferenceDialog dialog=new PreferenceDialog(editor.getSite().getShell());
		dialog.open();
	}
			
	public void selectionChanged(IAction action, ISelection selection)  {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor)  {
		editor=targetEditor;
	}
	
	// prefrence dialog
	class PreferenceDialog extends Dialog {
		
		LejosPreference pref;
		
		PreferenceDialog(Shell shell) {
			super(shell);
			pref=new LejosPreference();
		}
		
		protected void okPressed() {
			pref.performOk();
			super.okPressed();
		}
		
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("leJOS preferences");
		}
		
		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite)super.createDialogArea(parent);			
			return pref.getContents(composite);
		}
		
	}	
}
