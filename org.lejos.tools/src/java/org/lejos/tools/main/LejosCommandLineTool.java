package org.lejos.tools.main;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This is an abstract implementation of a leJOS based commmand line tool.
 * 
 * <p>
 * This class has to be extended for specific tools, e.g. linker, download, etc.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public abstract class LejosCommandLineTool {

    // constructor

    /**
     * Default constructor
     */
    public LejosCommandLineTool() {
        // nothing to do yet
    }

    // abstract methods

    /**
     * get the options for this commandline tool.
     * 
     * @return options based on commons-cli
     */
    protected abstract Options getOptions();

    /**
     * get the usage message for this commandline tool.
     * 
     * @return the usage message
     */
    protected abstract String getCommandLineUsage();

    /**
     * Evaluate the commandline.
     * 
     * @param cmdLine
     *            the parsed commandline
     * @return the return code. 1 means usage message
     * @throws ParseException
     *             will be raised if the commandline is invalid
     */
    protected abstract int executeCommandLine(CommandLine cmdLine)
            throws ParseException;

    // public methods

    /**
     * the real processing of the main.
     * <p>
     * Has been implemented on instance side, to support better testing. The
     * only code to System.exit() is the static main method. This method will
     * only return the return code.
     * </p>
     * 
     * @param allArgs
     *            all arguments from the static main method
     * @return the return code, which will be forwarded to System.exit()
     */
    public int doMain(String[] allArgs) {
        int rc = 0;
        Options options = getOptions();
        try {
            CommandLine cmdLine = getCommandLine(options, allArgs);
            rc = executeCommandLine(cmdLine);
        } catch (ParseException ex) {
            // ex.printStackTrace (System.err);

            printUsage(options, getCommandLineUsage());
            rc = 1; // wrong call, show usage
        }
        return rc;
    }

    // protected methods

    /**
     * Get the commandline based on the given options and arguments from main.
     * 
     * @param options
     *            the common-cli options
     * @param allArgs
     *            all commandline arguments
     * @return the parsed commandline
     * @throws ParseException
     *             will be raised, if the args cannot be parsed correctly
     */
    protected CommandLine getCommandLine(Options options, String[] allArgs)
            throws ParseException {
        CommandLineParser parser = new GnuParser();

        if (allArgs == null) {
            throw new ParseException("no arguments");
        }
        // parse command line
        CommandLine cmdLine = parser.parse(options, allArgs, true);

        if (cmdLine.getArgs().length < 1) {
            throw new ParseException("no classes specified");
        }

        return cmdLine;
    }

    /**
     * Print the usage message based on common-cli.
     * 
     * @param options
     *            the common-cli options
     * @param cmdLineUsage
     *            the usage message to print out
     */
    protected void printUsage(Options options, String cmdLineUsage) {
        PrintWriter pw = new PrintWriter(System.err);
        HelpFormatter hf = new HelpFormatter();
        hf
                .printHelp(pw, 80, cmdLineUsage, "Options:", options, 8, 8, "",
                        false);
        pw.flush();
    }

    /**
     * Parses a string with a comma separated list of classes into a
     * <code>String[]</code>.
     * 
     * @param s
     *            the comma separated input string
     * @return an array with the parsed classes
     */
    protected String[] parseClasses(String s) {
        StringTokenizer tok = new StringTokenizer(s, ",");
        String[] classes = new String[tok.countTokens()];
        int i = 0;
        while (tok.hasMoreTokens()) {
            classes[i] = tok.nextToken();
            i = i + 1;
        }
        return classes;
    }

    // private methods

    /**
     * A helper to debug out a given commandline.
     * 
     * @param cmdLine
     *            the commandline
     */
    private void debugCommandLine(CommandLine cmdLine) {
        System.out.println(cmdLine);

        // dump all parsed out
        Iterator iter = cmdLine.iterator();
        while (iter.hasNext()) {
            Option o = (Option) iter.next();
            String optVal = o.getValue();
            System.out.println("option: " + o.getOpt() + "=" + optVal);
        }

        String[] args = cmdLine.getArgs();
        String[] classes = parseClasses(args[0]);
        for (int i = 0; i < classes.length; i++) {
            System.out.print("class=" + String.valueOf(classes[i]) + ", ");
        }
        for (int i = 1; i < args.length; i++) {
            System.out.print("arg=" + String.valueOf(args[i]) + ", ");
        }
        System.out.println();
    }
}