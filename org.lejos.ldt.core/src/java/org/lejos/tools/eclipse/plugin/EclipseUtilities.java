package org.lejos.tools.eclipse.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.lejos.tools.api.ToolsetFactory;

/**
 * The <code>EclipseUtilities</code> provides some common services to handle
 * Eclipse object types.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public final class EclipseUtilities extends ToolsetFactory {

	// static methods

	/**
	 * Get the output folder for the compilation unit.
	 * 
	 * @param cu the compilation unit
	 * @return a file specifiying the output directory
	 * @throws JavaModelException will be raised, if output location cannot be
	 * determined
	 */
	public static File getOutputFolder(ICompilationUnit cu)
		throws JavaModelException {
		// from CU to java project, from java project to output folder
		IPath outputFolder = cu.getJavaProject().getOutputLocation();

		// return the absolute file reference
		File absoluteOutputFolder =
			getAbsoluteLocationForResource(
				cu.getJavaProject().getProject(),
				outputFolder);

		return absoluteOutputFolder;
	}

	/**
	 * Get a file reference for the compilation unit, within output folder, wit
	 * the given extension.
	 * 
	 * @param cu the compilation unit
	 * @param anExtension the given extension
	 * @return a file reference to the output file
	 * @throws JavaModelException will be raised, if output location cannot be
	 * determined
	 */
	public static File getOutputFile(ICompilationUnit cu, String anExtension)
		throws JavaModelException {
		// from CU to java project, from java project to output folder
		IPath outputFolder = cu.getJavaProject().getOutputLocation();

		// get the declared package
		IPackageDeclaration[] packages = cu.getPackageDeclarations();
		String packageName = "";
		if (packages.length > 0) {
			packageName = packages[0].getElementName();
		}
		String packagePath = packageName.replace('.', '/');

		// get the class name from the CU
		String className = cu.getElementName();
		className = className.substring(0, className.indexOf(".java"));

		// create the output file
		IPath outputFile = outputFolder.append(packagePath);
		outputFile = outputFile.append(className + anExtension);

		// return the absolute file reference
		File absoluteOutputFile =
			getAbsoluteLocationForResource(
				cu.getJavaProject().getProject(),
				outputFile);

		return absoluteOutputFile;
	}

	/**
	 * Get an absolute file reference for a resource within the project.
	 * 
	 * @param aProject the project to be used
	 * @param aResourcePath the path of the resource
	 * @return an IPath, representing the path to the resource
	 */
	public static File getAbsoluteLocationForResource(
		IProject aProject,
		IPath aResourcePath) {
		// we have to know the physical project location
		IPath absoluteProjectPath = aProject.getLocation();

		// we have to remove the project name again, otherwise it would be
		// there twice. API of Eclipse a little bit strange
		aResourcePath = aResourcePath.removeFirstSegments(1);

		// create the output file
		IPath absoluteOutputFileName =
			absoluteProjectPath.append(aResourcePath);
		File absoluteOutputFile = new File(absoluteOutputFileName.toOSString());

		return absoluteOutputFile;
	}

	/**
	 * Get the Full Qualified Class Name (FQCN) for the compilation unit.
	 * 
	 * @param cu the compilation unit
	 * @return a full qualified class name in "." notation
	 * @throws JavaModelException will be raised, if package declarations
	 * cannot be determined
	 */
	public static String getFQCN(ICompilationUnit cu)
		throws JavaModelException {
		// get the declared package
		IPackageDeclaration[] packages;
		packages = cu.getPackageDeclarations();
		String packageName = "";
		if (packages.length > 0) {
			packageName = packages[0].getElementName();
		}

		// get the class name, as name for the linked leJOS program
		String className = cu.getElementName();
		className = className.substring(0, className.indexOf(".java"));

		return packageName + "." + className;
	}

	/**
	 * Checks, whether a compilation unit has a main methids,
	 * 
	 * @param cu the compilation unit
	 * @return true, if the compilation unit has a main method
	 * @throws JavaModelException will be raised, if output location cannot be
	 * determined
	 */
	public static boolean hasMain(ICompilationUnit cu)
		throws JavaModelException {
		String javaFile = cu.getElementName();
		String javaName = javaFile.substring(0, javaFile.indexOf(".java"));
		IType type = cu.getType(javaName);
		IMethod[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].isMainMethod()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Collect all classes, which can be linked.
	 * 
	 * <p>
	 * Each class to link must have a main method.
	 * </p>
	 * <p>
	 * If the java element is a package, all included classes and subpackages
	 * will be collected too. </p
	 * <p>
	 * The method will do this using a recursive approach.
	 * </p>
	 * 
	 * @param javaElem the given java element
	 * @return an array with compilation units
	 * @throws JavaModelException will be raised, if output location cannot be
	 * determined
	 */
	public static ICompilationUnit[] collectLinkClasses(IJavaElement javaElem)
		throws JavaModelException {
		// first, collect all cu's
		Set allCU = new HashSet();
		switch (javaElem.getElementType()) {

			case IJavaElement.PACKAGE_FRAGMENT_ROOT :
				{
					IPackageFragmentRoot root = (IPackageFragmentRoot) javaElem;
					IJavaElement[] elems = root.getChildren();
					for (int i = 0; i < elems.length; i++) {
						IPackageFragment aPackage = (IPackageFragment) elems[i];
						ICompilationUnit[] subCUs =
							collectLinkClasses(elems[i]);
						for (int j = 0; j < subCUs.length; j++) {
							allCU.add(subCUs[j]);
						}
					}
				}
				break;

			case IJavaElement.PACKAGE_FRAGMENT :
				{
					IPackageFragment fragment = (IPackageFragment) javaElem;
					ICompilationUnit[] children =
						fragment.getCompilationUnits();
					for (int i = 0; i < children.length; i++) {
						if (hasMain(children[i])) {
							allCU.add(children[i]);
						}
					}
					// now recurse over all java elements
					if (fragment.hasSubpackages()) {
						IPackageFragmentRoot root =
							(IPackageFragmentRoot) fragment.getParent();
						IJavaElement[] elems = root.getChildren();
						// now check, whether these packages are a subpackage
						// of the given package
						for (int i = 0; i < elems.length; i++) {
							IPackageFragment aPackage =
								(IPackageFragment) elems[i];
							String fragmentName = fragment.getElementName();
							String packageName = aPackage.getElementName();
							// if package names match and are not the same
							if ((packageName.indexOf(fragmentName) >= 0)
								&& (!packageName.equals(fragmentName))) {
								ICompilationUnit[] subCUs =
									collectLinkClasses(elems[i]);
								for (int j = 0; j < subCUs.length; j++) {
									allCU.add(subCUs[j]);
								}
							}
						}
					}
				}
				break;

			case IJavaElement.COMPILATION_UNIT :
				{
					ICompilationUnit cu = (ICompilationUnit) javaElem;
					if (hasMain(cu)) {
						allCU.add(cu);
					}
				}
				break;

			case IJavaElement.TYPE :
				{
					IType type = (IType) javaElem;
					ICompilationUnit cu = type.getCompilationUnit();
					if (hasMain(cu)) {
						allCU.add(cu);
					}
				}
				break;

			default :
				LejosPlugin.debug(
					"selected object of type "
						+ String.valueOf(javaElem.getElementType())
						+ " not supported");
				return new ICompilationUnit[0];
		}
		ICompilationUnit[] cus = new ICompilationUnit[allCU.size()];
		allCU.toArray(cus);
		return cus;
	}

	// constructor

	/**
	 * Make constructor private, as this is a utilits class only.
	 */
	private EclipseUtilities() {
		// nothing to do
	}
}
