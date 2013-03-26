package org.newdawn.slick.tools.hiero.truetype;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A wrapper that brings Java font and TTF read fonts together.
 *
 * @author kevin
 */
public class FontData {
	/** The maximum font file size that will be read */
	private static long MAX_FILE_SIZE = 2000000;
	
	/** The user's home directory for locating fonts */
	private static String userhome = System.getProperty("user.home");
	
	/** The windows list of possible font locations */
 	private static File[] win32 = new File[] {
 											  new File("c:/windows/fonts")
 	};
	/** The macos list of possible font locations */
	private static File[] macos = new File[] {new File("/System/Library/Fonts/"),
											  new File("/Library/Fonts/"),
											  new File("/System Folder/Fonts/"),
											  new File("/Network/Library/Fonts/"),
											  new File(userhome+"/Library/Fonts")
	};
	/** The linux list of possible font locations */
	private static File[] linux = new File[] {new File("/usr/share/fonts"),
			  								  new File("/usr/share/X11/fonts")
	};
	
	/** True if we're displaying debug en-route */
	private static boolean DEBUG = true;
	/** The list of family names */
	private static ArrayList families = new ArrayList();
	/** The list of all the fonts available */
	private static ArrayList fonts;
	/** Plain fonts */
	private static HashMap plain = new HashMap();
	/** Bold fonts */
	private static HashMap bold = new HashMap();
	/** Italic fonts */
	private static HashMap italic = new HashMap();
	/** Bold Italic fonts */
	private static HashMap bolditalic = new HashMap();
	/** Set the status listener */
	private static StatusListener statusListener;

	/** The list of processed directories */
	private static ArrayList processed = new ArrayList();
	
	/**
	 * Set the the status listener that will be notified of font loading progress
	 * 
	 * @param listener The listener to be notified of font loading progress
	 */
	public static void setStatusListener(StatusListener listener) {
		statusListener = listener;
	}
	
	/**
	 * Get the list of all the font family names available
	 * 
	 * @return The list of family names available
	 */
	public static String[] getFamilyNames() {
		if (fonts == null) {
			getAllFonts();
		}
		
		return (String[]) families.toArray(new String[0]);
	}
	
	/**
	 * Get the plain version of a family name
	 * 
	 * @param familyName The font family to retrieve
	 * @return The plain version of the font or null if no plain version exits
	 */
	public static FontData getPlain(String familyName) {
		FontData data = (FontData) plain.get(familyName);
		
		return data;
	}
	
	/**
	 * Get the bold version of the font
	 * 
	 * @param familyName The name of the font family
	 * @return The bold version of the font or null if no bold version exists
	 */
	public static FontData getBold(String familyName) {
		FontData data = (FontData) bold.get(familyName);
		
		return data;
	}
	
	/**
	 * Get the bold italic  version of the font
	 * 
	 * @param familyName The name of the font family
	 * @return The bold italic  version of the font or null if no bold italic version exists
	 */
	public static FontData getBoldItalic(String familyName) {
		FontData data = (FontData) bolditalic.get(familyName);
		
		return data;
	}

	/**
	 * Get the italic version of the font
	 * 
	 * @param familyName The name of the font family
	 * @return The italic version of the font or null if no italic version exists
	 */
	public static FontData getItalic(String familyName) {
		FontData data = (FontData) italic.get(familyName);
		
		return data;
	}
	
	/**
	 * Get a styled version of a particular font family
	 * 
	 * @param familyName The name of the font family
	 * @param style The style (@see java.awt.Font#PLAIN)
	 * @return The styled font or null if no such font exists
	 */
	public static FontData getStyled(String familyName, int style) {
		boolean b = (style & Font.BOLD) != 0;
		boolean i = (style & Font.ITALIC) != 0;
	
		if (b & i) {
			return getBoldItalic(familyName);
		} else if (b) {
			return getBold(familyName);
		} else if (i) {
			return getItalic(familyName);
		} else {
			return getPlain(familyName);
		}
	}
	
