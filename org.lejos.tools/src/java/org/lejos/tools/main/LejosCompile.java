package org.lejos.tools.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.api.ToolsetFactory;

/**
 * represents a main program for the compiler
 * <p>
 * It uses the com.sun.tools.javac package for compiling
 * </p>
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
 */
public class LejosCompile extends LejosCommandLineTool
{

   // construction

   /**
    * default constructor
    */
   public LejosCompile ()
   {
      super();
   }

   // abstract CommandLineTool methods

   /**
    * Get the options for this command line tool
    * 
    * @return the common-cli based options
    */
   protected Options getOptions ()
   {
      Options options = new Options();
      // TODO some options here?
      //		options.addOption("v", "verbose", false, "verbose output");
      return options;
   }

   /**
    * Get the usage message for this tool.
    * 
    * @return a message to print out
    */
   protected String getCommandLineUsage ()
   {
      return "lejosjc[.bat] [options] <source file>";
   }

   /**
    * run (execute the command line)
    * 
    * @param cmdLine the commandline
    * @return the return code of the execution.
    * @throws ParseException will be raised if commandline is invalid, e.g.
    *            missing options, missing arguments etc.
    */
   protected int executeCommandLine (CommandLine cmdLine) throws ParseException
   {
      // arguments
      if (cmdLine.getArgs().length == 0)
      {
         throw new ParseException("No source file specified");
      }
      String[] args = new String[cmdLine.getArgs().length - 1];
      for (int i = 0; i < cmdLine.getArgs().length - 1; i++)
      {
         args[i] = cmdLine.getArgs()[i];
      }
      // source file
      String[] sourceFiles = new String[1];
      sourceFiles[0] = cmdLine.getArgs()[cmdLine.getArgs().length - 1];

      // create the toolset
      ToolsetFactory factory = ToolsetFactory.newInstance();
      IRuntimeToolset toolset = factory.newRuntimeToolset();
      //toolset.setVerbose(verbose);

      // call on toolset to compile
      try
      {
         toolset.compile(sourceFiles, args);
      }
      catch (ToolsetException ex)
      {
         ex.printStackTrace();
         return 2;
      }
      return 0;
   }

   // public methods

   /**
    * main
    * 
    * @param args all command line arguments
    */
   public static void main (String[] args)
   {
      LejosCompile main = new LejosCompile();
      int rc = main.doMain(args);
      System.exit(rc);
   }
}