package org.lejos.tools.eclipse.plugin.launch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;

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

      String mainClass = verifyMainTypeName(configuration);
      if (mainClass == null)
      {
        throw new ToolsetException("main class not found");
      }

      IPath outputPathRel = javaProject.getOutputLocation();
      IPath outputPath = workspace.getFile(outputPathRel).getLocation();
      File outputDir = new File(outputPath.toOSString());

      IType type = javaProject.findType(mainClass);
      String typeName = type.getFullyQualifiedName();
      String typePath = typeName.replace('.', IPath.SEPARATOR);
      IPath binPathRel = outputPathRel.append(typePath).addFileExtension("bin");
      IFile binFile = workspace.getFile(binPathRel);
      IPath binPath = binFile.getLocation();

      // create bin file
      binFile.delete(true, false, monitor);
      binFile.create(null, true, monitor);

      // link
      EclipseToolsetFacade facade = new EclipseToolsetFacade();
      // TODO change facade to use IProgressMonitor
      // facade.setProgressMonitor(monitor);
      OutputStream stream = new FileOutputStream(binPath.toOSString());
      facade.link(outputDir.getAbsolutePath(), mainClass, stream);
      // TODO download
      stream.close();
    }
    catch (IOException e)
    {
      abort("Linking failed", e,
          IJavaLaunchConfigurationConstants.ERR_VM_LAUNCH_ERROR);
    }
    catch (ToolsetException e)
    {
      abort("Linking failed", e,
          IJavaLaunchConfigurationConstants.ERR_VM_LAUNCH_ERROR);
    }
  }
}