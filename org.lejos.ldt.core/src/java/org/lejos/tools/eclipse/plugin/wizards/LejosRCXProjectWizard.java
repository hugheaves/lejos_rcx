package org.lejos.tools.eclipse.plugin.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Represents a project wizard for leJOS RCX projects.
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
 *         based on implementation parts of Christophe Ponsard
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosRCXProjectWizard extends JavaProjectWizard
{
  // public methods
  
  /**
   * @see Wizard#performFinish
   */
  public boolean performFinish()
  {
    boolean rc = super.performFinish();
    // get project
    JavaCapabilityConfigurationPage page = (JavaCapabilityConfigurationPage) getPage("JavaCapabilityConfigurationPage");
    if (page == null) {
      return rc;
    }
    // set leJOS project nature
    IJavaProject project = page.getJavaProject();
    EclipseUtilities.setLeJOSNature(project.getProject());
    // update classpath
    updateClasspath(project);
    // set "target 1.1" option
    project.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
        JavaCore.VERSION_1_1);
    return rc;
  }

  // private methods

  /**
   * update the project's classpath with additional leJOS libraries.
   * 
   * @param aProject a java project
   */
  private void updateClasspath(IJavaProject aProject)
  {
    try
    {
      // get existing classpath
      IClasspathEntry[] existingClasspath = aProject.getRawClasspath();
      // get lejos libraries
      String[] lejosLibs = LejosPlugin.getPreferences()
          .getDefaultClasspathEntries();
      if ((lejosLibs == null) || (lejosLibs.length == 0))
        return;
      // create new classpath with additional leJOS libraries last
      IClasspathEntry[] newClasspath = new IClasspathEntry[existingClasspath.length
          + lejosLibs.length];
      int counter = 0;
      for (int i = 0; i < existingClasspath.length; i++)
      {
        newClasspath[counter] = existingClasspath[i];
        counter = counter + 1;
      }
      for (int i = 0; i < lejosLibs.length; i++)
      {
        IPath lejosLibPath = new Path(lejosLibs[i]);
        IPath absoluteLibPath = EclipseUtilities.findFileInPlugin("org.lejos",
            lejosLibPath.toString());
        IClasspathEntry classpathForLibrary = JavaCore.newLibraryEntry(
            absoluteLibPath, null, null);
        newClasspath[counter] = classpathForLibrary;
        counter = counter + 1;
      }
      // set new classpath to project
      aProject.setRawClasspath(newClasspath, null);
    } catch (Exception e)
    {
      LejosPlugin.debug(e);
      e.printStackTrace();
    }
  }
}