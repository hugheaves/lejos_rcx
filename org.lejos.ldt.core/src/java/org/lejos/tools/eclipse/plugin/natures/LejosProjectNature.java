package org.lejos.tools.eclipse.plugin.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Represents a leJOS project nature
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
 * @author <a href="mailto:jochen.hiller@t-online.de">JOchen Hiller </a>
 *  
 */
public class LejosProjectNature implements IProjectNature {

    private IProject fProject = null;

    public void configure() throws CoreException {
        // nothing to do here yet
    }

    public void deconfigure() throws CoreException {
        // nothing to do here yet
    }

    public IProject getProject() {
        return fProject;
    }

    public void setProject(IProject value) {
        fProject = value;
    }
}