package org.lejos.tools.impl;

import js.common.ToolProgressMonitor;

import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * Implementation of ToolProgressMonitor for eclipse progress monitoring.
 */
public class ToolProgressMonitorImpl implements ToolProgressMonitor
{
   private IProgressMonitorToolset _monitor;
   private int _progress;

   /**
    * Constructor.
    */
   public ToolProgressMonitorImpl (IProgressMonitorToolset monitor)
   {
      assert monitor != null: "Precondition: monitor != null";

      _monitor = monitor;
      _progress = 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see js.tools.ToolProgressMonitor#operation(java.lang.String)
    */
   public void operation (String operation)
   {
      _monitor.beginTask(operation, 1000);
      _progress = 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see js.tools.ToolProgressMonitor#log(java.lang.String)
    */
   public void log (String message)
   {
   // ignore log messages
   }

   /*
    * (non-Javadoc)
    * 
    * @see js.tools.ToolProgressMonitor#progress(int)
    */
   public void progress (int progress)
   {
      if (progress >= 1000)
      {
         _monitor.done();
      }
      else
      {
         _monitor.worked(progress - _progress);
      }
      _progress = progress;
   }

   /*
    * (non-Javadoc)
    * 
    * @see js.common.ToolProgressMonitor#isCanceled()
    */
   public boolean isCanceled ()
   {
      return _monitor.isCanceled();
   }
}