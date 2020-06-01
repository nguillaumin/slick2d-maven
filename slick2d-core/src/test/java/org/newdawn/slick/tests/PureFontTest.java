package org.newdawn.slick.tests;
	
import org.newdawn.slick.*;
import org.newdawn.slick.input.Input;
import org.newdawn.slick.input.sources.keymaps.USKeyboard;

/**
 * A test of the font rendering capabilities
 *
 * @author kevin
 */
public class PureFontTest extends BasicGame {
	/** The font we're going to use to render */
	private Font font;
	/** The image */
	private Image image;
	
	/**
	 * Create a new test for font rendering
	 */
	public PureFontTest() {
		super("Hiero Font Test");
	}
	
	/**
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		image = new Image("testdata/sky.jpg");
		font = new AngelCodeFont("testdata/hiero.fnt","testdata/hiero.png");
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		image.draw(0,0,800,600);
		font.drawString(100, 32, "On top of old smokey, all");
		font.drawString(100, 80, "covered with sand..");
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == USKeyboard.KEY_ESCAPE) {
			System.exit(0);
		}
	}
	
	/** The container we're using */
	private static AppGameContainer container;
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed in the test
	 */
	public static void main(String[] argv) {
		try {
			container = new AppGameContainer(new PureFontTest());
			container.setDisplayMode(800,600, DisplayMode.Opt.WINDOWED);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
