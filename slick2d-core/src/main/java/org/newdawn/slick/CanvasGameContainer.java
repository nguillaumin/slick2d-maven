package org.newdawn.slick;

import java.awt.Canvas;

import javax.swing.SwingUtilities;

import org.newdawn.slick.input.Input;
import org.newdawn.slick.util.Log;

/**
 * A game container that displays the game on an AWT Canvas.
 * 
 * @author kevin
 * @tyler
 */
public class CanvasGameContainer extends Canvas {
	private static final Log LOG = new Log(CanvasGameContainer.class);

	/** The actual container implementation */
	protected Container container;
	/** The game being held in this container */
	protected Game game;

	/**
	 * Create a new panel
	 * 
	 * @param game The game being held
	 * @throws SlickException Indicates a failure during creation of the container
	 */
	public CanvasGameContainer(Game game) throws SlickException {
		this(game, false);
	}
	
	/**
	 * Create a new panel
	 * 
	 * @param game The game being held
	 * @param shared True if shared GL context should be enabled. This allows multiple panels
	 * to share textures and other GL resources.
	 * @throws SlickException Indicates a failure during creation of the container
	 */
	public CanvasGameContainer(Game game, boolean shared) throws SlickException {
		super();

		this.game = game;
		setIgnoreRepaint(true);
		requestFocus();
		setSize(500,500);
		
		container = new Container(game, shared);
		container.setForceExit(false);
	}

	/**
	 * Start the game container rendering
	 * 
	 * @throws SlickException Indicates a failure during game execution
	 */
	public void start() {
		SwingUtilities.invokeLater(() -> {
			Input.disableControllers();
			container.setup();
			scheduleUpdate();
		});
	}
	
	/**
	 * Schedule an update on the EDT
	 */
	private void scheduleUpdate() {
		if (!isVisible()) {
			return;
		}
		
		SwingUtilities.invokeLater(() -> {
			container.gameLoop();
			container.checkDimensions();
			scheduleUpdate();
		});
	}
	/**
	 * Dispose the container and any resources it holds
	 */
	public void dispose() {
	}

	/**
	 * Get the GameContainer providing this canvas
	 * 
	 * @return The game container providing this canvas
	 */
	public GameContainer getContainer() {
		return container;
	}

	/**
	 * A game container to provide the canvas context
	 * 
	 * @author kevin
	 */
	private class Container extends AppGameContainer {
		/**
		 * Create a new container wrapped round the game
		 * 
		 * @param game
		 *            The game to be held in this container
		 * @param shared True if shared GL context should be enabled. This allows multiple panels
		 * to share textures and other GL resources.
		 * @throws SlickException Indicates a failure to initialise
		 */
		public Container(Game game, boolean shared) throws SlickException {
			super(game, CanvasGameContainer.this.getWidth(), CanvasGameContainer.this.getHeight(), DisplayMode.Opt.WINDOWED);

			width = CanvasGameContainer.this.getWidth();
			height = CanvasGameContainer.this.getHeight();
			
			if (shared) {
				enableSharedContext();
			}
		}

		/**
		 * Updated the FPS counter
		 */
		protected void updateFPS() {
			super.updateFPS();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#running()
		 */
		protected boolean running() {
			return super.running() && CanvasGameContainer.this.isDisplayable();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getHeight()
		 */
		public int getHeight() {
			return CanvasGameContainer.this.getHeight();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getWidth()
		 */
		public int getWidth() {
			return CanvasGameContainer.this.getWidth();
		}

		/**
		 * Check the dimensions of the canvas match the display
		 */
		public void checkDimensions() {
			if ((width != CanvasGameContainer.this.getWidth()) ||
			    (height != CanvasGameContainer.this.getHeight())) {
				
				try {
					setDisplayMode(CanvasGameContainer.this.getWidth(), 
								   CanvasGameContainer.this.getHeight(), DisplayMode.Opt.WINDOWED);
				} catch (SlickException e) {
					LOG.error(e);
				}
			}
		}
	}
}
