package org.lejos.tools.eclipse.plugin.view.model;

/**
 * Abstract model element.
 */
public abstract class Element
{
  /**
   * Parent element.
   */
  private Element _parent;
  
  /**
   * Constructor.
   * 
   * @param parent parent element
   */
   public Element (Element parent)
   {
      _parent = parent;
   }
   
   /**
    * Parent element.
    */
   public Element getParent ()
   {
      return _parent;
   }
}
