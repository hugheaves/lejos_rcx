package org.lejos.plugins.eclipse.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.lejos.plugins.eclipse.LejosPlugin;

/**
 * Common code for all the actions.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public abstract class AbstractAction {

	// public methods

	public ICompilationUnit[] collectLinkClasses(IJavaElement javaElem) {
		// first, collect all cu's
		List allCU = new ArrayList();
		switch (javaElem.getElementType()) {

			case IJavaElement.PACKAGE_FRAGMENT :
				{
					IPackageFragment fragment = (IPackageFragment) javaElem;
					try {
						ICompilationUnit[] children =
							fragment.getCompilationUnits();
						for (int i = 0; i < children.length; i++) {
							if (hasMain(children[i])) {
								allCU.add(children[i]);
							}
						}
					} catch (JavaModelException e) {
						LejosPlugin.debug(e);
						return new ICompilationUnit[0];
					}
					break;
				}

			case IJavaElement.COMPILATION_UNIT :
				allCU.add((ICompilationUnit) javaElem);
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

	// private methods

	public boolean hasMain(ICompilationUnit cu) {
		String javaFile = cu.getElementName();
		String javaName = javaFile.substring(0, javaFile.indexOf(".java"));
		IType type = cu.getType(javaName);
		try {
			IMethod[] methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].isMainMethod()) {
					return true;
				}
			}
		} catch (JavaModelException e) {
			LejosPlugin.debug(e);
		}
		return false;
	}
}
