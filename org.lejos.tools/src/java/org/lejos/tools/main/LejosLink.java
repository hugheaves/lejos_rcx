package org.lejos.tools.main;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.api.PlatformRegistry;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.api.ToolsetFactory;

/**
 * This is the main program for the linker.
 * 
 * <p>
 * It will use the runtime toolset, to call the link process. Any progress will
 * be indicated through a console progress monitor.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class LejosLink extends LejosCommandLineTool
{

   // constructors

   /**
    * Default constructor
    */
   public LejosLink ()
   {
      super();
   }

   // public methods

   // implementation of abstract methods

   /**
    * Get the options for this commandline tool.
    * 
    * @return the common-cli based options
    */
   protected Options getOptions ()
   {
      Options options = new Options();
      options.addOption("o", "output", true, "dump binary file into path");
      options.addOption("s", "signature", false, "create signature file");
      options.addOption("v", "verbose", false, "verbose output");
      options.addOption("a", "all", false, "include all methods");
      return options;
   }

   /**
    * Get the usage message for this tool.
    * 
    * @return a message to print out
    */
   protected String getCommandLineUsage ()
   {
      return "lejoslink[.bat] [options] class1[,class2,...] "
         + "[arg1 arg2 ...]";
   }

   /**
    * Do the "real" work, means execute the commandline.
    * 
    * @param cmdLine the commandline
    * @return the return code of the execution.
    * @throws ParseException will be raised if commandline is invalid, e.g.
    *            missing options, missing arguments etc.
    */
   protected int executeCommandLine (CommandLine cmdLine) throws ParseException
   {
      // get all required params from commandline
      if (cmdLine.getArgs().length == 0)
      {
         throw new ParseException("No classes specified");
      }
      String[] classNames = parseClasses(cmdLine.getArgs()[0]);
      if (!cmdLine.hasOption("o"))
      {
         throw new ParseException("No output specified");
      }
      String output = cmdLine.getOptionValue("o");
      File outputFile = new File(output);

      // all optional arguments
      boolean verbose = cmdLine.hasOption("v");
      int linkMode = IRuntimeToolset.LINK_METHOD_OPTIMIZING;
      if (cmdLine.hasOption("a"))
      {
         linkMode = IRuntimeToolset.LINK_METHOD_ALL;
      }
      boolean createSignature = cmdLine.hasOption("s");

      // handle args
      String[] args = new String[cmdLine.getArgs().length - 1];
      for (int i = 1; i < cmdLine.getArgs().length; i++)
      {
         args[i - 1] = cmdLine.getArgs()[i];
      }

      // create the toolset
      ToolsetFactory factory = ToolsetFactory.newInstance();
      IRuntimeToolset toolset = factory.newRuntimeToolset();
      toolset.setVerbose(verbose);

      // now call the toolset to link
      try
      {
         toolset.link(PlatformRegistry.RCX, outputFile, linkMode,
            createSignature,
            // TODO get the classpath. Handle it after supporting compile.
            ".", classNames, args);
      }
      catch (ToolsetException ex)
      {
         ex.printStackTrace();
         return 2;
      }
      return 0;
   }

   // static methods

   /**
    * The main entry point for the LejosLink tool.
    * 
    * @param args all commandline arguments
    */
   public static void main (String[] args)
   {
      LejosLink main = new LejosLink();
      int rc = main.doMain(args);
      System.exit(rc);
   }
}