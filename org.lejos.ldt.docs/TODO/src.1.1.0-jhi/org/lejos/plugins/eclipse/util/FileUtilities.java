package org.lejos.plugins.eclipse.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.lejos.plugins.eclipse.LejosPlugin;

/**
 * Some common file utilities for the leJOS plugin.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class FileUtilities {

	/**
	 * The linked file for a compilation unit (e.g. a Java Class) will be stored
	 * in the output folder, in the same directory, where the .class will be stored.
	 * 
	 * <p>The linked file will be named .leJOS.</p>
	 * 
	 * @param compilationUnit the compilation unit to be used
	 * @return an IPath, representing the path to the resource
	 */
	public static IPath getLinkFileForCompilationUnit(ICompilationUnit aCompilationUnit)
		throws JavaModelException {
		// we give the output file the same folder/name as input file
		// and store it in output folder

		// take the output folder, append package, class name, .leJOS

		// output folder taken from project
		// @TODO what happens with individual output folders per source folder ?
		IPath outputPath =
			aCompilationUnit.getJavaProject().getOutputLocation();

		// get the declared package
		IPackageDeclaration[] packages =
			aCompilationUnit.getPackageDeclarations();
		String packageName =
			(packages.length == 0) ? "" : packages[0].getElementName();
		String packagePath = packageName.replace('.', '/');

		// get the class name, as name for the linked leJOS program
		String className = aCompilationUnit.getElementName();
		className = className.substring(0, className.indexOf(".java"));

		// create the output file name				
		outputPath = outputPath.append(packagePath + "/");
		outputPath = outputPath.append(className + ".leJOS");

		LejosPlugin.debug("getLinkFileForCompilationUnit: " + String.valueOf(outputPath));

		return outputPath;
	}

	/**
	 * Get an absolute file reference for a resource within the
	 * project.
	 * 
	 * <p>The linked file will be named .leJOS.</p>
	 * 
	 * @param compilationUnit the compilation unit to be used
	 * @return an IPath, representing the path to the resource
	 */
	public static File getAbsoluteLocationForResource(
		IProject aProject,
		IPath aResourcePath)
		throws JavaModelException {
		// we have to know the physical project location
		IPath absoluteProjectPath = aProject.getLocation();

		// we have to remove the project name again, otherwise it would be 
		// there twice. API of Eclipse a little bit strange
		aResourcePath = aResourcePath.removeFirstSegments(1);

		// create the output file
		IPath absoluteOutputFileName =
			absoluteProjectPath.append(aResourcePath);
		File absoluteOutputFile = new File(absoluteOutputFileName.toOSString());

		LejosPlugin.debug(
			"getAbsoluteLocationForResource: "
				+ String.valueOf(absoluteOutputFile));

		return absoluteOutputFile;
	}

	/**
	 * Get the sabsolute path for a file.
	 * 
	 * @param file
	 * @return
	 */
	public static String getAbsolutePath(File file) {
		String s = file.getAbsolutePath();
		return FileUtilities.getAbsolutePath(s);
	}

	/**
	 * Get the output location for the 
	 * compilation unit.
	 * 
	 * @param compilationUnit the compilation unit to be used
	 * @return an IPath, representing the path to the resource
	 */
	public static IPath getOutputLocation(IJavaProject aProject)
		throws JavaModelException {
		IPath outputPath =
			aProject.getJavaProject().getOutputLocation();
		LejosPlugin.debug("getOutputLocation: " + String.valueOf(outputPath));
		return outputPath;
	}

	public static String getAbsolutePath(IFile file) {
		IPath p = file.getLocation();
		String s = p.toString();
		return FileUtilities.getAbsolutePath(s);
	}

	public static String getAbsolutePath(IPath path) {
		String s = path.toString();
		return FileUtilities.getAbsolutePath(s);
	}

	public static String getAbsolutePath(String file) {
		file.replace('\\', '/');

		// quoting for window (for now assuming Linux dont use name with spaces...)
		int p = file.indexOf(" ");
		if (p != -1)
			return "\"" + file + "\"";
		return file;
	}
}