	/**
	 * Process a directory potentially full of fonts
	 * 
	 * @param dir The directory of fonts to process
	 * @param fonts The fonts list to add to
	 */
	private static void processFontDirectory(File dir, ArrayList fonts) {
		if (!dir.exists()) {
			return;
		}
		if (processed.contains(dir)) {
			return;
		}
		processed.add(dir);
		
		File[] sources = dir.listFiles();
		if (sources == null) {
			return;
		}
		
		for (int j=0;j<sources.length;j++) {
			File source = sources[j];
		
			if (source.getName().equals(".")) {
				continue;
			}
			if (source.getName().equals("..")) {
				continue;
			}
			if (source.isDirectory()) {
				processFontDirectory(source, fonts);
				continue;
			}
			if (source.getName().toLowerCase().endsWith(".ttf")) {
				try {
					if (statusListener != null) {
						statusListener.updateStatus("Processing "+source.getName());
					}
					FontData data = new FontData(new FileInputStream(source), 1);
					fonts.add(data);
					
					String famName = data.getFamilyName();
					if (!families.contains(famName)) {
						families.add(famName);
					}
					
					boolean bo = data.getJavaFont().isBold();
					boolean it = data.getJavaFont().isItalic();
					
					if ((bo) && (it)) {
						bolditalic.put(famName, data);
					} else if (bo) {
						bold.put(famName, data);
					} else if (it) {
						italic.put(famName, data);
					} else {
						plain.put(famName, data);
					}
				} catch (Exception e) {
					if (DEBUG) {
						System.err.println("Unable to process: "+source.getAbsolutePath()+" ("+e.getClass()+": "+e.getMessage()+")");
					}
					if (statusListener != null) {
						statusListener.updateStatus("Unable to process: "+source.getName());
					}
				}
			}
		}	
	}
	
	/**
	 * Get all the fonts available
	 * 
	 * @return The list of fonts available
	 */
	public static FontData[] getAllFonts() {
		if (fonts == null) {
			fonts = new ArrayList();
			
			String os = System.getProperty("os.name");
			File[] locs = new File[0];
			
			if (os.startsWith("Windows")) {
				locs = win32;
			}
			if (os.startsWith("Linux")) {
				locs = linux;
			}
			if (os.startsWith("Mac OS")) {
				locs = macos;
			}
			
			for (int i=0;i<locs.length;i++) {
				File loc = locs[i];
			
				processFontDirectory(loc, fonts);
			}

			if (os.startsWith("Linux")) {
				locateLinuxFonts(new File("/etc/fonts/fonts.conf"));
			}
		}
		
		
		return (FontData[]) fonts.toArray(new FontData[0]);
	}
	
	/**
	 * Locate the linux fonts based on the XML configuration file
	 * 
	 * @param file The location of the XML file
	 */
	private static void locateLinuxFonts(File file) {
		if (!file.exists()) {
			System.err.println("Unable to open: "+file.getAbsolutePath());
			return;
		}

		try {
			InputStream in = new FileInputStream(file);
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			PrintStream pout = new PrintStream(temp);
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.indexOf("DOCTYPE") == -1) {
					pout.println(line);
				}
			}
			
			in = new ByteArrayInputStream(temp.toByteArray());
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document document = builder.parse(in);
			
			NodeList dirs = document.getElementsByTagName("dir");
			for (int i=0;i<dirs.getLength();i++) {
				Element element = (Element) dirs.item(i);
				String dir = element.getFirstChild().getNodeValue();
				
				if (dir.startsWith("~")) {
					dir = dir.substring(1);
					dir = userhome + dir;
				}
				
				addFontDirectory(new File(dir));
			}
			
