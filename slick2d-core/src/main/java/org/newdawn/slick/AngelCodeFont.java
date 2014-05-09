package org.newdawn.slick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A font implementation that will parse BMFont format font files. The font files can be output
 * by Hiero, which is included with Slick, and also the AngelCode font tool available at:
 * 
 * <a
 * href="http://www.angelcode.com/products/bmfont/">http://www.angelcode.com/products/bmfont/</a>
 * 
 * This implementation copes with both the font display and kerning information
 * allowing nicer looking paragraphs of text. Note that this utility only
 * supports the text BMFont format definition file.
 * 
 * @author kevin
 * @author Nathan Sweet <misc@n4te.com>
 */
public class AngelCodeFont implements Font {
	/** The renderer to use for all GL operations */
	private static SGL GL = Renderer.get();

	/**
	 * The line cache size, this is how many lines we can render before starting
	 * to regenerate lists
	 */
	private static final int DISPLAY_LIST_CACHE_SIZE = 200;
	
	/** The highest character that AngelCodeFont will support. */
	private static final int MAX_CHAR = 255;

	/** True if this font should use display list caching */
	private boolean displayListCaching = true;

	/** The image containing the bitmap font */
	private Image fontImage;
	/** The characters building up the font */
	private Glyph[] chars;
	/** The height of a line */
	private int lineHeight;
	/** The first display list ID */
	private int baseDisplayListID = -1;
	/** The eldest display list ID */
	private int eldestDisplayListID;
	/** The eldest display list  */
	private DisplayList eldestDisplayList;
	
	private boolean singleCase = false;
	private short ascent;
	private short descent; 
	private short leading; //TODO: fix leading, use for multi-line text
	
