package org.lejos.tools.eclipse.plugin.launch;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

/**
 * Launch shortcut for running in emulator.
 */
public class RunRCXShortcut extends RunShortcut
{
   /**
    * Returns the local java launch config type.
    */
   protected ILaunchConfigurationType getLejosLaunchConfigType ()
   {
      ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
      return lm.getLaunchConfigurationType("org.lejos.ldt.core.run");
   }
}