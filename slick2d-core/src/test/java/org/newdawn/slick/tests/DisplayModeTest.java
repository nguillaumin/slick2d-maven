package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * A test to demonstrate world clipping as opposed to screen clipping
 *
 * @author kevin
 */
public class DisplayModeTest extends BasicGame {
	/** The texture to apply over the top */
	private Image tex;
	private AppGameContainer appContainer;
	
	/**
	 * Create a new tester for the clip plane based clipping
	 */
	public DisplayModeTest() {
		super("Display Mode Alpha Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		this.appContainer = (AppGameContainer)container;
		
		tex = new Image("testdata/grass.png");
		container.getGraphics().setBackground(Color.lightGray);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input input = container.getInput();
		
		if (input.isKeyPressed(Input.KEY_1))
			appContainer.setDisplayMode(800, 600, false);
		else if (input.isKeyPressed(Input.KEY_2))
			appContainer.setDisplayMode(1024, 768, false);
		else if (input.isKeyPressed(Input.KEY_3))
			appContainer.setDisplayMode(1280, 600, false);
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		tex.draw(50, 50);
		tex.draw(container.getWidth()-tex.getWidth(), 300);
		g.drawString("input: "+container.getInput().getMouseX()+" , "+container.getInput().getMouseY(), 10, 20);
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new DisplayModeTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
