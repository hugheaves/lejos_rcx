package org.lejos.tools.eclipse.plugin.view.model;

import js.tinyvm.Binary;

/**
 * All constants of a binary.
 */
public class BinaryConstantElement extends Element
{
   /**
    * Constructor.
    */
   public BinaryConstantElement (BinaryElement parent)
   {
      super(parent);
   }

   /**
    * Binary.
    */
   public Binary getBinary ()
   {
      return ((BinaryElement) getParent()).getBinary();
   }
}
