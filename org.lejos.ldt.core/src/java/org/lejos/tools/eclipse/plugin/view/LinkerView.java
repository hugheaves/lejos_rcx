package org.lejos.tools.eclipse.plugin.view;

import js.tinyvm.Binary;
import js.tinyvm.TinyVMException;

import org.apache.bcel.util.ClassPath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.lejos.tools.eclipse.plugin.view.model.BinaryElement;

/**
 * Shows results of leJOS linking for entry classes.
 */
public class LinkerView extends ViewPart
{
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   public void createPartControl (Composite parent)
   {
      // TODO Auto-generated method stub
      System.out.println("create view part control");
      TreeViewer tree = new TreeViewer(parent);
      tree.setContentProvider(new LinkerContentProvider());
      tree.setLabelProvider(new LinkerLabelProvider());
      try
      {
         Binary binary = Binary
            .createFromClosureOf(
               new String[]
               {
                  "roboter.SoccerRobot"
               },
               new ClassPath(
                  "c:/programme/eclipse/workspace/Robosoc/lib/classes.jar;c:/programme/eclipse/workspace/Robosoc/classes"),
               false);
         tree.setInput(new BinaryElement(binary));
      }
      catch (TinyVMException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#setFocus()
    */
   public void setFocus ()
   {
      // TODO Auto-generated method stub
      System.out.println("linker view has got focus");
   }
}