package org.lejos.tools.impl;

import org.lejos.tools.api.IProgressMonitorToolset;

import js.tools.ToolProgressListener;

/**
 * Implementation of ToolProgressListener for eclipse progress monitoring.
 */
public class ToolProgressListenerImpl implements ToolProgressListener
{
  private IProgressMonitorToolset _monitor;
  private int _progress;
  
  /**
   * Constructor.
   */
  public ToolProgressListenerImpl(IProgressMonitorToolset monitor)
  {
    assert monitor != null : "Precondition: monitor != null";
    
    _monitor = monitor;
  }

  /*
   * (non-Javadoc)
   * @see js.tools.ToolProgressListener#operation(java.lang.String)
   */
  public void operation (String operation)
  {
    _monitor.beginTask(operation, 100);
    _progress = 0;
  }

  /*
   * (non-Javadoc)
   * @see js.tools.ToolProgressListener#log(java.lang.String)
   */
  public void log (String message)
  {
    // ignore log messages
  }

  /*
   * (non-Javadoc)
   * @see js.tools.ToolProgressListener#progress(int)
   */
  public void progress (int progress)
  {
    if (progress >= 100)
    {
      _monitor.done();
    }
    else
    {
      _monitor.worked(progress - _progress);
    }
    _progress = progress;
  }
}