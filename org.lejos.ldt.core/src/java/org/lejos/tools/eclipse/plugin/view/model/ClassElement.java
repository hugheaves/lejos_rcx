package org.lejos.tools.eclipse.plugin.view.model;

import js.tinyvm.ClassRecord;

/**
 * Class.
 */
public class ClassElement extends Element
{
   /**
    * Class record.
    */
   private ClassRecord _classRecord;

   /**
    * Constructor.
    * 
    * @param parent parent element
    * @param classRecord class record
    */
   public ClassElement (BinaryClassElement parent, ClassRecord classRecord)
   {
      super(parent);

      _classRecord = classRecord;
   }

   /**
    * Class record.
    */
   public ClassRecord getClassRecord ()
   {
      return _classRecord;
   }
}