			NodeList includes = document.getElementsByTagName("include");
			for (int i=0;i<includes.getLength();i++) {
				Element element = (Element) dirs.item(i);
				String inc = element.getFirstChild().getNodeValue();
				if (inc.startsWith("~")) {
					inc = inc.substring(1);
					inc = userhome + inc;
				}
				
				locateLinuxFonts(new File(inc));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to process: "+file.getAbsolutePath());
		}
	}
	
	/**
	 * Add a font directory
	 * 
	 * @param dir The directory containing fonts
	 */
	public static void addFontDirectory(File dir) {
		processFontDirectory(dir, fonts);
	}
	
	/** The java font version of the font */
	private Font javaFont;
	/** The size of this instance of the font */
	private float size;
	/** The UPEM */
	private int upem;
	/** The mapping table from TTF */
	private Map ansiKerning;
	/** The width of the characters */
	private int[] charWidth;
	/** The name of the font */
	private String fontName;
	/** The name of the font family */
	private String familyName;
	
	/**
	 * Create a new font data element
	 * 
	 * @param ttf The TTF file to read
	 * @param size The size of the new font
	 * @throws IOException Indicates a failure to 
	 */
	private FontData(InputStream ttf, float size) throws IOException {
		if (ttf.available() > MAX_FILE_SIZE) {
			throw new IOException("Can't load font - too big");
		}
		byte[] data = IOUtils.toByteArray(ttf);
		if (data.length > MAX_FILE_SIZE) {
			throw new IOException("Can't load font - too big");
		}
		
		this.size = size;
		try {
			javaFont = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data));
			TTFFile rawFont = new TTFFile();
			if (!rawFont.readFont(new FontFileReader(data))) {
				throw new IOException("Invalid font file");
			}
			upem = rawFont.getUPEM();
			ansiKerning = rawFont.getAnsiKerning();
			charWidth = rawFont.getAnsiWidth();
			fontName = rawFont.getPostScriptName();
			familyName = rawFont.getFamilyName();
			
			String name = getName();
			System.err.println("Loaded: "+name+" ("+data.length+")");
			boolean bo = false;
			boolean it = false;
			if (name.indexOf(',') >= 0) {
				name = name.substring(name.indexOf(','));
			
				if (name.indexOf("Bold") >= 0) {
					bo = true;
				}
				if (name.indexOf("Italic") >= 0) {
					it = true;
				}
			}
			
			if ((bo & it)) {
				javaFont = javaFont.deriveFont(Font.BOLD | Font.ITALIC);
			} else if (bo) {
				javaFont = javaFont.deriveFont(Font.BOLD);
			} else if (it) {
				javaFont = javaFont.deriveFont(Font.ITALIC);
			}
		} catch (FontFormatException e) {
			IOException x = new IOException("Failed to read font");
			x.initCause(e);
			throw x;
		}
	}
	
	/**
	 * Private default constructor for derivation
	 */
	private FontData() {
	}
	
	/**
	 * Derive a new version of this font based on a new size
	 * 
	 * @param size The size of the new font
	 * @return The new font data
	 */
	public FontData deriveFont(float size) {
		return deriveFont(size, javaFont.getStyle());
	}

	/**
	 * Derive a new version of this font based on a new size
	 * 
	 * @param size The size of the new font
	 * @param style The style of the new font
	 * @return The new font data
	 */
	public FontData deriveFont(float size, int style) {
		FontData original = getStyled(getFamilyName(), style);
		FontData data = new FontData();
		data.size = size;
		data.javaFont = original.javaFont.deriveFont(style, size);
		data.upem = upem;
		data.ansiKerning = ansiKerning;
		data.charWidth = charWidth;
		
		return data;
	}
	
	/**
	 * Get the full name of this font
	 * 
	 * @return The full name of this font
	 */
	public String getName() {
		return fontName;
	}
	
	/**
	 * Get the family name of this font
	 * 
	 * @return The family name of this font
	 */
	public String getFamilyName() {
		return familyName;
	}
	
	/**
	 * Get the size of this instance of the font data
	 * 
	 * @return The size of the font
	 */
	public float getSize() {
		return size;
	}
	
	/**
	 * Get the Java font representing this font data
	 * 
	 * @return The Java font representing this font data
	 */
	public Font getJavaFont() {
		return javaFont;
	}
	
	/**
	 * Get the kerning value between two characters
	 * 
	 * @param first The first character
	 * @param second The second character
	 * @return The amount of kerning to apply between the two characters
	 */
	public int getKerning(char first, char second) {
		Map toMap = (Map) ansiKerning.get(new Integer(first));
		if (toMap == null) {
			return 0;
		}
		
		Integer kerning = (Integer) toMap.get(new Integer(second));
		if (kerning == null) {
			return 0;
		}
		
		return Math.round(convertUnitToEm(size, kerning.intValue()));
	}

    /**
     * Covert a "units" value to a point value based on a given
     * point size.
     * 
     * @param ptSize The point size of the font being rendered
     * @param units The units to be converted
     * @return The size in points
     */
    public float convertUnitToEm(float ptSize, int units) {
    	return (units * ptSize) / upem;
    }
    
	/**
	 * Get the "advance" value for the given character
	 * 
	 * @param c The character to get the advance for
	 * @return The adavance value for the given character
	 */
	public float getAdvance(char c) {
		return Math.round(convertUnitToEm(size, charWidth[c]));
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Font Data face='"+getName()+"' size="+size+" bold="+javaFont.isBold()+" italic="+javaFont.isItalic()+"]";
	}
	
}
