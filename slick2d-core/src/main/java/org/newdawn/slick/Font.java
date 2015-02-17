package org.newdawn.slick;


/**
 * The proprieties of any font implementation
 * 
 * @author Kevin Glass
 */
public interface Font {
	/**
	 * Get the width of the given string
	 * 
	 * @param str The string to obtain the rendered with of
	 * @return The width of the given string
	 */
	int getWidth(CharSequence str);
	
	/**
	 * Get the height of the given string
	 * 
	 * @param str The string to obtain the rendered with of
	 * @return The width of the given string
	 */
	int getHeight(CharSequence str);
	
	/**
	 * Get the maximum height of any line drawn by this font
	 * 
	 * @return The maxium height of any line drawn by this font
	 */
	int getLineHeight();
	
	/**
	 * Draw a string to the screen
	 * 
	 * @param x The x location at which to draw the string
	 * @param y The y location at which to draw the string
	 * @param text The text to be displayed
	 */
	void drawString(float x, float y, CharSequence text);

	/**
	 * Draw a string to the screen
	 * 
	 * @param x The x location at which to draw the string
	 * @param y The y location at which to draw the string
	 * @param text The text to be displayed
	 * @param col The colour to draw with
	 */
	void drawString(float x, float y, CharSequence text, Color col);


	/**
	 * Draw part of a string to the screen. Note that this will
	 * still position the text as though it's part of the bigger string.
	 * 
	 * @param x The x location at which to draw the string
	 * @param y The y location at which to draw the string
	 * @param text The text to be displayed
	 * @param col The colour to draw with
	 * @param startIndex The index of the first character to draw
	 * @param endIndex The index of the last character from the string to draw
	 */
	void drawString(float x, float y, CharSequence text, Color col, int startIndex, int endIndex);
}