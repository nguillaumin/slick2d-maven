package org.newdawn.slick;

import java.util.Properties;

import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.input.Input;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.pbuffer.FBOGraphics;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.glfw.GLFW.*;

/**
 * A generic game container that handles the game loop, fps recording and
 * managing the input system
 *
 * @author kevin
 * @author tyler
 */
public abstract class GameContainer implements GUIContext {
	private static final Log LOG = new Log(GameContainer.class);

	public static long GAME_WINDOW = -1L;

	/** The renderer to use for all GL operations */
	protected static SGL GL = Renderer.get();
	/** The shared drawable if any */
	protected static FBOGraphics SHARED_DRAWABLE;
	
	/** The time the last frame was rendered */
	protected long lastFrame;
	/** The last time the FPS recorded */
	protected long lastFPS;
	/** The last recorded FPS */
	protected int recordedFPS;
	/** The current count of FPS */
	protected int fps;
	/** True if we're currently running the game loop */
	protected boolean running = true;
	
	/** The width of the display */
	protected static int width = 0;
	/** The height of the display */
	protected static int height = 0;
	/** The game being managed */
	protected Game game;
	
	/** The default font to use in the graphics context */
	private Font defaultFont;
	/** The graphics context to be passed to the game */
	private Graphics graphics;
	
	/** The input system to pass to the game */
	protected Input input;
	/** The FPS we want to lock to */
	protected int targetFPS = -1;
	/** True if we should show the fps */
	private boolean showFPS = true;
	/** The minimum logic update interval */
	protected long minimumLogicInterval = 1;
	/** The stored delta */
	protected long storedDelta;
	/** The maximum logic update interval */
	protected long maximumLogicInterval = 0;
	/** The last game started */
	protected Game lastGame;
	/** True if we should clear the screen each frame */
	protected boolean clearEachFrame = true;
	
	/** True if the game is paused */
	protected boolean paused;
	/** True if we should force exit */
	protected boolean forceExit = true;
	/** True if vsync has been requested */
	protected boolean vsync;
	/** Smoothed deltas requested */
	protected boolean smoothDeltas;
	/** The number of samples we'll attempt through hardware */
	protected int samples;
	
	/** True if this context supports multisample */
	protected boolean supportsMultiSample;
	
	/** True if we should render when not focused */
	protected boolean alwaysRender;
	/** True if we require stencil bits */
	protected static boolean stencil;
	
	/**
	 * Create a new game container wrapping a given game
	 */
	protected GameContainer(Game game) {
		this.game = game;
		lastFrame = getTime();

		getBuildVersion();
	}

	public static void enableStencil() {
		stencil = true;
	}
	
	public void setDefaultFont(Font font) {
		if (font != null) {
			this.defaultFont = font;
		} else {
			LOG.warn("Please provide a non null font");
		}
	}
	
	/**
	 * Indicate whether we want to try to use fullscreen multisampling. This will
	 * give antialiasing across the whole scene using a hardware feature.
	 * 
	 * @param samples The number of samples to attempt (2 is safe)
	 */
	public void setMultiSample(int samples) {
		this.samples = samples;
	}
	
	public boolean supportsMultiSample() {
		return supportsMultiSample;
	}
	
	/**
	 * The number of samples we're attempting to performing using
	 * hardware multisampling
	 *
	 * @return The number of samples requested
	 */
	public int getSamples() {
		return samples;
	}
	
	public void setForceExit(boolean forceExit) {
		this.forceExit = forceExit;
	}
	
	public void setSmoothDeltas(boolean smoothDeltas) {
		this.smoothDeltas = smoothDeltas;
	}
	
	public boolean isFullscreen() {
		return false;
	}
	
	public float getAspectRatio() {
		return getWidth() / getHeight();
	}
	
	public void setFullscreen(DisplayMode.Opt displayType) throws SlickException {
	}
	
	public static void enableSharedContext() throws SlickException {
		// todo this is def not right :?
		SHARED_DRAWABLE = new FBOGraphics(new Image());
	}
	
	public static FBOGraphics getSharedContext() {
		return SHARED_DRAWABLE;
	}
	
	public void setClearEachFrame(boolean clear) {
		this.clearEachFrame = clear;
	}
	
	public void reInit() throws SlickException {
	}
	
	public void pause() {
		setPaused(true);
	}
	
