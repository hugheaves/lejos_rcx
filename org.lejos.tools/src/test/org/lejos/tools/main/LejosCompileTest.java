package org.lejos.tools.main;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.cli.*;

/**
 * Tests for <code>LejosCompile</code> class
 * 
 * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz</a>
 */
public class LejosCompileTest extends TestCase {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // public methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(LejosCompileTest.class));
    }

    public LejosCompileTest(String name) {
        super(name);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // test methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testCommandLineValid() {
        NoLejosCompile noCompile = new NoLejosCompile();

        // check 
        int rc = noCompile.doMain(new String[] { "-verbose", "Source.java" });
        assertEquals(0, rc);
        assertEquals(1,noCompile.cl.getArgs().length);
        assertEquals("-verbose", noCompile.cl.getArgs()[0]);
    } // testCommandLineValid()

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * no compiling
     */
    public static class NoLejosCompile extends LejosLink {
        public CommandLine cl;
        public CommandLine getCommandLine(Options options, String[] args)
            throws ParseException {
            cl = super.getCommandLine(options, args);
            return cl;
        }
        public int executeCommandLine(CommandLine cmdLine) {
            // do nothing here
            return 0;
        }
    }

}
