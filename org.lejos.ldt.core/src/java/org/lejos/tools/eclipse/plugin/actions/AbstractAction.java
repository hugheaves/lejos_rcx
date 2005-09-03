package org.lejos.tools.eclipse.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Abstract base class for actions.
 */
public abstract class AbstractAction
   implements IObjectActionDelegate, IWorkbenchWindowActionDelegate
{
   /**
    * the current selection
    */
   private ISelection fSelection = null;

   /**
    * an workbench part
    */
   private IWorkbenchPart fWorkbenchPart = null;

   /**
    * a workbench window
    */
   private IWorkbenchWindow fWorkbenchWindow = null;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
    */
   public void dispose ()
   {}

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
    */
   public void init (IWorkbenchWindow window)
   {
      fWorkbenchWindow = window;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
    *      org.eclipse.ui.IWorkbenchPart)
    */
   public void setActivePart (IAction action, IWorkbenchPart targetPart)
   {
      fWorkbenchPart = targetPart;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
    *      org.eclipse.jface.viewers.ISelection)
    */
   public void selectionChanged (IAction action, ISelection selection)
   {
      fSelection = selection;
   }

   //
   // protected interface
   //

   /**
    * Get current shell.
    */
   protected Shell getShell ()
   {
      // get shell
      Shell result = null;
      if (fWorkbenchPart != null)
      {
         result = fWorkbenchPart.getSite().getShell();
      }
      else if (fWorkbenchWindow != null)
      {
         result = fWorkbenchWindow.getShell();
      }

      return result;
   }
   
   /**
    * Get current selection.
    */
   protected ISelection getSelection ()
   {
      return fSelection;
   }
}