	public void resume() {
		setPaused(false);
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public boolean getAlwaysRender () {
		return alwaysRender;
	}

	public void setAlwaysRender (boolean alwaysRender) {
		this.alwaysRender = alwaysRender;
	}
	
	public static int getBuildVersion() {
		try {
			Properties props = new Properties();
			props.load(ResourceLoader.getResourceAsStream("version"));
			
			int build = Integer.parseInt(props.getProperty("build"));
			LOG.info("Slick Build #"+build);
			
			return build;
		} catch (Exception e) {
			LOG.error("Unable to determine Slick build number");
			return -1;
		}
	}
	
	public Font getDefaultFont() {
		return defaultFont;
	}
	
	public boolean isSoundOn() {
		return SoundStore.get().soundsOn();
	}

	public boolean isMusicOn() {
		return SoundStore.get().musicOn();
	}
	
	public void setMusicOn(boolean on) {
		SoundStore.get().setMusicOn(on);
	}

	public void setSoundOn(boolean on) {
		SoundStore.get().setSoundsOn(on);
	}
	
	public float getMusicVolume() {
		return SoundStore.get().getMusicVolume();
	}
	
	public float getSoundVolume() {
		return SoundStore.get().getSoundVolume();
	}
	
	public void setSoundVolume(float volume) {
		SoundStore.get().setSoundVolume(volume);
	}

	public void setMusicVolume(float volume) {
		SoundStore.get().setMusicVolume(volume);
	}
	
	public abstract int getScreenWidth();
	
	public abstract int getScreenHeight();
	
	/**
	 * Get the width of the game canvas
	 *
	 * @return The width of the game canvas
	 */
	public int getWidth() {
		return width;
	}

	public static int getStaticWidth() {
		return width;
	}
	
	/**
	 * Get the height of the game canvas
	 * 
	 * @return The height of the game canvas
	 */
	public int getHeight() {
		return height;
	}

	public static int getStaticHeight() {
		return height;
	}
	
	public abstract void setIcon(String ref) throws SlickException;
	
	/**
	 * Set the icons to be used for this application. Note that the size of the icon
	 * defines how it will be used. Important ones to note
	 *
	 * Windows window icon must be 16x16
	 * Windows alt-tab icon must be 24x24 or 32x32 depending on Windows version (XP=32)
	 *
	 * @param refs The reference to the icon to be displayed
	 * @throws SlickException Indicates a failure to load the icon
	 */
	public abstract void setIcons(String[] refs) throws SlickException;

	// TODO still dont know if i can really use system time here
	public long getTime() {
		return System.currentTimeMillis();
	}

	public void sleep(int milliseconds) {
		long target = getTime()+milliseconds;
		while (getTime() < target) {
			try { Thread.sleep(1); } catch (Exception e) {}
		}
	}
	
	public abstract void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException;

	public abstract void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException;

	public abstract void setMouseCursor(Image image, int hotSpotX, int hotSpotY) throws SlickException;
	
	public abstract void setMouseCursor(long cursor, int hotSpotX, int hotSpotY) throws SlickException;
	
	// FIXME this is broken until I figure a better solution
	public void setAnimatedMouseCursor(String ref, int x, int y, int width, int height, int[] cursorDelays) throws SlickException
	{
//		long cursor;
//		cursor = CursorLoader.get().getAnimatedCursor(ref, x, y, width, height, cursorDelays);
//		setMouseCursor(cursor, x, y);
	}
	
	public abstract void setDefaultMouseCursor();
	
	public Input getInput() {
		return input;
	}

	public int getFPS() {
		return recordedFPS;
	}

	public static void setMouseGrabbed(boolean grabbed) {
		GLFW.glfwSetInputMode(GAME_WINDOW, GLFW_CURSOR,  grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
	}

	public static boolean isMouseGrabbed() {
		return GLFW.glfwGetInputMode(GAME_WINDOW, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
	}

	public static boolean hasFocus() {
		return GLFW.glfwGetWindowAttrib(GAME_WINDOW, GLFW_FOCUSED) == GLFW_TRUE;
	}
	
	/**
	 * Retrieve the time taken to render the last frame, i.e. the change in time - delta.
	 * 
	 * @return The time taken to render the last frame
	 */
	protected int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		
		return delta;
	}
	
	protected void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			lastFPS = getTime();
			recordedFPS = fps;
			fps = 0;
		}
		fps++;
	}
	
