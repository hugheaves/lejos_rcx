package org.lejos.plugins.eclipse.actions;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.lejos.plugins.eclipse.LejosPlugin;
import org.lejos.plugins.eclipse.adaptors.AdaptorCallback;
import org.lejos.plugins.eclipse.adaptors.LejosAdaptorNative;
import org.lejos.plugins.eclipse.util.FileUtilities;

/**
 * Worker for linking a leJOS application.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class LinkWorker implements IRunnableWithProgress {

	private ICompilationUnit[] compilationUnits;

	public LinkWorker(ICompilationUnit cu) {
		this.compilationUnits = new ICompilationUnit[] { cu };
	}

	public LinkWorker(ICompilationUnit[] cus) {
		this.compilationUnits = cus;
	}

	public void run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(
				"Linking leJOS application ...",
				compilationUnits.length);
			AdaptorCallback callback = new AdaptorCallbackImpl(monitor);
			for (int i = 0; i < compilationUnits.length; i++) {
				ICompilationUnit cu = compilationUnits[i];
				monitor.setTaskName(
					"Linking leJOS application " + getFQCN(cu) + " ...");
				run(cu, callback);
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}

	// private methods

	private void run(
		ICompilationUnit compilationUnit,
		AdaptorCallback callback) {

		// @TODO introduce a sub progress monitor

		// get java home
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		File javaHome = vmInstall.getInstallLocation();

		// get the leJOS properties
		String lejosHome = LejosPlugin.getDefault().getLejosPath();
		File lejosHomeFile = new File(lejosHome);

		// get all compilation unit relevant information
		File outputLocation;
		File outputFile;
		String dir;
		String code;
		try {
			// get output location
			IPath outputPath =
				FileUtilities.getOutputLocation(
					compilationUnit.getJavaProject());
			outputLocation =
				FileUtilities.getAbsoluteLocationForResource(
					compilationUnit.getJavaProject().getProject(),
					outputPath);

			// get the file for output  
			IPath resPath =
				FileUtilities.getLinkFileForCompilationUnit(compilationUnit);
			outputFile =
				FileUtilities.getAbsoluteLocationForResource(
					compilationUnit.getJavaProject().getProject(),
					resPath);

			//@TODO
			code = getFQCN(compilationUnit);

			LejosPlugin.debug(outputFile.getAbsolutePath());

		} catch (JavaModelException ex) {
			// @TODO
			throw new RuntimeException(ex);
		}

		LejosAdaptorNative
			.linkApplication(
				javaHome,
				lejosHomeFile,
				outputLocation,
				outputFile,
				code,
		// cu,
		callback);
	}

	private String getFQCN(ICompilationUnit cu) {
		// get the declared package
		IPackageDeclaration[] packages;
		try {
			packages = cu.getPackageDeclarations();
			String packageName =
				(packages.length == 0) ? "" : packages[0].getElementName();

			// get the class name, as name for the linked leJOS program
			String className = cu.getElementName();
			className = className.substring(0, className.indexOf(".java"));

			return packageName + "." + className;
		} catch (JavaModelException e) {
			// @TODO
			return "JavaModelException ...";
		}
	}
}
