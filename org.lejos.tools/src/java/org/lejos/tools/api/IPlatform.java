package org.lejos.tools.api;

/**
 * This is the interface to represent a leJOS platform.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public interface IPlatform
{
   /**
    * Gets the unique platform id.
    * 
    * @return the unique platform id
    */
   String getId ();

   /**
    * Gets the platform description.
    * 
    * @return the platform description
    */
   String getDescription ();

   /**
    * Gets the extension for the binary file.
    * 
    * @return the extension of a binary file, e.g. "-rcx.leJOS"
    */
   String getBinaryExtension ();

   /**
    * Gets the endian for this platform.
    * 
    * @see IRuntimeToolset#BYTE_ORDER_BIG_ENDIAN
    * @see IRuntimeToolset#BYTE_ORDER_LITTLE_ENDIAN
    * @see IRuntimeToolset#BYTE_ORDER_UNKNOWN
    * 
    * @return the extension of a binary file, e.g. "-rcx.leJOS"
    */
   int getByteOrder ();
}