	/**
	 * Set the minimum amount of time in milliseonds that has to
	 * pass before update() is called on the container game. This gives
	 * a way to limit logic updates compared to renders.
	 *
	 * @param interval The minimum interval between logic updates
	 */
	public void setMinimumLogicUpdateInterval(int interval) {
		minimumLogicInterval = interval;
	}

	/**
	 * Set the maximum amount of time in milliseconds that can passed
	 * into the update method. Useful for collision detection without
	 * sweeping.
	 * 
	 * @param interval The maximum interval between logic updates
	 */
	public void setMaximumLogicUpdateInterval(int interval) {
		maximumLogicInterval = interval;
	}
	
	protected void updateAndRender(int delta) throws SlickException {
		if (smoothDeltas) {
			if (getFPS() != 0) {
				delta = 1000 / getFPS();
			}
		}
		
		input.poll(width, height);
	
		Music.poll(delta);
		if (!paused) {
			storedDelta += delta;
			
			if (storedDelta >= minimumLogicInterval) {
				try {
					if (maximumLogicInterval != 0) {
						long cycles = storedDelta / maximumLogicInterval;
						for (int i=0;i<cycles;i++) {
							game.update(this, (int) maximumLogicInterval);
						}
						
						int remainder = (int) (storedDelta % maximumLogicInterval);
						if (remainder > minimumLogicInterval) {
							game.update(this, (int) (remainder % maximumLogicInterval));
							storedDelta = 0;
						} else {
							storedDelta = remainder;
						}
					} else {
						game.update(this, (int) storedDelta);
						storedDelta = 0;
					}
					
				} catch (Throwable e) {
					LOG.error(e);
					throw new SlickException("Game.update() failure - check the game code.");
				}
			}
		} else {
			game.update(this, 0);
		}
		
		if (hasFocus() || getAlwaysRender()) {
			if (clearEachFrame) {
				GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
			}

			GL.glLoadIdentity();

			graphics.resetTransform();
			graphics.resetFont();
			graphics.resetLineWidth();
			graphics.setAntiAlias(false);
			try {
				game.render(this, graphics);
			} catch (Throwable e) {
				LOG.error(e);
				throw new SlickException("Game.render() failure - check the game code.");
			}
			graphics.resetTransform();

			if (showFPS) {
				defaultFont.drawString(10, 10, "FPS: "+recordedFPS);
			}

			GL.flush();
		}

		// TODO this sync should really happen when I swap glBuffers
//		if (targetFPS != -1) {
//			Display.sync(targetFPS);
//		}
	}
	
	public void setUpdateOnlyWhenVisible(boolean updateOnlyWhenVisible) {
	}

	public boolean isUpdatingOnlyWhenVisible() {
		return true;
	}
	
	protected void initGL() {
		LOG.info("Starting display "+width+"x"+height);
		GL.initDisplay(width, height);
		
		if (input == null) {
			input = new Input(height);
		}
		input.init(height);
		// no need to remove listeners?
		//input.removeAllListeners();
		if (game instanceof InputListener) {
			input.removeListener((InputListener) game);
			input.addListener((InputListener) game);
		}

		if (graphics != null) {
			graphics.setDimensions(getWidth(), getHeight());
		}
		lastGame = game;
	}
	
	protected void initSystem() throws SlickException {
		initGL();
		setMusicVolume(1.0f);
		setSoundVolume(1.0f);
		
		graphics = new Graphics(width, height);
		defaultFont = graphics.getFont();
	}
	
	protected void enterOrtho() {
		enterOrtho(width, height);
	}
	
	public void setShowFPS(boolean show) {
		showFPS = show;
	}
	
	public boolean isShowingFPS() {
		return showFPS;
	}
	
	public void setTargetFrameRate(int fps) {
		targetFPS = fps;
	}
	
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
	}
	
	public boolean isVSyncRequested() {
		return vsync;
	}
	
	protected boolean running() {
		return running;
	}
	
	public void setVerbose(boolean verbose) {
		LOG.setVerbose(verbose);
	}
	
	public void exit() {
		running = false;
	}
	
	public Graphics getGraphics() {
		return graphics;
	}
	
	protected void enterOrtho(int xsize, int ysize) {
		GL.enterOrtho(xsize, ysize);
	}
}
