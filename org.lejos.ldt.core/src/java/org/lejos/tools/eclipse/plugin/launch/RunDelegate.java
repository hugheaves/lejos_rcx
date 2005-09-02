package org.lejos.tools.eclipse.plugin.launch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.lejos.tools.api.PlatformRegistry;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.EclipseProgressMonitorToolsetImpl;
import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Run on RCX launch delegate.
 */
public class RunDelegate extends AbstractJavaLaunchConfigurationDelegate
{
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
    *      java.lang.String, org.eclipse.debug.core.ILaunch,
    *      org.eclipse.core.runtime.IProgressMonitor)
    */
   public void launch (ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException
   {
      IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();

      try
      {
         IJavaProject javaProject = verifyJavaProject(configuration);
         if (javaProject == null)
         {
            throw new ToolsetException("java project not found");
         }

         final String classpath = EclipseUtilities.getClasspath(javaProject);
         
         final String mainClass = verifyMainTypeName(configuration);
         if (mainClass == null)
         {
            throw new ToolsetException("main class not found");
         }

         IType type = javaProject.findType(mainClass);
         if (type == null)
         {
            throw new ToolsetException("main class not found");
         }

         final IPath outputPathRel = javaProject.getOutputLocation();

         String typeName = type.getFullyQualifiedName();
         String typePath = typeName.replace('.', IPath.SEPARATOR);
         IPath binPathRel = outputPathRel.append(typePath).addFileExtension(
            "bin");
         IFile binFile = workspace.getFile(binPathRel);
         final IPath binPath = binFile.getLocation();

         // create bin file
         binFile.delete(true, false, monitor);
         binFile.create(null, true, monitor);

         Job job = new Job("leJOS link")
         {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            protected IStatus run (IProgressMonitor monitor)
            {
               EclipseToolsetFacade facade = new EclipseToolsetFacade();

               try
               {
                  // link
                  setName("leJOS link");
                  monitor.beginTask("leJOS link", 1000);
                  OutputStream output = new FileOutputStream(binPath
                     .toOSString());
                  try
                  {
                     facade
                        .setProgressMonitor(new EclipseProgressMonitorToolsetImpl(
                           monitor));
                     facade.link(PlatformRegistry.RCX, classpath, mainClass, output);
                  }
                  finally
                  {
                     output.close();
                     monitor.done();
                  }
               }
               catch (IOException e)
               {
                  return LejosPlugin.errorStatus(e);
               }
               catch (ToolsetException e)
               {
                  return LejosPlugin.errorStatus(e);
               }

               try
               {
                  // download
                  setName("leJOS program download");
                  monitor.beginTask("leJOS program download", 1000);
                  InputStream input = new FileInputStream(binPath.toOSString());
                  facade
                     .setProgressMonitor(new EclipseProgressMonitorToolsetImpl(
                        monitor));
                  facade.downloadExecutable(input);
                  input.close();
                  monitor.done();
               }
               catch (IOException e)
               {
                  return LejosPlugin.errorStatus(e);
               }
               catch (ToolsetException e)
               {
                  if (monitor.isCanceled())
                  {
                     return Status.CANCEL_STATUS;
                  }

                  return LejosPlugin.errorStatus(e);
               }

               return Status.OK_STATUS;
            }
         };
         job.setUser(true);
         job.schedule();
      }
      catch (ToolsetException e)
      {
         abort("Run failed: " + e.getMessage(), e,
            IJavaLaunchConfigurationConstants.ERR_VM_LAUNCH_ERROR);
      }
   }
}