package org.lejos.tools.eclipse.plugin;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * The <code>EclipseProgressMonitorToolsetImpl</code> provides the
 * implementation of a progress monitor within the Eclipse environment.
 * 
 * <p>
 * The implementation is based on the existig Eclipse classes.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class EclipseProgressMonitorToolsetImpl
    implements
      IProgressMonitorToolset
{
  //
  // attributes
  //

  /**
   * the dialog which will be poped up
   */
  private Display _display;
  private ProgressMonitorDialog _dialog;

  //
  // constructor
  //
  
  /**
   * Default constructor.
   * <p>
   * Will create a dialog object.
   * </p>
   */
  public EclipseProgressMonitorToolsetImpl()
  {
    this(new ProgressMonitorDialog(new Shell()));
  }

  /**
   * Constructor for a given dialog.
   * <p>
   * Will create shell. Dialog will be reused.
   * </p>
   * 
   * @param dialog the dialog to reuse
   */
  public EclipseProgressMonitorToolsetImpl(ProgressMonitorDialog dialog)
  {
    _dialog = dialog;
    _dialog.open();
    _display = dialog.getShell().getDisplay();
  }

  /**
   * Get the created dialog object.
   * 
   * @return the dialog object
   */
  public Dialog getDialog ()
  {
    return _dialog;
  }

  /**
   * Implementation of <code>beginTask()</code>.
   * 
   * <p>
   * Will be redirected to the Eclipse progress monitor, which is attached to
   * the progress monitor dialog.
   * </p>
   * 
   * @param name the name of the task
   * @param totalWork the total work for this task
   */
  public void beginTask (final String name, final int totalWork)
  {
    _display.asyncExec(new Runnable()
    {
      public void run ()
      {
        _dialog.getProgressMonitor().beginTask(name, totalWork);
      }
    });
  }

  /**
   * Implementation of <code>done()</code>.
   * <p>
   * Will be redirected to the Eclipse progress monitor, which is attached to
   * the progress monitor dialog.
   * </p>
   */
  public void done ()
  {
    _display.asyncExec(new Runnable()
    {
      public void run ()
      {
        _dialog.getProgressMonitor().done();
      }
    });
  }

  /**
   * Implementation of <code>isCanceled()</code>.
   * <p>
   * Will be redirected to the Eclipse progress monitor, which is attached to
   * the progress monitor dialog.
   * </p>
   * 
   * @return true, if this progress monitor has been canceled.
   */
  public boolean isCanceled ()
  {
    return _dialog.getProgressMonitor().isCanceled();
  }

  /**
   * Implementation of <code>setCanceled()</code>.
   * <p>
   * Will be redirected to the Eclipse progress monitor, which is attached to
   * the progress monitor dialog.
   * </p>
   * 
   * @param value true, if progress monitor has be canceled
   */
  public void setCanceled (final boolean value)
  {
    _display.asyncExec(new Runnable()
    {
      public void run ()
      {
        _dialog.getProgressMonitor().setCanceled(value);
      }
    });
  }

  /**
   * Implementation of <code>worked()</code>.
   * <p>
   * Will be redirected to the Eclipse progress monitor, which is attached to
   * the progress monitor dialog.
   * </p>
   * 
   * @param work the currently part of work been done
   */
  public void worked (final int work)
  {
    _display.asyncExec(new Runnable()
    {
      public void run ()
      {
        _dialog.getProgressMonitor().worked(work);
      }
    });
  }
}