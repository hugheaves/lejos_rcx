package org.lejos.tools.eclipse.plugin;

import org.eclipse.core.runtime.IProgressMonitor;
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
public class EclipseProgressMonitorToolsetImpl implements IProgressMonitorToolset
{
  //
  // attributes
  //

  /**
   * the progress monitor
   */
  private IProgressMonitor _progress;

  //
  // constructor
  //

  /**
   * Default constructor.
   * 
   * @param progress progress monitor to use
   */
  public EclipseProgressMonitorToolsetImpl(IProgressMonitor progress)
  {
    assert progress != null : "Precondition: progress != null";

    _progress = progress;
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
  public void beginTask (String name, int totalWork)
  {
    _progress.beginTask(name, totalWork);
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
    _progress.done();
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
    return _progress.isCanceled();
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
  public void setCanceled (boolean value)
  {
    _progress.setCanceled(value);
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
  public void worked (int work)
  {
    _progress.worked(work);
  }
}