package org.lejos.tools.eclipse.plugin.actions;

import java.util.Hashtable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * represents a compile action for object for the leJOS plugin.
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
 */
public class CompileAction
   implements IObjectActionDelegate, IWorkbenchWindowActionDelegate
{

   // fields

   /** the current selection */
   private ISelection fSelection = null;

   /** an workbench part */
   private IWorkbenchPart fWorkbenchPart = null;

   /** a workbench window */
   private IWorkbenchWindow fWorkbenchWindow = null;

   // implementations of <IWorkbenchWindowActionDelegate>,
   // <IObjectActionDelegate> and <IActionDelegate>

   /**
    * Disposes this action delegate. The implementor should unhook any
    * references to itself so that garbage collection can occur.
    */
   public void dispose ()
   {
   // nothing to dispose of here
   }

   /**
    * Stores the current workbench window for later usage.
    * 
    * @param aWindow the window that provides the context for this delegate
    */
   public void init (IWorkbenchWindow aWindow)
   {
      this.fWorkbenchWindow = aWindow;
   }

   /**
    * Sets the active part for the delegate
    * 
    * @param action the action proxy that handles presentation portion of the
    *           action
    * @param targetPart the new part target
    */
   public void setActivePart (IAction action, IWorkbenchPart targetPart)
   {
      this.fWorkbenchPart = targetPart;
   }

   /**
    * runs the compile action
    * <p>
    * This method will check, whether there are compilation units selected. If
    * not, a message will be displayed, who indicates "nosource files"
    * available. If there are compilation units, the compile will be started for
    * each of them.
    * </p>
    * 
    * @param action the action proxy that handles the presentation portion of
    *           the action
    */
   public void run (IAction action)
   {
      // is there a structured selection?
      if (!(this.fSelection instanceof IStructuredSelection))
      {
         return;
      }
      // get selection
      final IJavaElement[] elems = EclipseUtilities
         .getSelectedJavaElements(this.fSelection);
      // something selected at all?
      if ((elems == null) || (elems.length == 0))
      {
         System.out.println("nothing selected");
         return;
      }
      // create a shell based on the current workbench window
      Shell shell = null;
      if (this.fWorkbenchPart != null)
      {
         shell = this.fWorkbenchPart.getSite().getShell();
      }
      if (this.fWorkbenchWindow != null)
      {
         shell = this.fWorkbenchWindow.getShell();
      }
      // get project (all elements are in one project)
      IJavaElement elem = elems[0];
      IProject project = elem.getJavaProject().getProject();
      // build
      try
      {
         IProgressMonitor myProgressMonitor = new ProgressMonitorPart(shell,
            null);
         project.build(IncrementalProjectBuilder.FULL_BUILD, myProgressMonitor);
         // set "target 1.1" option
         Hashtable options = JavaCore.getOptions();
         options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
            JavaCore.VERSION_1_1);
         JavaCore.setOptions(options);
      }
      catch (CoreException exc)
      {
         exc.printStackTrace();
         // show error
         MessageDialog
            .openInformation(null, null, "error: " + exc.getMessage());
      }
   }

   /**
    * selection changed
    * 
    * <p>
    * accepted selections: of types
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
    *        only.
    *        </p>
    * @param action the action proxy that handles presentation portion of the
    *           action
    * @param aSelection the current selection, or <code>null</code> if there
    *           is no selection.
    */
   public void selectionChanged (IAction action, ISelection aSelection)
   {
      this.fSelection = aSelection;
      // we require a structured selection
      if (!(this.fSelection instanceof IStructuredSelection))
      {
         return;
      }
      IJavaElement[] elems = EclipseUtilities
         .getSelectedJavaElements(this.fSelection);
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
            enabled = LejosPlugin.checkForLeJOSNature(project);
         }
         catch (CoreException exc)
         {
            exc.printStackTrace();
            enabled = false;
         }
         if (enabled)
         {
            // now check for valid types
            for (int i = 0; i < elems.length; i++)
            {
               IJavaElement elem = elems[i];
               // all other types are INVALID
               if (!(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
                  && !(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
                  && !(elem.getElementType() == IJavaElement.COMPILATION_UNIT)
                  && !(elem.getElementType() == IJavaElement.TYPE))
               {
                  enabled = false;
               }
            }
         }
      }
      action.setEnabled(enabled);
   }
}