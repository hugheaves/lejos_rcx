package org.lejos.tools.eclipse.plugin.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.EclipseProgressMonitorToolsetImpl;
import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;

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
    final EclipseToolsetFacade facade = new EclipseToolsetFacade();
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
    facade.setProgressMonitor(new EclipseProgressMonitorToolsetImpl(dialog));

    try
    {
      // fork as process, allow cancel
      dialog.run(true, true, new IRunnableWithProgress()
      {
        public void run (IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException
        {
          try
          {
            facade.installFirmware();
          }
          catch (ToolsetException e)
          {
            e.printStackTrace();
            throw new InvocationTargetException(e);
          }
        }
      });
    }
    catch (InvocationTargetException e)
    {
      MessageDialog.openError(getShell(), "Firmware download failed", e.getCause().getMessage());
    }
    catch (InterruptedException e)
    {
      MessageDialog.openError(getShell(), "Firmware download cancelled", "Firmware download cancelled");
    }
  }
}