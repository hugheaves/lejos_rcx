package org.lejos.tools.eclipse.plugin.view.model;

import js.tinyvm.ConstantRecord;

/**
 * Constant.
 */
public class ConstantElement extends Element
{
   /**
    * Class record.
    */
   private ConstantRecord _constantRecord;

   /**
    * Constructor.
    * 
    * @param parent parent element
    * @param constantRecord constant record
    */
   public ConstantElement (BinaryConstantElement parent, ConstantRecord constantRecord)
   {
      super(parent);

      _constantRecord = constantRecord;
   }

   /**
    * Class record.
    */
   public ConstantRecord getConstantRecord ()
   {
      return _constantRecord;
   }
}
