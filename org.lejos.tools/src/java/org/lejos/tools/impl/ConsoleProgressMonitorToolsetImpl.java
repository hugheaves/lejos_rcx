package org.lejos.tools.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * This is the default implementation of a standard
 * <code>IProgressMonitorToolset</code>.
 * 
 * <p>
 * It will print any progress messages onto standard output console.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class ConsoleProgressMonitorToolsetImpl implements
        IProgressMonitorToolset {

    // attributes

    /** flag, whether the progress monitor has been canceled */
    private boolean isCanceled = false;

    /**
     * flag, which indicates whether this progress monitor has been used.
     * <p>
     * If it has been used, a <code>beginTask()</code> is not valid anymore.
     * </p>
     */
    private boolean used = false;

    /** the default for the total work to be done */
    private int totalWork = 1;

    // constructors

    /**
     * Default constructor
     */
    public ConsoleProgressMonitorToolsetImpl() {
        // nothing to do yet
    }

    // implementation of IProgressMonitorToolset

    /**
     * Notifies that the main task is beginning. This must only be called once
     * on a given progress monitor instance.
     * 
     * @param name
     *            the name (or description) of the main task
     * @param aTotalWork
     *            the total number of work units into which the main task is
     *            been subdivided. If the value is <code>UNKNOWN</code> the
     *            implemenation is free to indicate progress in a way which
     *            doesn't require the total number of work units in advance.
     */
    public void beginTask(String name, int aTotalWork) {
        if (this.used) {
            throw new IllegalStateException(
                    "A progress monitor can NOT be used multiple times");
        }
        this.totalWork = aTotalWork;
        print(String.valueOf(name) + "\n");
        print("  0%");
    }

    /**
     * Notifies that the work is done; that is, either the main task is
     * completed or the user canceled it. This method may be called more than
     * once (implementations should be prepared to handle this case).
     */
    public void done() {
        print("\r  100%\n");
        this.used = true;
    }

    /**
     * Returns whether cancelation of current operation has been requested.
     * Long-running operations should poll to see if cancelation has been
     * requested.
     * 
     * @return <code>true</code> if cancellation has been requested, and
     *         <code>false</code> otherwise
     * @see #setCanceled
     */
    public boolean isCanceled() {
        return this.isCanceled;
    }

    /**
     * Sets the cancel state to the given value.
     * 
     * @param value
     *            <code>true</code> indicates that cancelation has been
     *            requested (but not necessarily acknowledged);
     *            <code>false</code> clears this flag
     * 
     * @see #isCanceled
     */
    public void setCanceled(boolean value) {
        this.isCanceled = value;
    }

    /**
     * Notifies that a given number of work unit of the main task has been
     * completed. Note that this amount represents an installment, as opposed to
     * a cumulative amount of work done to date.
     * 
     * @param work
     *            the number of work units just completed
     */
    public void worked(int work) {
        double d = (double) work / (double) this.totalWork;
        NumberFormat format = DecimalFormat.getPercentInstance();
        String percentage = format.format(d);
        print("\r  " + percentage);
    }

    // protected methods

    /**
     * Prints out a message to <code>System.err</code>.
     * 
     * @param aMsg
     *            the message
     */
    protected void print(String aMsg) {
        System.err.print(aMsg);
    }
}