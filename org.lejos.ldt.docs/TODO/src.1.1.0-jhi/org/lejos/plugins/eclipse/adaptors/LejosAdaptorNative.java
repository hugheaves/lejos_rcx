package org.lejos.plugins.eclipse.adaptors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.lejos.plugins.eclipse.LejosPlugin;
import org.lejos.plugins.eclipse.util.FileUtilities;

/**
 * Adaptor class to connect to the leJOS 
 * native applications, e.g. to the .exe files.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class LejosAdaptorNative {

	/**
	 * Link a leJOS application.
	 * 
	 * <p>The application will be linked into a file
	 * <code>Xxx.leJOS</code>.<p>
	 *
	 * @param javaHome installation directory of java
	 * @param lejosHome home directory of lejos installation
	 * @param outputLocation the eclipse output directory
	 * @param outputFile the output file
	 * @param fccn full qualified class name 
	 * @param callback the callback, for cancel and progress indication
	 */
	public static void linkApplication(
		File javaHome,
		File lejosHome,
		File outputLocation,
		File outputFile,
		String fqcn,
		AdaptorCallback callback) {

		// we need some files for construction of commandline
		File java = new File(javaHome, "bin/java");
		File toolsJarFile = new File(lejosHome, "lib/jtools.jar");
		File classesJarFile = new File(lejosHome, "lib/classes.jar");
		File rcxrcxcommJarFile = new File(lejosHome, "lib/rcxrcxcomm.jar");

		// build the commandline
		String cmd =
			FileUtilities.getAbsolutePath(java)
				+ " -Dtinyvm.write.order=BE -Dtinyvm.home="
				+ FileUtilities.getAbsolutePath(lejosHome)
				+ " -cp "
				+ FileUtilities.getAbsolutePath(toolsJarFile)
				+ " js.tinyvm.TinyVM"
				+ " -classpath "
				+ FileUtilities.getAbsolutePath(outputLocation)
				+ File.pathSeparator
				+ FileUtilities.getAbsolutePath(classesJarFile)
				+ File.pathSeparator
				+ FileUtilities.getAbsolutePath(rcxrcxcommJarFile)
				+ " "
				+ fqcn
//				+ " -verbose=9"
				+ " -o "
				+ FileUtilities.getAbsolutePath(outputFile.getAbsoluteFile());
		LejosPlugin.debug("cmd: " + cmd);

		// now start the external java vm
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			
			// redirect verbose output to a file ".signature"
			File signatureFile = new File (outputFile.getAbsoluteFile() + ".signature");
			FileOutputStream os = new FileOutputStream (signatureFile);
			// @TODO finish it
			new PrintWriter (os).println ("@TODO");

			// we use err stream to read until process has finished
			BufferedReader err =
				new BufferedReader(new InputStreamReader(process.getErrorStream()));
			StringBuffer errMsg = new StringBuffer();

			// read until process does not response anymore
			while (true) {
				// leave loop ?
				if (callback.isCanceled()) {
					process.destroy();
					try {
						process.waitFor();
						return;
					} catch (InterruptedException e) {
						// in this case, ignore the exception
					}
					break;
				}

				// read from error stream
				String s = err.readLine();
				if (s == null) {
					break;
				} else {
					LejosPlugin.debug("err: " + s);
					s = s.trim();
					if (s.length() > 0) {
						errMsg.append(s + "\n");
					}
				}
			}

			// did an error occur ?
			int rc = process.exitValue();
			LejosPlugin.debug("rc: "+String.valueOf (rc));
			LejosPlugin.debug("err: " + errMsg.toString());
			if ((rc != 0) || (errMsg.length() > 0)) {
				// @TODO
				throw new RuntimeException(errMsg.toString());
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
