package org.lejos.tools.eclipse.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.wizard.Wizard;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Wizard for creating a leJOS RCX project.
 * <p>
 * The wizard will update the CLASSPATH after creating the project. The leJOS
 * standard libraries <code>classes.jar</code> and <code>rcxcomm.jar</code>
 * will be added with a link to the corresponding sources. The standard
 * JRE_CONTAINER will be removed, to avoid duplicate include of Java core
 * classes.
 * </p>
 * <p>
 * Additionally, the internal compiler will be set to compile against target
 * 1.1. There will a leJOS nature added to the project.
 * </p>
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosRCXProjectWizard extends JavaProjectWizard
{
   // public methods

   /**
    * Will be called when setup of project has been finished.
    * 
    * @see Wizard#performFinish
    */
   public boolean performFinish ()
   {
      boolean rc = super.performFinish();
      // get project
      JavaCapabilityConfigurationPage page = (JavaCapabilityConfigurationPage) getPage("JavaCapabilityConfigurationPage");
      if (page == null)
      {
         return rc;
      }
      // set leJOS project nature
      IJavaProject project = page.getJavaProject();
      EclipseUtilities.addLeJOSNature(project.getProject());
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
   private void updateClasspath (IJavaProject aProject)
   {
      try
      {
         // get existing classpath
         IClasspathEntry[] existingClasspath = aProject.getRawClasspath();
         // get classpath entries from preferences
         IClasspathEntry[] theCPEntries = LejosPlugin.getPreferences()
            .getRCXClasspathEntries();

         // create new classpath with additional leJOS libraries last
         List newClasspath = new ArrayList(existingClasspath.length
            + theCPEntries.length);
         for (int i = 0; i < existingClasspath.length; i++)
         {
            // filter out JRE_CONTAINER
            IClasspathEntry cpEntry = existingClasspath[i];
            if ((cpEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
               && ((cpEntry.getPath().lastSegment()).indexOf("JRE_CONTAINER") >= 0))
            {
               // skip JRE_CONTAINER, if container ends with JRE_CONTAINER
            }
            else
            {
               // e.g. source container
               newClasspath.add(existingClasspath[i]);
            }
         }
         // add the other cp entries
         for (int i = 0; i < theCPEntries.length; i++)
         {
            newClasspath.add(theCPEntries[i]);
         }

         IClasspathEntry[] cpEntries = (IClasspathEntry[]) newClasspath
            .toArray(new IClasspathEntry[0]);
         // set new classpath to project
         aProject.setRawClasspath(cpEntries, null);
      }
      catch (Exception e)
      {
         LejosPlugin.debug(e);
         e.printStackTrace();
      }
   }
}