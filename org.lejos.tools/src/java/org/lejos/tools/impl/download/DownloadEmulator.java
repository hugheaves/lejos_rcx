package org.lejos.tools.impl.download;

import java.io.File;

/**
 * Implements the download into emulator.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class DownloadEmulator
{
   // attributes

   private File binary;
   private File executable;

   // constructor

   /**
    * Default constructor
    */
   public DownloadEmulator ()
   {
      this.executable = null;
   }

   // public methods

   public void setEmulatorExecutable (File anExecutable)
   {
      this.executable = anExecutable;
   }

   public void setBinary (File aBinary)
   {
      this.binary = aBinary;
   }
}