package org.lejos.tools.eclipse.plugin.wizards;

import java.util.Hashtable;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.NewProjectCreationWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

////////////////////////////////////////////////////////
/**
 *
 * represents a project wizard for Lejos projects
 *
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz</a>
 * based on implementation parts of Christophe Ponsard
 *   
 */
public class LejosProjectWizard  extends NewProjectCreationWizard {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // overrides of NewProjectCreationWizard
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * @see Wizard#performFinish
    */     
    public boolean performFinish() {
            boolean rc = super.performFinish();
            // get project
            JavaCapabilityConfigurationPage page = 
                (JavaCapabilityConfigurationPage)getPage("JavaCapabilityConfigurationPage");
            if (page==null) 
                return rc;
            IJavaProject project = page.getJavaProject();     
            // set leJOS project nature
            EclipseUtilities.setLeJOSNature(project.getProject());
            // update classpath
            updateClasspath(project);
            // set "target 1.1" option
            Hashtable options = JavaCore.getOptions();
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
                    JavaCore.VERSION_1_1);
            return rc;
    } //performFinish()
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // private methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // update the project's classpath with additional leJOS libraries
    //@param IJavaProject the project
    private void updateClasspath(IJavaProject aProject) {
    	try {
            // get existing classpath
            IClasspathEntry[] existingClasspath = aProject.getRawClasspath();
            // get lejos libraries
            String[] lejosLibs = LejosPlugin.getPreferences(). getDefaultClasspathEntries();
            if((lejosLibs==null)||(lejosLibs.length==0))
                return;
            // create new classpath with additional eJOS libraries first
            IClasspathEntry[] newClasspath = new IClasspathEntry[existingClasspath.length
                +   lejosLibs.length];
            int counter = 0;
            for (int j = 0; j < lejosLibs.length; j++) {
                IPath lejosLibPath = new Path(lejosLibs[j]);
                Path absoluteLibPath = EclipseUtilities.findFileInPlugin("org.lejos", lejosLibPath.toString());
                IClasspathEntry classpathForLibrary = 
                    JavaCore.newLibraryEntry(absoluteLibPath,null,null);
                newClasspath[counter++] = classpathForLibrary;
            } //for                
            for (int i = 0; i < existingClasspath.length; i++) {
                newClasspath[counter++] = existingClasspath[i];
            } // for
            // set new classpath to project
            aProject.setRawClasspath(newClasspath,null);
    	} catch(Exception e) {
            LejosPlugin.debug(e);   
    		e.printStackTrace();
    	} //catch 
 
    } // updateClasspath()

}
