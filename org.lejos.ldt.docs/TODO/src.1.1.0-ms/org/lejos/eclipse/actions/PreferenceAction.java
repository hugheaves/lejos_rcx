package org.lejos.eclipse.actions;
/*
* $Log$
* Revision 1.1  2003/11/14 10:47:34  jhiller
* Starting new development of v1.2.0 (Jochen Hiller)
*
* Revision 1.1  2003/10/05 16:34:06  mpscholz
* lejos plugin for Eclipse
*
*/

////////////////////////////////////////////////////////
/**
 *
 * This class is the preference action for the Lejos Plugin
 *
 * @author Christophe Ponsard
 * @version 1.1.0 
 *   
 */

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.lejos.eclipse.LejosPreference;

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
