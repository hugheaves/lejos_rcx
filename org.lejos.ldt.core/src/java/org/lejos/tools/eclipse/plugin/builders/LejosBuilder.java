package org.lejos.tools.eclipse.plugin.builders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
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

      if (hasBuildErrors())
      {
         return null;
      }

      clearLinkMarkers(getProject());
      linkAll(monitor);

      // TODO jhi make build later a delta based builder,
      // so only link the modified main classes
      // Object o = getDelta(getProject());
      return null;
   }

   // private methods

   /**
    * Link all compilation units with a main method.
    * 
    * @param monitor the progress monitor to use
    */
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
                     String cuName = String.valueOf(cu.getPath());
                     // remove leading slash
                     cuName = cuName.substring(1);
                     monitor.subTask("Linking " + cuName);
                     monitor.worked(j);
                     try
                     {
                        deleteMarkers(getProject(), cu);
                        facade
                           .linkJavaElement(cu, LejosPlugin.getPreferences());
                     }
                     catch (ToolsetException ex)
                     {
                        createMarker(getProject(), cu, ex);
                     }
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
   }

   /**
    * Checks for any severy build errors
    * 
    * @return true, if there are severy build erros
    * @throws CoreException will be raised if markers could not be read
    */
   private boolean hasBuildErrors () throws CoreException
   {
      IMarker[] markers = getProject().findMarkers(
         IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
         IResource.DEPTH_INFINITE);
      for (int i = 0; i < markers.length; i++)
      {
         IMarker marker = markers[i];
         if (marker.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR)
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Delete all linker markers (before linking).
    */
   private void deleteMarkers (IProject project, ICompilationUnit cu)
   {
      try
      {
         IResource resource = cu.getUnderlyingResource();
         resource.deleteMarkers(LejosPlugin.LEJOS_MARKER_LINKER, false, IResource.DEPTH_INFINITE);
      }
      catch (CoreException ex)
      {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }
   }

   /**
    * Create a linker marker for a given exception (linking failed).
    */
   private void createMarker (IProject project, ICompilationUnit cu,
      Exception anException)
   {
      try
      {
         IResource resource = cu.getUnderlyingResource();
         IMarker newMarker = resource
            .createMarker(LejosPlugin.LEJOS_MARKER_LINKER);
         Map map = new HashMap();
         map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
         String message = "Link failed due to exception: ";
         message = message + anException.getMessage();
         map.put(IMarker.MESSAGE, message);
         newMarker.setAttributes(map);
      }
      catch (CoreException ex)
      {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }
   }

   private void clearLinkMarkers (IProject project) throws CoreException
   {
      project.getProject().deleteMarkers(LejosPlugin.LEJOS_MARKER_LINKER,
         false, IResource.DEPTH_INFINITE);
   }
}