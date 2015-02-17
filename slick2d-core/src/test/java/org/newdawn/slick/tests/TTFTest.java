package org.newdawn.slick.tests;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class TTFTest extends BasicGame {
	
	private UnicodeFont ufont;
	
	private Font font;
	

	/**
	 * Create a new test for font rendering
	 */
	public TTFTest() {
		super("Font Performance Test");
	}

	/**
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		java.awt.Font f = new java.awt.Font("Papyrus", java.awt.Font.PLAIN, 16);
		//BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
		

		long t = System.currentTimeMillis();
		ufont = new UnicodeFont(f);
		ufont.getEffects().add(new ColorEffect(java.awt.Color.white));
		ufont.addGlyphs(32, 127);
		ufont.loadGlyphs();
		System.out.println("Time Taken: "+(System.currentTimeMillis()-t)+" ms");
		
//		int startCodePoint = 126;
//		int endCodePoint = 155;
//		StringBuilder str = new StringBuilder(endCodePoint - startCodePoint);
//		for (int codePoint = startCodePoint; codePoint <= endCodePoint; codePoint++)
//			str.append(Character.toChars(codePoint));
		
//		String str1 = new String(Character.toChars(216));
//		ufont.addGlyphs(str1);
//		ufont.loadGlyphs();
//		String str2 = str.toString();
//		System.out.println(str1+"\n"+str2);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		ufont.drawString(50, 50, "hello");
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer,
	 *      int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv
	 *            The arguments passed in the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(
					new TTFTest());
			container.setDisplayMode(800, 600, false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
