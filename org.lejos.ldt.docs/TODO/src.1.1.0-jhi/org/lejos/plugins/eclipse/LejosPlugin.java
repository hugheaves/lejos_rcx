package org.lejos.plugins.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.lejos.plugins.eclipse.util.FileUtilities;

/**
 * The main plugin class to be used in the desktop.
 */
public class LejosPlugin extends AbstractUIPlugin {
	
	//The shared instance.
	static LejosPlugin plugin;
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	// LejosPath 
	public static final String LEJOS_PATH = "lejos.path";
	static final String DEFAULT_LEJOS_PATH = "";
	
	static final String[] WIN_PORTS={"com1","com2","usb"};
	static final String[] LINUX_PORTS={"/dev/ttyS0","/dev/ttyS1","usb"};
	public static final String LEJOS_PORT ="lejos.port";
	static final int DEFAULT_LEJOS_PORT = 0;

	public static final String LEJOS_ISFAST = "lejos.isFast";
	static final boolean DEFAULT_LEJOS_ISFAST = true;

	// static OS stuff
	private static final int UNKNOWN=0;
	private static final int WIN=1;
	private static final int LINUX=2;
	private static int type;
	
	static {
		String os=System.getProperty("os.name").toLowerCase();
		if (os.startsWith("win")) type=WIN;
		else if (os.startsWith("linux")) type=LINUX;
		else type=UNKNOWN;		
	}
	
	/**
	 * The constructor.
	 */
	public LejosPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("lejos.lejosPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		
	}

	/**
	 * Returns the shared instance.
	 */
	public static LejosPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}	

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= LejosPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/** 
	 * Initializes a preference store with default preference values 
	 * for this plug-in.
	 * @param store the preference store to fill
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(LEJOS_PATH, DEFAULT_LEJOS_PATH);
		store.setDefault(LEJOS_PORT, DEFAULT_LEJOS_PORT);
		store.setDefault(LEJOS_ISFAST, DEFAULT_LEJOS_ISFAST);
	}
	
	void setLejosPath(String path) {
		getPreferenceStore().setValue(LEJOS_PATH, path);
	}
	
	public String getPlatformLejosPath() {
		return getPreferenceStore().getString(LEJOS_PATH);
	}
	
	public String getLejosPath() {
		return FileUtilities.getAbsolutePath(getPreferenceStore().getString(LEJOS_PATH));
	}	
	
	void setLejosPort(int port) {
		getPreferenceStore().setValue(LEJOS_PORT, port);
	}
	
	public int getLejosPort() {
		return getPreferenceStore().getInt(LEJOS_PORT);
	}

	void setLejosIsFast(boolean isFast) {
		getPreferenceStore().setValue(LEJOS_ISFAST, isFast);
	}
	
	public boolean getLejosIsFast() {
		return getPreferenceStore().getBoolean(LEJOS_ISFAST);
	}

	private IEditorInput getEditorInput() throws IOException {
		IWorkbench wb=PlatformUI.getWorkbench();
		IWorkbenchWindow[] wws=wb.getWorkbenchWindows();
		if (wws.length!=1) throw new IOException("Failed to find workbench window");
		IWorkbenchWindow ww=wws[0];
		
		IWorkbenchPage[] wps=ww.getPages();
		if (wws.length!=1) throw new IOException("Failed to find workbench page");
		IWorkbenchPage wp=wps[0];
		
		IEditorPart ep=wp.getActiveEditor();
		if (ep==null) throw new IOException("Failed to find active editor");
		return ep.getEditorInput();		
	}
	
	public IFile getCurrentJavaFile() throws IOException {
		IEditorInput ei=getEditorInput();
		if (!(ei instanceof IFileEditorInput)) throw new IOException("IFileEditorInput expected");
		IFileEditorInput fei=(IFileEditorInput)ei;
		return fei.getFile();
	}

	public ICompilationUnit getCurrentCompilationUnit() throws IOException {
		IEditorInput ei=getEditorInput();
		IWorkingCopyManager wcm=JavaUI.getWorkingCopyManager();
		return wcm.getWorkingCopy(ei);
	}

	// static helpers
	// for string only uses forward slashed (works on windows & linux)
		
	public boolean isValid() {
		return type!=0;
	}
	
	public static boolean isWindows() {
		return type==WIN;
	}
	
	public static boolean isLinux() {
		return type==LINUX;
	}

	public static String[] getPorts() {
		if (isWindows()) return WIN_PORTS;
		return LINUX_PORTS;		
	}	

	public static String getPort(int port) {
		if (isWindows()) return WIN_PORTS[port];
		return LINUX_PORTS[port];
	}

	public File getFileInPlugin(IPath path) {
		try {
			URL installURL= new URL(getDescriptor().getInstallURL(), path.toString());
			URL localURL= Platform.asLocalURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			return null;
		}
	}
	
	// static methods
	
	public static void debug (String msg) {
		if (plugin.isDebugging()) {
			System.out.println ("org.lejos.plugins.eclipse: " + msg);
		}
	}

	public static void debug (Throwable t) {
		if (plugin.isDebugging()) {
			System.out.println ("org.lejos.plugins.eclipse: Exception occured: " + t.toString());
		}
	}
}
