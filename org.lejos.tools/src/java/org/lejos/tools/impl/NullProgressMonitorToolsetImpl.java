package org.lejos.tools.impl;

import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * This is a <b>null </b> implementation of a standard
 * <code>IProgressMonitorToolset</code>.
 * 
 * <p>
 * It will do <b>nothing </b>.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class NullProgressMonitorToolsetImpl implements IProgressMonitorToolset
{

   // implementation of IProgressMonitorToolset

   /**
    * Notifies that the main task is beginning. This must only be called once on
    * a given progress monitor instance.
    * 
    * @param name the name (or description) of the main task
    * @param aTotalWork the total number of work units into which the main task
    *           is been subdivided. If the value is <code>UNKNOWN</code> the
    *           implemenation is free to indicate progress in a way which
    *           doesn't require the total number of work units in advance.
    */
   public void beginTask (String name, int aTotalWork)
   {}

   /**
    * Notifies that the work is done; that is, either the main task is completed
    * or the user canceled it. This method may be called more than once
    * (implementations should be prepared to handle this case).
    */
   public void done ()
   {}

   /**
    * Returns whether cancelation of current operation has been requested.
    * Long-running operations should poll to see if cancelation has been
    * requested.
    * 
    * @return <code>true</code> if cancellation has been requested, and
    *         <code>false</code> otherwise
    * @see #setCanceled
    */
   public boolean isCanceled ()
   {
      return false;
   }

   /**
    * Sets the cancel state to the given value.
    * 
    * @param value <code>true</code> indicates that cancelation has been
    *           requested (but not necessarily acknowledged); <code>false</code>
    *           clears this flag
    * 
    * @see #isCanceled
    */
   public void setCanceled (boolean value)
   {}

   /**
    * Notifies that a given number of work unit of the main task has been
    * completed. Note that this amount represents an installment, as opposed to
    * a cumulative amount of work done to date.
    * 
    * @param work the number of work units just completed
    */
   public void worked (int work)
   {}
}