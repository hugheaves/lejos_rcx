package org.lejos.tools.eclipse.plugin.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lejos.tools.eclipse.plugin.EclipseProgressMonitorToolsetImpl;
import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * This is the Link action for object for the leJOS plugin.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LinkAction
   implements IObjectActionDelegate, IWorkbenchWindowActionDelegate
{

   // attributes

   /** the current selection */
   private ISelection selection = null;

   /** an workbench part */
   private IWorkbenchPart workbenchPart = null;

   /** a workbench window */
   private IWorkbenchWindow workbenchWindow = null;

   // implementation of <IWorkbenchWindowActionDelegate>

   /**
    * Disposes this action delegate. The implementor should unhook any
    * references to itself so that garbage collection can occur.
    */
   public void dispose ()
   {
   // TODO Auto-generated method stub
   }

   /**
    * Stores the current workbench window for later usage.
    * 
    * @param aWindow the window that provides the context for this delegate
    */
   public void init (IWorkbenchWindow aWindow)
   {
      this.workbenchWindow = aWindow;
   }

   // implementation of <IObjectActionDelegate>

   /**
    * Sets the active part for the delegate. The active part is commonly used to
    * get a working context for the action, such as the shell for any dialog
    * which is needed.
    * <p>
    * This method will be called every time the action appears in a popup menu.
    * The targetPart may change with each invocation.
    * </p>
    * 
    * @param action the action proxy that handles presentation portion of the
    *           action
    * @param targetPart the new part target
    */
   public void setActivePart (IAction action, IWorkbenchPart targetPart)
   {
      this.workbenchPart = targetPart;
   }

   // implementation of <IActionDelegate>

   /**
    * Performs the leJOS link action.
    * <p>
    * This method will check, whether there are compilation units selected. If
    * not, a message will be displayed, who indicates "no classes" available. If
    * there are compilation units, the link will be started for all of them.
    * </p>
    * 
    * @param action the action proxy that handles the presentation portion of
    *           the action
    */
   public void run (IAction action)
   {
      // we requires a structured selection
      if (!(this.selection instanceof IStructuredSelection))
      {
         return;
      }
      final IJavaElement[] elems = EclipseUtilities
         .getSelectedJavaElements(this.selection);
      final EclipseToolsetFacade facade = new EclipseToolsetFacade();
      try
      {
         int n = facade.countCU(elems);
         if (n == 0)
         {
            MessageDialog.openInformation(null, null,
               "No classes to link found. "
                  + "Check, whether the selected elements "
                  + "have a \"main(String[])\" method.");
         }
         else
         {
            // create a shell based on the current workbench window
            Shell shell = null;
            if (this.workbenchPart != null)
            {
               shell = this.workbenchPart.getSite().getShell();
            }
            if (this.workbenchWindow != null)
            {
               shell = this.workbenchWindow.getShell();
            }
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
            facade.setProgressMonitor(new EclipseProgressMonitorToolsetImpl(
               dialog.getProgressMonitor()));

            // dont fork as process, allow cancel
            dialog.run(false, true, new IRunnableWithProgress()
            {
               public void run (IProgressMonitor monitor)
                  throws InvocationTargetException, InterruptedException
               {
                  try
                  {
                     facade
                        .linkJavaElement(elems, LejosPlugin.getPreferences());
                  }
                  catch (Exception ex)
                  {
                     ex.printStackTrace();
                  }
               }
            });
         }
      }
      catch (JavaModelException ex)
      {
         MessageDialog.openError(null, null,
            "Internal error: Could not determine the classes to link");
      }
      catch (InvocationTargetException ex)
      {
         MessageDialog.openError(null, null, "Internal error: ("
            + ex.getMessage() + ") occured");
      }
      catch (InterruptedException ex)
      {
         MessageDialog.openError(null, null, "Internal error: ("
            + ex.getMessage() + ") occured");
      }
   }

   /**
    * Will be notified, when a selection changed.
    * 
    * <p>
    * We only accept selections of types
    * <ul>
    * <li>
    * 
    * @{link org.eclipse.jdt.core.IJavaElement#PACKAGE_FRAGMENT_ROOT}</li>
    *        <li>
    * @{link org.eclipse.jdt.core.IJavaElement#PACKAGE_FRAGMENT}</li>
    *        <li>
    * @{link org.eclipse.jdt.core.IJavaElement#COMPILATION_UNIT}</li>
    *        <li>
    * @{link org.eclipse.jdt.core.IJavaElement#TYPE}</li>
    *        </ul>
    *        </p>
    * 
    * @param action the action proxy that handles presentation portion of the
    *           action
    * @param aSelection the current selection, or <code>null</code> if there
    *           is no selection.
    */
   public void selectionChanged (IAction action, ISelection aSelection)
   {
      this.selection = aSelection;
      // we requires a structured selection
      if (!(this.selection instanceof IStructuredSelection))
      {
         return;
      }
      IJavaElement[] elems = EclipseUtilities
         .getSelectedJavaElements(this.selection);
      boolean enabled = false;
      if ((elems == null) || (elems.length == 0))
      {
         enabled = false;
      }
      else
      {
         // check for leJOS project nature
         IJavaElement firstElem = elems[0];
         IProject project = firstElem.getJavaProject().getProject();
         try
         {
            enabled = EclipseUtilities.checkForLeJOSNature(project);
         }
         catch (CoreException exc)
         {
            exc.printStackTrace();
            enabled = false;
         } // catch
         if (enabled)
         {
            // now check for valid types
            for (int i = 0; i < elems.length; i++)
            {
               IJavaElement elem = elems[i];
               enabled = true;
               // all other types are INVALID
               if (!(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
                  && !(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
                  && !(elem.getElementType() == IJavaElement.COMPILATION_UNIT)
                  && !(elem.getElementType() == IJavaElement.TYPE))
               {
                  enabled = false;
                  enabled = false;
               }
            }
         }
      }
      action.setEnabled(enabled);
   }

   // private methods

   /**
    * Get the IJavaElements from the current selection.
    * 
    * <p>
    * Only structured selections will be used.
    * </p>
    * 
    * @param aSelection the current selection
    * @return an array with all java elements selected. Is always not null. If
    *         no valid structured selections are available, an array with size =
    *         0 will be returned.
    */
   /*
    * private IJavaElement[] getSelectedJavaElements(ISelection aSelection) {
    * IStructuredSelection structured = (IStructuredSelection) aSelection;
    * Object[] oElems = structured.toArray(); // copy into type safe array
    * IJavaElement[] elems = new IJavaElement[oElems.length]; for (int i = 0; i <
    * oElems.length; i++) { elems[i] = (IJavaElement) oElems[i]; } return elems; }
    */
}