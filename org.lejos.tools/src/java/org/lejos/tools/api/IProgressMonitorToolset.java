package org.lejos.tools.api;

/**
 * This is the interface to communicate with external processes using the
 * toolset.
 * 
 * <p>
 * The <code>IProgressMonitorToolset</code> can be used, to show any progress
 * during processing, but can also be used to stop any further processing, using
 * the <code>setCanceled()</code> method.
 * </p>
 * 
 * <p>
 * This interface is defined like the class <code>IProgressMonitor</code>
 * within Eclipse. As this is an Eclipse independent package, we avoid direct
 * references to this class.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public interface IProgressMonitorToolset
{

   /**
    * Notifies that the main task is beginning. This must only be called once on
    * a given progress monitor instance.
    * 
    * @param name the name (or description) of the main task
    * @param totalWork the total number of work units into which the main task
    *           is been subdivided. If the value is <code>UNKNOWN</code> the
    *           implemenation is free to indicate progress in a way which
    *           doesn't require the total number of work units in advance.
    */
   void beginTask (String name, int totalWork);

   /**
    * Notifies that the work is done; that is, either the main task is completed
    * or the user canceled it. This method may be called more than once
    * (implementations should be prepared to handle this case).
    */
   void done ();

   /**
    * Returns whether cancelation of current operation has been requested.
    * Long-running operations should poll to see if cancelation has been
    * requested.
    * 
    * @return <code>true</code> if cancellation has been requested, and
    *         <code>false</code> otherwise
    * @see #setCanceled
    */
   boolean isCanceled ();

   /**
    * Sets the cancel state to the given value.
    * 
    * @param value <code>true</code> indicates that cancelation has been
    *           requested (but not necessarily acknowledged); <code>false</code>
    *           clears this flag
    * 
    * @see #isCanceled
    */
   void setCanceled (boolean value);

   /**
    * Notifies that a given number of work unit of the main task has been
    * completed. Note that this amount represents an installment, as opposed to
    * a cumulative amount of work done to date.
    * 
    * @param work the number of work units just completed
    */
   void worked (int work);
}