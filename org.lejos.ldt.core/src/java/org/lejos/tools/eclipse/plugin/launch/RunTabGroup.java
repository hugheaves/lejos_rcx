package org.lejos.tools.eclipse.plugin.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;

/**
 * leJOS tab group
 */
public class RunTabGroup
    extends
      AbstractLaunchConfigurationTabGroup
{
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
   *      java.lang.String)
   */
  public void createTabs (ILaunchConfigurationDialog dialog, String mode)
  {
    ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[]
    {new JavaMainTab(), new JavaArgumentsTab(), new JavaJRETab(),
        new JavaClasspathTab(), new SourceLookupTab(), new EnvironmentTab(),
        new CommonTab()};
    setTabs(tabs);
  }
}
