package org.newdawn.slick.tests;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

/**
 * A test for transparent colour specification
 *
 * @author kevin
 */
public class TransparentColorTest extends BasicGame {
	/** The image we're currently displaying */
	private Image image;
	/** The image we're currently displaying */
	private Image timage;
	
	private Image gifImage, gifTImage;
	private Image tgaImage, tgaTImage;
	
	/**
	 * Create a new image rendering test
	 */
	public TransparentColorTest() {
		super("Transparent Color Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		image = new Image("testdata/transtest.png");
		timage = new Image("testdata/transtest.png",new Color(94,66,41,255));
		
		gifImage = new Image("testdata/logo.gif");
		gifTImage = new Image("testdata/logo.gif", new Color(254, 255, 252));
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		g.setBackground(Color.lightGray);
//		image.draw(0,0, inside ? Color.red : Color.white);
//		timage.draw(10,310);
		
		image.draw();
		timage.draw(image.getWidth(), 0);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		int mx = container.getInput().getMouseX();
		int my = container.getInput().getMouseY();
		try {
			Color c =image.getColor(mx, my);
			System.out.println(c.getRed()+" "+c.getBlue()+" "+c.getGreen());
		} catch (Exception e) {}
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new TransparentColorTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
	}
}
