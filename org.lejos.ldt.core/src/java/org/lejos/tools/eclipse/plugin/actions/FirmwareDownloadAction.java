package org.lejos.tools.eclipse.plugin.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.EclipseProgressMonitorToolsetImpl;
import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Firmware download action.
 */
public class FirmwareDownloadAction extends AbstractAction
{
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run (IAction action)
  {
    Job job = new Job("leJOS firmware download")
    {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      protected IStatus run (IProgressMonitor monitor)
      {
        final EclipseToolsetFacade facade = new EclipseToolsetFacade();
        facade
            .setProgressMonitor(new EclipseProgressMonitorToolsetImpl(monitor));
        try
        {
          facade.installFirmware();
        }
        catch (ToolsetException e)
        {
          String pluginID = LejosPlugin.getDefault().getBundle()
              .getSymbolicName();
          return new Status(IStatus.ERROR, pluginID, -1, e.getMessage(), e);
        }

        return Status.OK_STATUS;
      }
    };
    job.setUser(true);
    job.schedule();
  }
}