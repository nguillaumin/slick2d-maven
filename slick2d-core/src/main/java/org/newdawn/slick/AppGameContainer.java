package org.newdawn.slick;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.TGAImageData;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import javax.imageio.ImageIO;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * A game container that will display the game as an stand alone 
 * application.
 *
 * @author kevin
 */
public class AppGameContainer extends GameContainer {
	private static final Log LOG = new Log(AppGameContainer.class);

	private static final long DEFAULT_SHARE = 0L;

	/** True if we should update the game only when the display is visible */
	protected boolean updateOnlyOnVisible = true;
	/** Alpha background supported */
	protected boolean alphaSupport = false;

	public AppGameContainer(Game game) throws SlickException {
		this(game,640,480, DisplayMode.Opt.WINDOWED);
	}

	public AppGameContainer(Game game, int width, int height, DisplayMode.Opt displayType) throws SlickException {
		super(game);

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		makeNewWindow(width,height, displayType);

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(GAME_WINDOW, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(GAME_WINDOW, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					GAME_WINDOW,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(GAME_WINDOW);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(GAME_WINDOW);

	}
	
	public boolean supportsAlphaInBackBuffer() {
		return alphaSupport;
	}
	
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(GAME_WINDOW, title);
	}

	private void makeNewWindow(int width, int height, DisplayMode.Opt displayOption) {
		if ((this.width == width) && (this.height == height) && displayOption == DisplayMode.getDisplayType()) {
			return;
		}

		long newWindow = GLFW.glfwCreateWindow(
				width,
				height,
				game.getTitle(),
				displayOption.toLong(),
				DEFAULT_SHARE // TODO What does share do?
		);

		if (GAME_WINDOW != -1L) {
			GLFW.glfwDestroyWindow(GAME_WINDOW);
		}

		GAME_WINDOW = newWindow;

		this.width = width;
		this.height = height;
		DisplayMode.setDisplayMode(width, height, displayOption);
	}

	public void setDisplayMode(int width, int height, DisplayMode.Opt displayOption) throws SlickException {
		makeNewWindow(width, height, displayOption);

		initGL();
		enterOrtho();

		// TODO what does this do?
		//		if (targetDisplayMode.getBitsPerPixel() == 16) {
		//			InternalTextureLoader.get().set16BitMode();
		//		}

		getDelta();

		// TODO did I miss anything?
		//		try {
		//			if (displayOption == DisplayMode.Opt.FULLSCREEN) {
		//				int freq = 0;
		//
		//				for (int i=0;i<modes.length;i++) {
		//					DisplayMode current = modes[i];
		//
		//					if ((current.getWidth() == width) && (current.getHeight() == height)) {
		//						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
		//							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
		//								targetDisplayMode = current;
		//								freq = targetDisplayMode.getFrequency();
		//							}
		//						}
		//
		//						// if we've found a match for bpp and frequence against the
		//						// original display mode then it's probably best to go for this one
		//						// since it's most likely compatible with the monitor
		//						if ((current.getBitsPerPixel() == originalDisplayMode.getBitsPerPixel()) &&
		//						    (current.getFrequency() == originalDisplayMode.getFrequency())) {
		//							targetDisplayMode = current;
		//							break;
		//						}
		//					}
		//				}
		//			} else {
		//				targetDisplayMode = new DisplayMode(width,height);
		//			}
		//
		//			if (targetDisplayMode == null) {
		//				throw new SlickException("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
		//			}
		//
		//			this.width = width;
		//			this.height = height;
		//
		//			Display.setDisplayMode(targetDisplayMode);
		//			Display.setFullscreen(fullscreen);
		//
		//			if (Display.isCreated()) {
		//				initGL();
		//				enterOrtho();
		//			}
		//
		//			if (targetDisplayMode.getBitsPerPixel() == 16) {
		//				InternalTextureLoader.get().set16BitMode();
		//			}
		//		} catch (LWJGLException e) {
		//			throw new SlickException("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen, e);
		//		}
		//
		//		getDelta();
	}

	public boolean isFullscreen() {
		return DisplayMode.getDisplayType() == DisplayMode.Opt.FULLSCREEN ;
	}

	public void setFullscreen(DisplayMode.Opt displayType) throws SlickException {
		if (DisplayMode.getDisplayType() == displayType) {
			return;
		}
		
		setDisplayMode(width, height, displayType);

		getDelta();
	}

	public void setMouseCursor(String ref, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			InputStream stream = new FileInputStream(ref);
			BufferedImage image = ImageIO.read(stream);

			int width = image.getWidth();
			int height = image.getHeight();

			int[] pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);

			// convert image to RGBA format
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					int pixel = pixels[y * width + x];

					buffer.put((byte) ((pixel >> 16) & 0xFF));  // red
					buffer.put((byte) ((pixel >> 8) & 0xFF));   // green
					buffer.put((byte) (pixel & 0xFF));          // blue
					buffer.put((byte) ((pixel >> 24) & 0xFF));  // alpha
				}
			}
			buffer.flip(); // this will flip the cursor image vertically

			// create a GLFWImage
			GLFWImage cursorImg= GLFWImage.create();
			cursorImg.width(width);     // set up image width
			cursorImg.height(height);   // set up image height
			cursorImg.pixels(buffer);   // pass image data

			long cursor = GLFW.glfwCreateCursor(cursorImg, hotSpotX, hotSpotY);
			GLFW.glfwSetCursor(GAME_WINDOW, cursor);
		} catch (Throwable e) {
			LOG.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
		}
	}

	public void setMouseCursor(ImageData data, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			// create a GLFWImage
			GLFWImage cursorImg= GLFWImage.create();
			cursorImg.width(data.getWidth());     // set up image width
			cursorImg.height(data.getHeight());   // set up image height
			cursorImg.pixels(data.getImageBufferData());   // pass image data

			long cursor = GLFW.glfwCreateCursor(cursorImg, hotSpotX, hotSpotY);
			GLFW.glfwSetCursor(GAME_WINDOW, cursor);
		} catch (Throwable e) {
			LOG.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
		}
	}

	public void setMouseCursor(long cursor, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			GLFW.glfwSetCursor(GAME_WINDOW, cursor);
		} catch (Throwable e) {
			LOG.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
		}
	}

	private int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	public void setMouseCursor(Image image, int hotSpotX, int hotSpotY) throws SlickException {
		try {
			Image temp = new Image(get2Fold(image.getWidth()), get2Fold(image.getHeight()));
			Graphics g = temp.getGraphics();
			
			ByteBuffer buffer = BufferUtils.createByteBuffer(temp.getWidth() * temp.getHeight() * 4);
			g.drawImage(image.getFlippedCopy(false, true), 0, 0);
			g.flush();
			g.getArea(0,0,temp.getWidth(),temp.getHeight(),buffer);

			// create a GLFWImage
			GLFWImage cursorImg= GLFWImage.create();
			cursorImg.width(temp.getWidth());     // set up image width
			cursorImg.height(temp.getHeight());   // set up image height
			cursorImg.pixels(buffer);   // pass image data

			long cursor = GLFW.glfwCreateCursor(cursorImg, hotSpotX, hotSpotY);
			GLFW.glfwSetCursor(GAME_WINDOW, cursor);
		} catch (Throwable e) {
			LOG.error("Failed to load and apply cursor.", e);
			throw new SlickException("Failed to set mouse cursor", e);
		}
	}

	public void reInit() throws SlickException {
		InternalTextureLoader.get().clear();
		SoundStore.get().clear();
		initSystem();
		enterOrtho();
		
		try {
			game.init(this);
		} catch (SlickException e) {
			LOG.error(e);
			running = false;
		}
	}

	public void start() throws SlickException {
		try {
			setup();
			
			getDelta();
			while (running()) {
				gameLoop();
			}
		} finally {
			destroy();
		}
		
		if (forceExit) {
			System.exit(0);
		}
	}
	
	protected void setup() throws SlickException {
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		// This _should_ unlock fps cap
		glfwSwapInterval(0);

		LOG.info("GLFW Version: " + GLFW.glfwGetVersionString());
		LOG.info("LWJGL Version: " + Version.getVersion());
		LOG.info("DisplayMode: " + DisplayMode.getDisplayType()
				+ "; width: " + DisplayMode.getWidth()
				+ "; height: " + DisplayMode.getHeight());

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		org.lwjgl.opengl.GL.createCapabilities();

		initGL();
		getDelta();
		initSystem();
		enterOrtho();

		// FIXME controllers are disabled
//		try {
//			getInput().initControllers();
//		} catch (SlickException e) {
//			LOG.info("Controllers not available");
//		} catch (Throwable e) {
//			LOG.info("Controllers not available");
//		}
		
		try {
			game.init(this);
		} catch (SlickException e) {
			LOG.error(e);
			running = false;
		}
	}

	private boolean isVisible() {
		return GLFW_TRUE == glfwGetWindowAttrib(GAME_WINDOW, GLFW_VISIBLE);
	}

	protected void gameLoop() {
		int delta = getDelta();
		if (!isVisible() && updateOnlyOnVisible) {
			try { Thread.sleep(100); } catch (Exception e) {}
		} else {
			try {
				updateAndRender(delta);
			} catch (SlickException e) {
				LOG.error(e);
				running = false;
				return;
			}
		}

		updateFPS();

		glfwPollEvents();
		glfwSwapBuffers(GAME_WINDOW);

		if (GLFW.glfwWindowShouldClose(GAME_WINDOW)) {
			if (game.closeRequested()) {
				running = false;
			}
		}
	}
	
	public void setUpdateOnlyWhenVisible(boolean updateOnlyWhenVisible) {
		updateOnlyOnVisible = updateOnlyWhenVisible;
	}
	
	public boolean isUpdatingOnlyWhenVisible() {
		return updateOnlyOnVisible;
	}
	
	public void setIcon(String ref) throws SlickException {
		setIcons(new String[] {ref});
	}

	public int getScreenHeight() {
		return getHeight();
	}

	public int getScreenWidth() {
		return getWidth();
	}
	
	public void destroy() {
		GLFW.glfwDestroyWindow(GAME_WINDOW);

		// TODO how do I destroy audio?
		//AL.destroy();
	}

	public void setIcons(String[] refs) throws SlickException {
		ByteBuffer[] bufs = new ByteBuffer[refs.length];
		for (int i=0;i<refs.length;i++) {
			LoadableImageData data;
			boolean flip = true;
			
			if (refs[i].endsWith(".tga")) {
				data = new TGAImageData();
			} else {
				flip = false;
				data = new ImageIOImageData();
			}
			
			try {
				bufs[i] = data.loadImage(ResourceLoader.getResourceAsStream(refs[i]), flip, false, null);
			} catch (Exception e) {
				LOG.error(e);
				throw new SlickException("Failed to set the icon");
			}
		}

		GLFW.glfwSetWindowIcon(GAME_WINDOW, new GLFWImage.Buffer(bufs[0]));
	}

	public void setDefaultMouseCursor() {
		GLFW.glfwSetCursor(GAME_WINDOW, GLFW_ARROW_CURSOR);
	}
}
