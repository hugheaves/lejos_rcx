package org.lejos.tools.eclipse.plugin.view.model;

import js.tinyvm.Binary;

/**
 * Root element for classes of a binary.
 */
public class BinaryClassElement extends Element
{
   /**
    * Constructor.
    */
   public BinaryClassElement (BinaryElement parent)
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
