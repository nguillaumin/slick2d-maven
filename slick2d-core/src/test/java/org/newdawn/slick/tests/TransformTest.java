package org.newdawn.slick.tests;

import org.newdawn.slick.*;
import org.newdawn.slick.input.sources.keymaps.USKeyboard;

/**
 * A test for transforming the graphics context
 *
 * @author kevin
 */
public class TransformTest extends BasicGame {
	/** The current scale applied to the graphics context */
	private float scale = 1;
	/** True if we should be scaling up */
	private boolean scaleUp;
	/** True if we should be scaling down */
	private boolean scaleDown;
	
	/**
	 * Create a new test of graphics context rendering
	 */
	public TransformTest() {
		super("Transform Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) {
		container.setTargetFrameRate(100);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer contiainer, Graphics g) {
		g.translate(320,240);
		g.scale(scale, scale);

		g.setColor(Color.red);
		for (int x=0;x<10;x++) {
			for (int y=0;y<10;y++) {
				g.fillRect(-500+(x*100), -500+(y*100), 80, 80);
			}
		}
		
		g.setColor(new Color(1,1,1,0.5f));
		g.fillRect(-320,-240,640,480);
		g.setColor(Color.white);
		g.drawRect(-320,-240,640,480);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		if (scaleUp) {
			scale += delta * 0.001f;
		}
		if (scaleDown) {
			scale -= delta * 0.001f;
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == USKeyboard.KEY_ESCAPE) {
			System.exit(0);
		}
		if (key == USKeyboard.KEY_Q) {
			scaleUp = true;
		}
		if (key == USKeyboard.KEY_A) {
			scaleDown = true;
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
		if (key == USKeyboard.KEY_Q) {
			scaleUp = false;
		}
		if (key == USKeyboard.KEY_A) {
			scaleDown = false;
		}
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed to the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new TransformTest(), 800, 600, DisplayMode.Opt.WINDOWED, false);
			container.setDisplayMode(640,480, DisplayMode.Opt.WINDOWED);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
