package org.lejos.tools.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.impl.link.LejosLink;

/**
 * This is the default implementation of the IRuntimeToolset.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class RuntimeToolsetImpl
	extends AbstractToolsetImpl
	implements IRuntimeToolset {

	// implementation of IRuntimeToolset

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
	 * @param classpathString the classpath to use. Can be null. Then a default
	 *        of "./" will be used.
	 * @param classFiles all class files to be linked, full qualified package
	 *        name. Must contain at minimu one entry.
	 * @param args optionally arguments for linking. These args will be used
	 *        when starting the <code>main()</code> methoid. Can be null or
	 *        an empty list.
	 * @throws ToolsetException will be raised in any error case
	 */
	public void link(
		File outputFile,
		int linkMethod,
		boolean createSignatureFile,
		String classpathString,
		String[] classFiles,
		String[] args)
		throws ToolsetException {

		this.getProgressMonitor().beginTask(
			"Linking to output file " + String.valueOf(outputFile),
			100);

		// convert classpath to object representation
		Classpath classpath = new Classpath(classpathString);
		// check for valid file specification
		checkFiles(outputFile, classFiles, classpath);
		this.getProgressMonitor().worked(25);

		// start now the real link process
		LejosLink link = new LejosLink();
		link.setLinkMethod(linkMethod);
		// TODO what for endian ?
		link.setByteOrder(IRuntimeToolset.BYTE_ORDER_BIG_ENDIAN);
		this.getProgressMonitor().worked(50);

		// parse the class files
		link.prepareCompiledClasses(classFiles, args, classpath);
		this.getProgressMonitor().worked(75);

		// and finally create the output file
		File signatureFile = new File(outputFile.toString() + ".signature");
		link.createBinaryFile(outputFile, signatureFile);
		this.getProgressMonitor().done();
	}

	// private methods

	/**
	 * Check for existence and writable files.
	 * 
	 * @param outputFile the outputFile must be writable
	 * @param classFiles the class files if specified must exist
	 * @param classpath the classpath to be used to check for the class files
	 * @throws ToolsetException raised, if anything is not valid
	 */
	private void checkFiles(
		File outputFile,
		String[] classFiles,
		Classpath classpath)
		throws ToolsetException {

		Map invalidFiles = new HashMap();
		StringBuffer details = new StringBuffer();
		// check whether output files can be created
		if (outputFile == null) {
			details.append("\toutputFile must not be null\n");
		} else {
			if (outputFile.exists() && (!outputFile.canWrite())) {
				invalidFiles.put(outputFile, "can not create");
			}
		}
		// check for existing classFiles
		if (classFiles == null) {
			details.append("\tclassFiles must not be null\n");
		} else {
			for (int i = 0; i < classFiles.length; i++) {
				if (!classpath.exists(classFiles[i])) {
					invalidFiles.put(
						classFiles[i],
						"class file does not exist");
				}
			}
		}

		// create toolset exception if invalidFiles have been detected
		if (invalidFiles.size() > 0) {
			Iterator iter = invalidFiles.keySet().iterator();
			while (iter.hasNext()) {
				Object o = iter.next();
				String msg = (String) invalidFiles.get(o);
				details.append(
					"\tFile " + String.valueOf(o) + ": " + msg + "\n");
			}
		}
		String detailsString = details.toString();
		if (detailsString.length() > 0) {
			throw new ToolsetException(
				"Can not link, due to file errors.\n" + detailsString);
		}
	}
}
