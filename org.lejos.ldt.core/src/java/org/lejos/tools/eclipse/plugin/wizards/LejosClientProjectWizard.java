package org.lejos.tools.eclipse.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.wizard.Wizard;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Represents a project wizard for leJOS client projects.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosClientProjectWizard extends JavaProjectWizard
{
   // public methods

   /**
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
      // update classpath
      IJavaProject project = page.getJavaProject();
      updateClasspath(project);
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
            .getClientClasspathEntries();

         // create new classpath with additional leJOS libraries last
         List newClasspath = new ArrayList(existingClasspath.length
            + theCPEntries.length);
         for (int i = 0; i < existingClasspath.length; i++)
         {
            newClasspath.add(existingClasspath[i]);
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