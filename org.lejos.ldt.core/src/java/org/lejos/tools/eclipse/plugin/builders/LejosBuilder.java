package org.lejos.tools.eclipse.plugin.builders;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
      // WAS HERE 
      // Object o = getDelta(getProject());
      // TODO Auto-generated method stub
      return null;
   }

}