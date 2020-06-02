package org.newdawn.slick.tests;

import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.input.Input;
import org.newdawn.slick.input.sources.keymaps.USKeyboard;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.util.Log;

/**
 * A test box containing a bunch of tests that can be used for quickly sanity
 * checking tests.
 *
 * @author kevin
 */
public class TestBox extends BasicGame {
	private static final Log LOG = new Log(TestBox.class);

	/** The games that have been added */
	private ArrayList games = new ArrayList();
	/** The current game */
	private BasicGame currentGame;
	/** The index of the current game */
	private int index;
	/** The game container */
	private AppGameContainer container;
	
	/**
	 * Create a new box containing all the tests
	 */
	public TestBox() {
		super("Test Box");
	}
	
	/**
	 * Add a game to the box
	 * 
	 * @param game The game to add to the test box
	 */
	public void addGame(Class game) {
		games.add(game);
	}
	
	/**
	 * Move to the next game
	 */
	private void nextGame() {
		LOG.info("Loading next game...");
		if (index == -1) {
			return;
		}
		
		index++;
		if (index >= games.size()) {
			index=0;
		}
	
		startGame();
	}

	private void previousGame() {
		LOG.info("Loading previous game...");
		if (index == -1) {
			return;
		}

		index--;
		if (index < 0) {
			index=0;
		}

		startGame();
	}
	
	/**
	 * Start a particular game
	 */
	private void startGame() {
		try {
			currentGame = (BasicGame) ((Class) games.get(index)).newInstance();
			container.getGraphics().setBackground(Color.black);
			currentGame.init(container);
			currentGame.bindControls();
			currentGame.render(container, container.getGraphics());
		} catch (Exception e) {
			LOG.error(e);
		}
		
		container.setTitle(currentGame.getTitle());
	}

	@Override
	public void bindControls() {
		Input.bindKeyPress(USKeyboard.KEY_ENTER, false, this::nextGame);
		Input.bindKeyPress(USKeyboard.KEY_BACK, false, this::previousGame);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer c) {
		if (games.size() == 0) {
			currentGame = new BasicGame("NULL") {
				public void init(GameContainer container) {
				}

				public void update(GameContainer container, int delta) throws SlickException {
				}

				public void render(GameContainer container, Graphics g) throws SlickException {
				}
			};
			currentGame.init(c);
			index = -1;
		} else {
			index = 0;
			container = (AppGameContainer) c;
			startGame();
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		currentGame.update(container, delta);
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		SlickCallable.enterSafeBlock();
		currentGame.render(container, g);
		SlickCallable.leaveSafeBlock();
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerButtonPressed(int, int)
	 */
	public void controllerButtonPressed(int controller, int button) {
		currentGame.controllerButtonPressed(controller, button);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerButtonReleased(int, int)
	 */
	public void controllerButtonReleased(int controller, int button) {
		currentGame.controllerButtonReleased(controller, button);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerDownPressed(int)
	 */
	public void controllerDownPressed(int controller) {
		currentGame.controllerDownPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerDownReleased(int)
	 */
	public void controllerDownReleased(int controller) {
		currentGame.controllerDownReleased(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerLeftPressed(int)
	 */
	public void controllerLeftPressed(int controller) {
		currentGame.controllerLeftPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerLeftReleased(int)
	 */
	public void controllerLeftReleased(int controller) {
		currentGame.controllerLeftReleased(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerRightPressed(int)
	 */
	public void controllerRightPressed(int controller) {
		currentGame.controllerRightPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerRightReleased(int)
	 */
	public void controllerRightReleased(int controller) {
		currentGame.controllerRightReleased(controller);
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#controllerUpPressed(int)
	 */
	public void controllerUpPressed(int controller) {
		currentGame.controllerUpPressed(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#controllerUpReleased(int)
	 */
	public void controllerUpReleased(int controller) {
		currentGame.controllerUpReleased(controller);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
		currentGame.keyReleased(key, c);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		currentGame.mouseMoved(oldx, oldy, newx, newy);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		currentGame.mousePressed(button, x, y);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
		currentGame.mouseReleased(button, x, y);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#mouseWheelMoved(int)
	 */
	public void mouseWheelMoved(int change) {
		currentGame.mouseWheelMoved(change);
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		TestBox box = new TestBox();
		box.addGame(AnimationTest.class);
		box.addGame(AntiAliasTest.class);
		box.addGame(BigImageTest.class);
		box.addGame(ClipTest.class);
		box.addGame(DuplicateEmitterTest.class);
		box.addGame(FlashTest.class);
		box.addGame(FontPerformanceTest.class);
		box.addGame(FontTest.class);
		box.addGame(GeomTest.class);
		box.addGame(GradientTest.class);
		box.addGame(GraphicsTest.class);
		box.addGame(ImageBufferTest.class);
		box.addGame(ImageReadTest.class);
		box.addGame(ImageTest.class);
		box.addGame(KeyRepeatTest.class);
		box.addGame(MusicListenerTest.class);
		box.addGame(PackedSheetTest.class);
		box.addGame(PedigreeTest.class);
		box.addGame(PureFontTest.class);
		box.addGame(ShapeTest.class);
		box.addGame(SoundTest.class);
		box.addGame(SpriteSheetFontTest.class);
		box.addGame(TransparentColorTest.class);

		AppGameContainer container = new AppGameContainer(box, 800, 600, DisplayMode.Opt.WINDOWED);
		container.start();
	}
}
