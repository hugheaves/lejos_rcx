package org.lejos.tools.eclipse.plugin.natures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Represents a leJOS project nature
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
 * @author <a href="mailto:jochen.hiller@t-online.de">JOchen Hiller </a>
 *  
 */
public class LejosProjectNature implements IProjectNature
{
   // attributes

   private IProject _project = null;

   // public methods

   /**
    * Add the builder to project descripton.
    * 
    * <p>
    * If yet set, ignore and reuse the just set builder.
    * <p>
    */
   public void configure () throws CoreException
   {
      IProjectDescription description = getProject().getDescription();
      ICommand[] commands = description.getBuildSpec();
      for (int i = 0; i < commands.length; i++)
      {
         if (commands[i].getBuilderName().equals(LejosPlugin.LEJOS_BUILDER))
         {
            return;
         }
      }
      ICommand command = description.newCommand();
      command.setBuilderName(LejosPlugin.LEJOS_BUILDER);
      ICommand[] newCommands = new ICommand[commands.length + 1];
      System.arraycopy(commands, 0, newCommands, 0, commands.length);
      newCommands[newCommands.length - 1] = command;
      description.setBuildSpec(newCommands);
      getProject().setDescription(description, null);
   }

   /**
    * Remove the builder from project description.
    * 
    * <p>
    * If builder is not there, ignore it.
    * </p>
    */
   public void deconfigure () throws CoreException
   {
      IProjectDescription description = getProject().getDescription();
      ICommand[] commands = description.getBuildSpec();
      List newCommandList = new ArrayList(commands.length);
      for (int i = 0; i < commands.length; i++)
      {
         if (!commands[i].getBuilderName().equals(LejosPlugin.LEJOS_BUILDER))
         {
            // preserve all other builders
            newCommandList.add(commands[i]);
         }
      }
      // copy back to array
      ICommand[] newCommands = (ICommand[]) newCommandList
         .toArray(new ICommand[0]);
      description.setBuildSpec(newCommands);
      getProject().setDescription(description, null);
   }

   public IProject getProject ()
   {
      return _project;
   }

   public void setProject (IProject aProject)
   {
      _project = aProject;
   }
}