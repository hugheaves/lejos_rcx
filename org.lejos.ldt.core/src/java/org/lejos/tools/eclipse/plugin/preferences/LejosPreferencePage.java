package org.lejos.tools.eclipse.plugin.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage </samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class LejosPreferencePage extends PreferencePage
   implements IWorkbenchPreferencePage
{
   private Combo _port;
   private Button _fastmode;

   public LejosPreferencePage ()
   {
      setPreferenceStore(LejosPlugin.getDefault().getPreferenceStore());
      setDescription("General settings for leJOS development:");
      initializeDefaults();
   }

   /**
    * Sets the default values of the preferences.
    */
   private void initializeDefaults ()
   {
      IPreferenceStore store = getPreferenceStore();
      // TODO fix defaults
      store.setDefault(LejosPreferences.P_PORT, LejosPreferences.D_PORT);
      store.setDefault(LejosPreferences.P_FASTMODE, LejosPreferences.D_PORT);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.preference.IPreferencePage#performOk()
    */
   public boolean performOk ()
   {
      IPreferenceStore store = getPreferenceStore();

      store.setValue(LejosPreferences.P_PORT, _port.getSelectionIndex());
      store.setValue(LejosPreferences.P_FASTMODE, _fastmode.getSelection());

      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
    */
   protected Control createContents (Composite parent)
   {
      IPreferenceStore store = getPreferenceStore();

      // TODO fix entries (linux, osx)
      Composite result = new Composite(parent, SWT.NULL);
      GridLayout resultLayout = new GridLayout(1, false);
      resultLayout.marginHeight = 0;
      resultLayout.marginWidth = 0;
  		result.setLayout(resultLayout);
      result.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
         true));
      result.setFont(parent.getFont());

      Group group = new Group(result, SWT.SHADOW_ETCHED_IN);
      group.setText("Tower");
      group.setLayout(new GridLayout(4, false));
      group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
         false));

      Label portLabel = new Label(group, SWT.NONE);
      portLabel.setText("Port: ");
      _port = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
      _port.setItems(new String[]
      {
         "USB", "COM1", "COM2", "COM3", "COM4"
      });
      _port.select(store.getInt(LejosPreferences.P_PORT));
      
      Label spacer = new Label(group, SWT.NONE);
  		spacer.setText(" ");

      _fastmode = new Button(group, SWT.CHECK | SWT.LEFT);
      _fastmode.setText("Fast mode");
      _fastmode.setSelection(store.getBoolean(LejosPreferences.P_FASTMODE));

      return result;
   }

   public void init (IWorkbench workbench)
   {}
}