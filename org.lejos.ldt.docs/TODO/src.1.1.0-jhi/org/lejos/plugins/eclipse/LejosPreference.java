package org.lejos.plugins.eclipse;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Insert the type's description here.
 * @see PreferencePage
 */
public class LejosPreference extends PreferencePage implements IWorkbenchPreferencePage {
	
	private LejosDirEditor fInstallDir;
	private Combo fPort;
	private Combo fSpeed;
		
	/**
	 * The constructor.
	 */
	public LejosPreference() {
	}

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench workbench)  {
		//Initialize the preference store we wish to use
		setPreferenceStore(LejosPlugin.getDefault().getPreferenceStore());		
	}

	protected void performDefaults() {
		fInstallDir.setStringValue(LejosPlugin.DEFAULT_LEJOS_PATH);
	}
	
	/** 
	 * Method declared on IPreferencePage. Save the
	 * author name to the preference store.
	 */
	public boolean performOk() {
		// perform checks
		String path=fInstallDir.getStringValue();

    	int port=fPort.getSelectionIndex();
	   	boolean isFast=(fSpeed.getSelectionIndex()==1);
		
		LejosPlugin.getDefault().setLejosPath(path);
		LejosPlugin.getDefault().setLejosPort(port);
		LejosPlugin.getDefault().setLejosIsFast(isFast);
		return super.performOk();
	}	
		
	// to get arount protected visibility of createContents
	public Control getContents(Composite parent) {
		 return createContents(parent);
	}

	/**
	 * Insert the method's description here.
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent)  {
		Composite composite= new Composite(parent, SWT.NONE);
		
		GridLayout gl= new GridLayout(3,false);
		gl.marginHeight=20;
		gl.marginWidth=20;
		gl.verticalSpacing=20;
		composite.setLayout(gl);

		// controls
		
/*		fInstallDir= new Text(composite, SWT.SINGLE | SWT.BORDER);		
		String path=LejosPlugin.getDefault().getPlatformLejosPath();
		fInstallDir.setText(path);
		
		Button fButton=new Button(composite, SWT.NONE);
		fButton.setText("Browse");

		fButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
try {				
				DirectoryDialog dd=new DirectoryDialog(getShell(),SWT.OPEN);
				dd.setFilterPath(fInstallDir.getText());
				String filename = dd.open();
				fInstallDir.setText(filename);
				checkValid(filename);
} catch (Exception ex) {
	LejosPlugin.debug(ex);
	ex.printStackTrace();
}				
			}
		});
*/

		fInstallDir=new LejosDirEditor("LejosDir", "leJOS installation directory", composite);
        fInstallDir.setStringValue(LejosPlugin.getDefault().getPlatformLejosPath());
				
		Label labelPort = new Label(composite, SWT.LEFT);
		labelPort.setText("RCX communication port");
		
		fPort=new Combo(composite,SWT.DROP_DOWN | SWT.READ_ONLY);
		fPort.setItems(LejosPlugin.getPorts());
		fPort.select(LejosPlugin.getDefault().getLejosPort());

		Label labelSpeed = new Label(composite, SWT.LEFT);
		labelSpeed.setText("Data transfert rate");
		
		fSpeed=new Combo(composite,SWT.DROP_DOWN | SWT.READ_ONLY);
		fSpeed.add("slow");
		fSpeed.add("fast");
		if (LejosPlugin.getDefault().getLejosIsFast())
			fSpeed.select(1);
		else
			fSpeed.select(0);
			
		// layout
//		data = new GridData(GridData.FILL_HORIZONTAL);
//		fInstallDir.getTextControl().setLayoutData(data);

//        GridData data = new GridData(GridData.FILL_HORIZONTAL);
//		data.horizontalSpan = 3;
//		fWarning.setLayoutData(data);
		
		GridData data = new GridData();
		labelPort.setLayoutData(data);
		
		data = new GridData();
		data.horizontalSpan = 2;
		fPort.setLayoutData(data);

		data = new GridData();
		labelSpeed.setLayoutData(data);

		data = new GridData();
		data.horizontalSpan = 2;
		fSpeed.setLayoutData(data);

		return composite;
	}
	
	class LejosDirEditor extends DirectoryFieldEditor {
		
		Label warning;
		
		LejosDirEditor(String name, String label, Composite parent) {
			super(name,label,parent);
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			warning=new Label(parent,SWT.LEFT);
	        GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 3;
			warning.setLayoutData(data);		
		}
		
		protected boolean doCheckState() {
			File file=new File(getStringValue(),"lib/classes.jar");
			boolean res=file.exists();

			if (res) 
				warning.setText("");
			else
				warning.setText("      >>>> Warning: invalid leJOS path <<<<");
			
			return res;
		}	
	}

}
