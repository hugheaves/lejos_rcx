package org.lejos.tools.eclipse.plugin;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.lejos.tools.api.IProgressMonitorToolset;
import org.lejos.tools.api.IRuntimeToolset;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.api.ToolsetFactory;

/**
 * The <code>EclipseToolsetFacade</code> provides the services of the <code>IRuntimeToolset</code>
 * based on Eclipse interfaces.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class EclipseToolsetFacade {

	// attributes

	/** a progress monitor to use */
	private IProgressMonitorToolset progressMonitor;

	// public methods

	/**
	 * Sets a progress monitor for the further actions.
	 * 
	 * @param aMonitor a progress monitor
	 */
	public void setProgressMonitor(IProgressMonitorToolset aMonitor) {
		this.progressMonitor = aMonitor;

	}

	/**
	 * Link one compilation unit with the given preferences
	 * 
	 * @param aCompilationUnit the compulation unit
	 * @param aPreferences the lejos preferences
	 * @throws JavaModelException will be raised, if some internal model error
	 *             occured
	 * @throws ToolsetException will be raised if some error occurs during
	 *             calling the leJOS toolset
	 */
	public void linkCU(
		ICompilationUnit aCompilationUnit,
		LejosPreferences aPreferences)
		throws ToolsetException, JavaModelException {
		ToolsetFactory factory = ToolsetFactory.newInstance();
		IRuntimeToolset toolset = factory.newRuntimeToolset();
		if (this.progressMonitor != null) {
			toolset.setProgressMonitor(this.progressMonitor);
		}

		// prepare all arguments
		File outputFile =
			EclipseUtilities.getOutputFile(
				aCompilationUnit,
				aPreferences.getExtensionBinary());
		int linkMethod = aPreferences.getLinkMethod();

		// only create signature, if specified in preferences
		boolean createSignatureFile = aPreferences.isCreateSignatureFile();
		String[] classNames = new String[1];
		classNames[0] = EclipseUtilities.getFQCN(aCompilationUnit);
		String[] args = new String[0]; // no args in eclipse

		// get the classpath from Eclipse
		String classpath =
			EclipseUtilities.getOutputFolder(aCompilationUnit).toString()
				+ File.pathSeparator;
		String[] lejosLibs = aPreferences.getDefaultClasspathEntries();
		try {
			for (int i = 0; i < lejosLibs.length; i++) {
				IPath lejosLibPath = new Path(lejosLibs[i]);
				Path absoluteLibPath =
					EclipseUtilities.findFileInPlugin(
						"org.lejos",
						lejosLibPath.toString());
				classpath =
					classpath + absoluteLibPath.toString() + File.pathSeparator;
			}
		} catch (IOException ex) {
			throw new ToolsetException(
				"Could not create CLASSPATH for link: ",
				ex);
		}

		// finally, call the link process
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Linking ");
		for (int i = 0; i < classNames.length; i++) {
			if (i > 0) {
				sbuf.append(", ");
			}
			sbuf.append(classNames[i]);
		}
		sbuf.append(" to " + outputFile.toString());
		LejosPlugin.debug(sbuf.toString());
		toolset.link(
			outputFile,
			linkMethod,
			createSignatureFile,
			classpath,
			classNames,
			args);
	}

	/**
	 * Link one java element.
	 * 
	 * <p>
	 * Will collect all classed for this java element, and link every
	 * compilation unit.
	 * </p>
	 * 
	 * @param aJavaElem the java element
	 * @param aPreferences the lejos preferences
	 * @throws JavaModelException will be raised, if some internal model error
	 *             occured
	 * @throws ToolsetException will be raised if some error occurs during
	 *             calling the leJOS toolset
	 */
	public void linkJavaElement(
		IJavaElement aJavaElem,
		LejosPreferences aPreferences)
		throws ToolsetException, JavaModelException {

		ICompilationUnit[] cus = EclipseUtilities.collectLinkClasses(aJavaElem);

		for (int i = 0; i < cus.length; i++) {
			linkCU(cus[i], aPreferences);
		}
	}

	/**
	 * Link an array of java elements.
	 * 
	 * <p>
	 * Will collect all classed for this java element, and link every
	 * compilation unit.
	 * </p>
	 * 
	 * @param aJavaElems an array with java elements
	 * @param aPreferences the lejos preferences
	 * @throws JavaModelException will be raised, if some internal model error
	 *             occured
	 * @throws ToolsetException will be raised if some error occurs during
	 *             calling the leJOS toolset
	 */
	public void linkJavaElement(
		IJavaElement[] aJavaElems,
		LejosPreferences aPreferences)
		throws ToolsetException, JavaModelException {

		for (int i = 0; i < aJavaElems.length; i++) {
			linkJavaElement(aJavaElems[i], aPreferences);
		}
	}

	/**
	 * Count the number of compilation units for a list of java elements.
	 * 
	 * @param elems an array of java elements
	 * @return the number of compilation units
	 * @throws JavaModelException will be raised in any error case within the
	 *             java model
	 */
	public int countCU(IJavaElement[] elems) throws JavaModelException {
		int n = 0;
		for (int i = 0; i < elems.length; i++) {
			IJavaElement elem = elems[i];
			ICompilationUnit[] cus = EclipseUtilities.collectLinkClasses(elem);
			n = n + cus.length;
		}
		return n;
	}
}
