package org.lejos.tools.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for all known platforms.
 * 
 * <p>
 * Simply make each known platform a static variable, due its very NON-dynamic
 * behavour ;-)
 * </p>
 * 
 * @see IPlatform
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class PlatformRegistry
{
   // static attributes

   /** The RCX platform. */
   public static IPlatform RCX = new PlatformImpl("rcx", "leJOS RCX",
      "-rcx.leJOS", IRuntimeToolset.BYTE_ORDER_BIG_ENDIAN);

   /** The Emulator platform. */
   public static IPlatform EMULATOR = new PlatformImpl("emu",
      "leJOS RCX Emulator", "-emu.leJOS",
      // TODO LITTLE_ENDIAN only in windows !!! Must be changed for Linux
      IRuntimeToolset.BYTE_ORDER_LITTLE_ENDIAN);

   /** The Gamyboy platform. */
   public static IPlatform GAMEBOY = new PlatformImpl("gameboy",
      "Nintendo Gameboy", "-gameboy.leJOS", IRuntimeToolset.BYTE_ORDER_UNKNOWN);

   // TODO which byte order for gameboy ?

   // constructors

   /**
    * Hide constructor due to singleton mechanism.
    */
   private PlatformRegistry ()
   {}

   // public methods

   public static List getPlatforms ()
   {
      List l = new ArrayList();
      l.add(RCX);
      l.add(EMULATOR);
      l.add(GAMEBOY);
      return l;
   }

   // inner clases

   /**
    * Simple implementation of a platform.
    */
   private static class PlatformImpl implements IPlatform
   {
      // attributes
      private String id;
      private String description;
      private String binaryExtension;
      private int byteOder;

      // constructor
      public PlatformImpl (String anId, String aDesc, String anExt, int anOrder)
      {
         this.id = anId;
         this.description = aDesc;
         this.binaryExtension = anExt;
         this.byteOder = anOrder;
      }

      // public methods
      public String getId ()
      {
         return this.id;
      }

      public String getDescription ()
      {
         return this.description;
      }

      public String getBinaryExtension ()
      {
         return this.binaryExtension;
      }

      public int getByteOrder ()
      {
         return this.byteOder;
      }
   }
}