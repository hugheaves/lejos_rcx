package org.lejos.tools.eclipse.plugin.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;

/**
 * Run on RCX launch delegate.
 */
public class RunDelegate
    extends
      AbstractJavaLaunchConfigurationDelegate
{
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
   *      java.lang.String, org.eclipse.debug.core.ILaunch,
   *      org.eclipse.core.runtime.IProgressMonitor)
   */
  public void launch (ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException
  {
    System.out.println("launch");
  }
}