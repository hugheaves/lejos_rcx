package org.lejos.tools.eclipse.plugin.view;

import js.tinyvm.ClassRecord;
import js.tinyvm.ConstantRecord;

import org.eclipse.jface.viewers.LabelProvider;
import org.lejos.tools.eclipse.plugin.view.model.BinaryClassElement;
import org.lejos.tools.eclipse.plugin.view.model.BinaryConstantElement;
import org.lejos.tools.eclipse.plugin.view.model.ClassElement;
import org.lejos.tools.eclipse.plugin.view.model.ConstantElement;

/**
 * Label provider for linker view.
 */
public class LinkerLabelProvider extends LabelProvider
{
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
    */
   public String getText (Object element)
   {
      if (element instanceof BinaryClassElement)
      {
         return "Classes";
      }
      else if (element instanceof BinaryConstantElement)
      {
         return "Constants";
      }
      else if (element instanceof ClassElement)
      {
         ClassElement classElement = (ClassElement) element;
         ClassRecord classRecord = classElement.getClassRecord();
         return classRecord.getName();
      }
      else if (element instanceof ConstantElement)
      {
         ConstantElement constantElement = (ConstantElement) element;
         ConstantRecord constantRecord = constantElement.getConstantRecord();
         Object value = constantRecord.constantValue().value();
         String result = value instanceof String? "\"" + value + "\"" : value.toString();
         result += " (" + value.getClass().getName() + ")";
         return result;
      }

      return "<unknown>";
   }
}