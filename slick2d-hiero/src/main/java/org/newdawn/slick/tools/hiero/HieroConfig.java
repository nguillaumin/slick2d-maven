package org.newdawn.slick.tools.hiero;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A central configuration managed
 *
 * @author kevin
 */
public class HieroConfig {
	/** The default scan size for distance fields */
	public static int DFIELD_SCAN_SIZE = 20;
	/** The scale of the image used as source for distance fields */
	public static int DFIELD_SCALE_UP = 8;
	
	/** The user's home directory */
	private static File home = new File(System.getProperty("user.home"));
	/** Hiero's configuration directory */
	private static File config = new File(home,".hiero");
	
	/**
	 * Initialise the configuration
	 *
	 */
	public static void init() {
		if (!config.exists()) {
			config.mkdirs();
		}
	}
	
	/**
	 * Get a file from the configuration
	 * 
	 * @param name The name of the configuration file to retrieve
	 * @return A handle to the requested file
	 */
	public static File getConfigFile(String name) {
		init();
		
		return new File(config, name);
	}
	
	/**
	 * List the files with a given extension in the configuration directory
	 * 
	 * @param ext The extension to search for
	 * @return The list of files from the configuration directory
	 */
	public static File[] listFiles(final String ext) {
		init();
		
		return config.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith(ext);
			}
		});
	}
}