	/** The display list cache for rendered lines */
	private final LinkedHashMap displayLists = new LinkedHashMap(DISPLAY_LIST_CACHE_SIZE, 1, true) {
		protected boolean removeEldestEntry(Entry eldest) {
			eldestDisplayList = (DisplayList)eldest.getValue();
			eldestDisplayListID = eldestDisplayList.id;

			return false;
		}
	};


	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param fntFile
	 *            The location of the font defnition file
	 * @param image
	 *            The image to use for the font
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public AngelCodeFont(String fntFile, Image image) throws SlickException {
		fontImage = image;

		parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	}

	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param fntFile
	 *            The location of the font defnition file
	 * @param imgFile
	 *            The location of the font image
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public AngelCodeFont(String fntFile, String imgFile) throws SlickException {
		fontImage = new Image(imgFile);

		parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	}

	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param fntFile
	 *            The location of the font defnition file
	 * @param image
	 *            The image to use for the font
	 * @param caching
	 *            True if this font should use display list caching
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public AngelCodeFont(String fntFile, Image image, boolean caching)
			throws SlickException {
		fontImage = image;
		displayListCaching = caching;
		parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	}

	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param fntFile
	 *            The location of the font defnition file
	 * @param imgFile
	 *            The location of the font image
	 * @param caching
	 *            True if this font should use display list caching
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public AngelCodeFont(String fntFile, String imgFile, boolean caching)
			throws SlickException {
		fontImage = new Image(imgFile);
		displayListCaching = caching;
		parseFnt(ResourceLoader.getResourceAsStream(fntFile));
	}

	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param name
	 *            The name to assign to the font image in the image store
	 * @param fntFile
	 *            The stream of the font defnition file
	 * @param imgFile
	 *            The stream of the font image
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile)
			throws SlickException {
		fontImage = new Image(imgFile, name, false);

		parseFnt(fntFile);
	}

	/**
	 * Create a new font based on a font definition from AngelCode's tool and
	 * the font image generated from the tool.
	 * 
	 * @param name
	 *            The name to assign to the font image in the image store
	 * @param fntFile
	 *            The stream of the font defnition file
	 * @param imgFile
	 *            The stream of the font image
	 * @param caching
	 *            True if this font should use display list caching
	 * @throws SlickException
	 *             Indicates a failure to load either file
	 */
	public AngelCodeFont(String name, InputStream fntFile, InputStream imgFile,
			boolean caching) throws SlickException {
		fontImage = new Image(imgFile, name, false);

		displayListCaching = caching;
		parseFnt(fntFile);
	}

	/**
	 * Parse the font definition file
	 * 
	 * @param fntFile
	 *            The stream from which the font file can be read
	 * @throws SlickException
	 */
	private void parseFnt(InputStream fntFile) throws SlickException {
		if (displayListCaching) {
			baseDisplayListID = GL.glGenLists(DISPLAY_LIST_CACHE_SIZE);
			if (baseDisplayListID == 0) displayListCaching = false;
		}

		try {
			// now parse the font file
			BufferedReader in = new BufferedReader(new InputStreamReader(
					fntFile));
			String info = in.readLine();
			String common = in.readLine();
			ascent = parseMetric(common, "base="); //not used apparently ?
			//ascent = parseMetric(common, "ascent=");
			descent = parseMetric(common, "descent=");
			leading = parseMetric(common, "leading=");
			
			String page = in.readLine();

			Map<Short, List<Short>> kerning = new HashMap<Short, List<Short>>(64);
			List<Glyph> charDefs = new ArrayList<Glyph>(MAX_CHAR);
			int maxChar = 0;
			boolean done = false;
			while (!done) {
				String line = in.readLine();
				if (line == null) {
					done = true;
				} else {
					if (line.startsWith("chars c")) {
						// ignore
					} else if (line.startsWith("char")) {
						Glyph def = parseChar(line);
						if (def != null) {
							maxChar = Math.max(maxChar, def.id);
							charDefs.add(def);
						}
					}
					if (line.startsWith("kernings c")) {
						// ignore
					} else if (line.startsWith("kerning")) {
						StringTokenizer tokens = new StringTokenizer(line, " =");
						tokens.nextToken(); // kerning
						tokens.nextToken(); // first
						short first = Short.parseShort(tokens.nextToken()); // first
																			// value
						tokens.nextToken(); // second
						int second = Integer.parseInt(tokens.nextToken()); // second
																			// value
						tokens.nextToken(); // offset
						int offset = Integer.parseInt(tokens.nextToken()); // offset
																			// value
						List<Short> values = kerning.get(first);
						if (values == null) {
							values = new ArrayList<Short>();
							kerning.put(first, values);
						}
						// Pack the character and kerning offset into a short.
						values.add((short) ((offset << 8) | second));
					}
				}
			}

			chars = new Glyph[maxChar + 1];
			for (Glyph def : charDefs) {
				chars[def.id] = def;
			}

			// Turn each list of kerning values into a short[] and set on the
			// chardef.
			for (Entry<Short, List<Short>> entry : kerning.entrySet()) {
				short first = entry.getKey();
				List<Short> valueList = entry.getValue();
				short[] valueArray = new short[valueList.size()];
				for (int i=0; i<valueList.size(); i++)
					valueArray[i] = valueList.get(i);
				chars[first].kerning = valueArray;
			}
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to parse font file: " + fntFile);
		}
	}
	
	/**
	 * Returns the sprite sheet image that holds all of the images. 
	 * @return the image for this bitmap font
	 */
	public Image getImage() {
		return fontImage;
	}

	/** 
	 * If a font has the same glyphs for upper and lower case text, we can
	 * minimize its glyph page size by only using one case. If single case
	 * is enabled (by default it is disabled), then the getGlyph method
	 * (and, in turn, glyph rendering/height/width/etc.) will try to find
	 * whichever case exists for the given code point (between 65-90 for
	 * upper case characters and 97-122 for lower case).
	 * 
	 * @param enabled true to enable
	 */
	public void setSingleCase(boolean enabled) {
		this.singleCase = enabled;
	}
	
	/** 
	 * If a font has the same glyphs for upper and lower case text, we can
	 * minimize its glyph page size by only using one case. If single case
	 * is enabled (by default it is disabled), then the getGlyph method
	 * (and, in turn, glyph rendering/height/width/etc.) will try to find
	 * whichever case exists for the given code point (between 65-90 for
	 * upper case characters and 97-122 for lower case).
	 * 
	 * @return true if single case is enabled 
	 */
	public boolean isSingleCase() {
		return singleCase;
	}
	
	private short parseMetric(String str, String sub) {
		int ind = str.indexOf(sub);
		if (ind!=-1) {
			String subStr = str.substring(ind+sub.length());
			ind = subStr.indexOf(' ');
			return Short.parseShort(subStr.substring(0, ind!=-1 ? ind : subStr.length()));
		}
		return -1;
	}
	
	/**
	 * Parse a single character line from the definition
	 * 
	 * @param line
	 *            The line to be parsed
	 * @return The character definition from the line
	 * @throws SlickException Indicates a given character is not valid in an angel code font
	 */
	private Glyph parseChar(String line) throws SlickException {
		StringTokenizer tokens = new StringTokenizer(line, " =");

		tokens.nextToken(); // char
		tokens.nextToken(); // id
		short id = Short.parseShort(tokens.nextToken()); // id value
		if (id < 0) {
			return null;
		}
		if (id > MAX_CHAR) {
			throw new SlickException("Invalid character '" + id
					+ "': SpriteFont does not support characters above "
					+ MAX_CHAR);
		}

		tokens.nextToken(); // x
		short x = Short.parseShort(tokens.nextToken()); // x value
		tokens.nextToken(); // y
		short y = Short.parseShort(tokens.nextToken()); // y value
		tokens.nextToken(); // width
		short width = Short.parseShort(tokens.nextToken()); // width value
		tokens.nextToken(); // height
		short height = Short.parseShort(tokens.nextToken()); // height value
		tokens.nextToken(); // x offset
		short xoffset = Short.parseShort(tokens.nextToken()); // xoffset value
		tokens.nextToken(); // y offset
		short yoffset = Short.parseShort(tokens.nextToken()); // yoffset value
		tokens.nextToken(); // xadvance
		short xadvance = Short.parseShort(tokens.nextToken()); // xadvance
		
		if (id != ' ') {
			lineHeight = Math.max(height + yoffset, lineHeight);
		}
		Image img = fontImage.getSubImage(x, y, width, height);
		return new Glyph(id, x, y, width, height, xoffset, yoffset, xadvance, img);
	}

	/**
	 * @see org.newdawn.slick.Font#drawString(float, float, java.lang.String)
	 */
	public void drawString(float x, float y, CharSequence text) {
		drawString(x, y, text, Color.white);
	}

	/**
	 * @see org.newdawn.slick.Font#drawString(float, float, java.lang.String,
	 *      org.newdawn.slick.Color)
	 */
	public void drawString(float x, float y, CharSequence text, Color col) {
		drawString(x, y, text, col, 0, text.length() - 1);
	}

	/**
	 * @see Font#drawString(float, float, String, Color, int, int)
	 */
	public void drawString(float x, float y, CharSequence text, Color col,
			int startIndex, int endIndex) {
		fontImage.bind();
		col.bind();

		GL.glTranslatef(x, y, 0);
		if (displayListCaching && startIndex == 0 && endIndex == text.length() - 1) {
			DisplayList displayList = (DisplayList)displayLists.get(text);
			if (displayList != null) {
				GL.glCallList(displayList.id);
			} else {
				// Compile a new display list.
				displayList = new DisplayList();
				displayList.text = text;
				int displayListCount = displayLists.size();
				if (displayListCount < DISPLAY_LIST_CACHE_SIZE) {
					displayList.id = baseDisplayListID + displayListCount;
				} else {
					displayList.id = eldestDisplayListID;
					displayLists.remove(eldestDisplayList.text);
				}
				
				displayLists.put(text, displayList);

				GL.glNewList(displayList.id, SGL.GL_COMPILE_AND_EXECUTE);
				render(text, startIndex, endIndex);
				GL.glEndList();
			}
		} else {
			render(text, startIndex, endIndex);
		}
		GL.glTranslatef(-x, -y, 0);
	}

	/**
	 * Render based on immediate rendering
	 * 
	 * @param text The text to be rendered
	 * @param start The index of the first character in the string to render
	 * @param end The index of the last character in the string to render
	 */
	private void render(CharSequence text, int start, int end) {
		GL.glBegin(SGL.GL_QUADS);

		int x = 0, y = 0;
		Glyph lastCharDef = null;
		
		for (int i = 0; i < text.length(); i++) {
			char id = text.charAt(i);
			if (id == '\n') {
				x = 0;
				y += getLineHeight();
				continue;
			}
			Glyph charDef = getGlyph(id);
			if (charDef == null) {
				continue;
			}
			if (lastCharDef != null) 
				x += lastCharDef.getKerning(id);
			else
				x -= charDef.xoffset;
			
			lastCharDef = charDef;
			
			if ((i >= start) && (i <= end)) {
				charDef.image.drawEmbedded(x + charDef.xoffset, y + charDef.yoffset, charDef.width, charDef.height);
				
			}

			x += charDef.xadvance;
		}
		GL.glEnd();
	}

	/**
	 * Returns the distance from the y drawing location to the top most pixel of the specified text.
	 * 
	 * @param text
	 *            The text that is to be tested
	 * @return The yoffset from the y draw location at which text will start
	 */
	public int getYOffset(String text) {
		DisplayList displayList = null;
		if (displayListCaching) {
			displayList = (DisplayList)displayLists.get(text);
			if (displayList != null && displayList.yOffset != null) return displayList.yOffset.intValue();
		}

		int stopIndex = text.indexOf('\n');
		if (stopIndex == -1) stopIndex = text.length();

		int minYOffset = 10000;
		for (int i = 0; i < stopIndex; i++) {
			Glyph charDef = getGlyph(text.charAt(i));
			if (charDef == null) {
				continue;
			}
			minYOffset = Math.min(charDef.yoffset, minYOffset);
		}

		if (displayList != null) displayList.yOffset = new Short((short)minYOffset);
		
		return minYOffset;
	}

	/**
	 * @see org.newdawn.slick.Font#getHeight(java.lang.String)
	 */
	public int getHeight(CharSequence text) {
		DisplayList displayList = null;
		if (displayListCaching) {
			displayList = (DisplayList)displayLists.get(text);
			if (displayList != null && displayList.height != null) return displayList.height.intValue();
		}

		int lines = 0;
		int maxHeight = 0;
		for (int i = 0; i < text.length(); i++) {
			char id = text.charAt(i);
			if (id == '\n') {
				lines++;
				maxHeight = 0;
				continue;
			}
			// ignore space, it doesn't contribute to height
			if (id == ' ') {
				continue;
			}
			Glyph charDef = getGlyph(id);
			if (charDef == null) {
				continue;
			}

			maxHeight = Math.max(charDef.height + charDef.yoffset,
					maxHeight);
		}

		maxHeight += lines * getLineHeight();
		
		if (displayList != null) displayList.height = new Short((short)maxHeight);
		
		return maxHeight;
	}

	/**
	 * @see org.newdawn.slick.Font#getWidth(java.lang.String)
	 */
	public int getWidth(CharSequence text) {
		DisplayList displayList = null;
		if (displayListCaching) {
			displayList = (DisplayList)displayLists.get(text);
			if (displayList != null && displayList.width != null) return displayList.width.intValue();
		}
		
		int maxWidth = 0;
		int width = 0;
		Glyph lastCharDef = null;
		for (int i = 0, n = text.length(); i < n; i++) {
			char id = text.charAt(i);
			if (id == '\n') {
				width = 0;
				continue;
			}
			Glyph charDef = getGlyph(id);
			if (charDef == null) {
				continue;
			}

			if (lastCharDef != null) 
				width += lastCharDef.getKerning(id);
//			else //first glyph
//				width -= charDef.xoffset;
//			
			lastCharDef = charDef;
			
			//space characters have zero width, so use their xadvance instead
			if (i < n - 1 || charDef.width==0) {
				width += charDef.xadvance;
			} else {
				width += charDef.width + charDef.xoffset;
			}
			maxWidth = Math.max(maxWidth, width);
		}
		if (displayList != null) displayList.width = new Short((short)maxWidth);
		
		return maxWidth;
	}

	/**
	 * @see org.newdawn.slick.Font#getLineHeight()
	 */
	public int getLineHeight() {
		return lineHeight;
	}

	/** 
	 * Requires export from the newest version of Hiero. Alternatively, you 
	 * could manually add <tt>descent=XX</tt> to the end of the 'common' line 
	 * in your font file.
	 * 
	 * The descent is the distance from the font's baseline to the 
	 * bottom of most alphanumeric characters with descenders.
	 */
	public int getDescent() {
		return descent;
	}

	/** 
	 * Requires export from the newest version of Hiero. Alternatively, you 
	 * could manually add <tt>ascent=XX</tt> to the end of the 'common' line 
	 * in your font file.
	 * 
	 * The ascent is the distance from the font's baseline to the top of most 
	 * alphanumeric characters.
	 */
	public int getAscent() {
		return ascent;
	}
	
	/**
	 * Returns the character definition for the given character. 
	 * 
	 * @param c the desired character
	 * @return the CharDef with glyph info
	 */
	public Glyph getGlyph(char c) {
		Glyph g = c<0 || c>= chars.length ? null : chars[c];
		if (g!=null)
			return g;
		if (g==null && singleCase) {
			if (c>=65 && c<=90)
				c += 32;
			else if (c>=97 && c<=122)
				c -= 32;
		}
		return c<0 || c>= chars.length ? null : chars[c];
	}
	
	/**
	 * The definition of a single character as defined in the AngelCode file
	 * format
	 * 
	 * @author kevin
	 */
	public static class Glyph {
		/** The id of the character */
		public final short id;
		/** The x location on the sprite sheet */
		public final short x;
		/** The y location on the sprite sheet */
		public final short y;
		/** The width of the character image */
		public final short width;
		/** The height of the character image */
		public final short height;
		/** The amount the x position should be offset when drawing the image */
		public final short xoffset;
		/** The amount the y position should be offset when drawing the image */
		public final short yoffset;
		/** The amount to move the current position after drawing the character */
		public final short xadvance;
		/** The sub-image containing the character */
		public final Image image;
		/** The display list index for this character */
		protected short dlIndex;
		/** The kerning info for this character */
		protected short[] kerning;

		protected Glyph(short id, short x, short y, short width, short height,
				short xoffset, short yoffset, short xadvance, Image image) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
			this.xadvance = xadvance;
			this.image = image;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "[CharDef id=" + id + " x=" + x + " y=" + y + "]";
		}

		/**
		 * Get the kerning offset between this character and the specified character.
		 * @param otherCodePoint The other code point
		 * @return the kerning offset 
		 */
		public int getKerning (int otherCodePoint) {
			if (kerning == null) return 0;
			int low = 0;
			int high = kerning.length - 1;
			while (low <= high) {
				int midIndex = (low + high) >>> 1;
				int value = kerning[midIndex];
				int foundCodePoint = value & 0xff;
				if (foundCodePoint < otherCodePoint)
					low = midIndex + 1;
				else if (foundCodePoint > otherCodePoint)
					high = midIndex - 1;
				else 
					return value >> 8;
			}
			return 0;
		}
	}

	/**
	 * A descriptor for a single display list
	 * 
	 * @author Nathan Sweet <misc@n4te.com>
	 */
	static private class DisplayList {
		/** The if of the distance list */
		int id;
		/** The offset of the line rendered */
		Short yOffset;
		/** The width of the line rendered */
		Short width;
		/** The height of the line rendered */
		Short height;
		/** The text that the display list holds */
		CharSequence text;
	}
}
