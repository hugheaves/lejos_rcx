package org.lejos.tools.eclipse.plugin.builders;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.EclipseProgressMonitorToolsetImpl;
import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * This is the leJOS builder, which is responsible for transparent linking of
 * all leJOS RCX main programs.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">JOchen Hiller </a>
 *  
 */
public class LejosBuilder extends IncrementalProjectBuilder
{
   // overriden methods of incremental project builder

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
    *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
    */
   protected IProject[] build (int kind, Map args, IProgressMonitor monitor)
      throws CoreException
   {
      LejosPlugin.debug("Builder has been running");

      linkAll(monitor);
      // WAS HERE
      // Object o = getDelta(getProject());
      // TODO Auto-generated method stub
      return null;
   }

   // private methods

private void linkAll (IProgressMonitor monitor)
   {
      IProject p = getProject();
      IJavaProject jp = JavaCore.create(p);
      try
      {
         IJavaElement[] elems = jp.getChildren();
         for (int i = 0; i < elems.length; i++)
         {
            IJavaElement elem = elems[i];
            if (elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
            {
               // System.out.println(elem);
               ICompilationUnit[] cus = EclipseUtilities
                  .collectLinkClasses(elem);
               monitor.beginTask("Linking leJOS", cus.length);
               for (int j = 0; j < cus.length; j++)
               {
                  ICompilationUnit cu = cus[j];
                  if (EclipseUtilities.hasMain(cu))
                  {
                     EclipseToolsetFacade facade = new EclipseToolsetFacade();
                     facade
                        .setProgressMonitor(new EclipseProgressMonitorToolsetImpl(
                           monitor));
                     monitor.subTask("Linking " + String.valueOf(cu.getPath()));
                     monitor.worked(j);
                     facade.linkJavaElement(cu, LejosPlugin.getPreferences());
                  }
               }
               monitor.done();
            }
         }
      }
      catch (JavaModelException ex)
      {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }
      catch (ToolsetException ex)
      {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }

   }}