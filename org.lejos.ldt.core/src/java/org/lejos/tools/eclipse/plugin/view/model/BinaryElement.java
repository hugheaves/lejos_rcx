package org.lejos.tools.eclipse.plugin.view.model;

import js.tinyvm.Binary;

/**
 * Model element for Binary.
 */
public class BinaryElement extends Element
{
   /**
    * The binary.
    */
   private Binary _binary;

   /**
    * Constructor.
    */
   public BinaryElement (Binary binary)
   {
      super(null);
      
      _binary = binary;
   }
   
   /**
    * Binary.
    */
   public Binary getBinary ()
   {
      return _binary;
   }
}
