package org.lejos.tools.api;

import java.io.File;
import java.io.PrintStream;

/**
 * This is the common interface to access the leJOS runtime toolset.
 * 
 * <p>
 * The toolset supports all functionality like <code>Compile</code>,<code>Link</code>,
 * <code>Download</code>,<code>FirmwareDownload</code>, etc.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public interface IRuntimeToolset {

	// attributes

	/**
	 * indicates an unknown linking behaviour.
	 */
	int LINK_METHOD_UNKNOWN = 0;

	/**
	 * indicates the linking behaviour, where all methods will be linked.
	 */
	int LINK_METHOD_ALL = 1;

	/**
	 * indicates the linking behaviour, where the linker tries to remove not
	 * used methods.
	 */
	int LINK_METHOD_OPTIMIZING = 2;

	/** indicates an unknown byte order */
	int BYTE_ORDER_UNKNOWN = 0;

	/** indicates the byte order for little endian. */
	int BYTE_ORDER_LITTLE_ENDIAN = 1;

	/** indicates the byte order for little endian. */
	int BYTE_ORDER_BIG_ENDIAN = 2;

	// methods

	/**
	 * Sets the verbose option for the whole toolset. If set to true, some
	 * useful messages will be printed out.
	 * 
	 * <p>
	 * The default is verbose = off.
	 * </p>
	 * 
	 * @param onOff true for verbose equals on
	 */
	void setVerbose(boolean onOff);

	/**
	 * Sets the output writer to be used for printing out verbose mnessages.
	 * 
	 * <p>
	 * If not set, <code>System.out</code> will be used.
	 * </p>
	 * 
	 * @param out the output stream
	 */
	void setVerboseStream(PrintStream out);

	/**
	 * Sets the progress monitor to be used.
	 * 
	 * <p>
	 * If not specified, a console based progress monitor will be used.
	 * </p>
	 * 
	 * @param progressMonitor the progress monitor
	 */
	void setProgressMonitor(IProgressMonitorToolset progressMonitor);

	/**
	 * Will compile all specified files.
	 * <p>
	 * The implementation has to care about:
	 * </p>
	 * <ul>
	 * <li>specify <code>-target 1.1</code> as far as required</li>
	 * <li>specify <code>-bootclasspath</code> with all required jar files.
	 * Currently this is <code>classes.jar</code> and <code>rcxrcxcomm.jar<code>.
	 *   </li>
	 * </ul>
	 * 
	 * @param compilerArguments all arguments for the compiler
	 * @param files the files to compile
	 */
	// TODO ENH compile
	// void compile (String compilerArguments, File[] files) throws
	// ToolsetException;

	/**
	 * Links a leJOS program.
	 * 
	 * <p>
	 * Will link a leJOS program, based on the given parameters.
	 * </p>
	 * 
	 * @see #LINK_METHOD_ALL
	 * @see #LINK_METHOD_OPTIMIZING
	 * 
	 * @param outputFile the output file, where the binary will be stored. Must
	 *        be not null.
	 * @param linkMethod the link method, whether optimizing linking or all
	 *        methods will be linked together. Must be {@link #LINK_METHOD_ALL},
	 *        {@link #LINK_METHOD_OPTIMIZING}.
	 * @param createSignatureFile create a signature file if true. The
	 *        signature file will have the same file name as the output file
	 *        with an ".signature" suffix
	 * @param classpath the classpath to use. Can be null. Then a default of
	 *        "./" will be used.
	 * @param classFiles all class files to be linked, full qualified package
	 *        name. Must contain at minimu one entry.
	 * @param args optionally arguments for linking. These args will be used when
	 *        starting the <code>main()</code> methoid. Can be null or an
	 *        empty list.
	 * @throws ToolsetException will be raised in any error case
	 */
	void link(
		File outputFile,
		int linkMethod,
		boolean createSignatureFile,
		String classpath,
		String[] classFiles,
		String[] args)
		throws ToolsetException;

	// TODO ENH download
	// void downloadExecutable () throws ToolsetException;

	// TODO ENH firmware download
	// void deleteFirmware () throws ToolsetException;
	// void downloadFirmware () throws ToolsetException;
	// void unlockFirmware () throws ToolsetException;
}
