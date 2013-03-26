package org.newdawn.slick.examples.scroller;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;

/**
 * An example to show scrolling around a tilemap smoothly. This seems to have caused confusion
 * a couple of times so here's "a" way to do it.
 *
 * @author kevin
 */
public class Scroller extends BasicGame {
	/** The size of the tank sprite - used for finding the centre */
	private static final int TANK_SIZE = 32;
	/** The size of the tiles - used to determine the amount to draw */
	private static final int TILE_SIZE = 32;
	/** The speed the tank moves at */
	private static final float TANK_MOVE_SPEED = 0.003f;
	/** The speed the tank rotates at */
	private static final float TANK_ROTATE_SPEED = 0.2f;
	
	/** The player's x position in tiles */
	private float playerX = 15;
	/** The player's y position in tiles */
	private float playerY = 16;
	
	/** The width of the display in tiles */
	private int widthInTiles;
	/** The height of the display in tiles */
	private int heightInTiles;
	
	/** The offset from the centre of the screen to the top edge in tiles */
	private int topOffsetInTiles;
	/** The offset from the centre of the screen to the left edge in tiles */
	private int leftOffsetInTiles;
	
	/** The map that we're going to drive around */
	private TiledMap map;
	
	/** The animation representing the player's tank */
	private Animation player;
	
	/** The angle the player is facing */
	private float ang;
	/** The x component of the movement vector */
	private float dirX;
	/** The y component of themovement vector */
	private float dirY;
	
	/** The collision map indicating which tiles block movement - generated based on tile properties */
	private boolean[][] blocked;
	
	/**
	 * Scroller example
	 */
	public Scroller() {
		super("Scroller");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		// load the sprites and tiles, note that underneath the texture
		// will be shared between the sprite sheet and tilemap
		SpriteSheet sheet = new SpriteSheet("testdata/scroller/sprites.png",32,32);
		// load the tilemap created the TileD tool 
		map = new TiledMap("testdata/scroller/map.tmx");
		
		// build a collision map based on tile properties in the TileD map
		blocked = new boolean[map.getWidth()][map.getHeight()];
		for (int x=0;x<map.getWidth();x++) {
			for (int y=0;y<map.getHeight();y++) {
				int tileID = map.getTileId(x, y, 0);
				String value = map.getTileProperty(tileID, "blocked", "false");
				if ("true".equals(value)) {
					blocked[x][y] = true;
				}
			}
		}
		
		// caculate some layout values for rendering the tilemap. How many tiles
		// do we need to render to fill the screen in each dimension and how far is
		// it from the centre of the screen
		widthInTiles = container.getWidth() / TILE_SIZE;
		heightInTiles = container.getHeight() / TILE_SIZE;
		topOffsetInTiles = heightInTiles / 2;
		leftOffsetInTiles = widthInTiles / 2;
		
		// create the player sprite based on a set of sprites from the sheet loaded
		// above (tank tracks moving)
		player = new Animation();
		for (int frame=0;frame<7;frame++) {
			player.addFrame(sheet.getSprite(frame,1), 150);
		}
		player.setAutoUpdate(false);

		// update the vector of movement based on the initial angle
		updateMovementVector();
		
		Log.info("Window Dimensions in Tiles: "+widthInTiles+"x"+heightInTiles);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		// check the controls, left/right adjust the rotation of the tank, up/down 
		// move backwards and forwards
		if (container.getInput().isKeyDown(Input.KEY_LEFT)) {
			ang -= delta * TANK_ROTATE_SPEED;
			updateMovementVector();
		}
		if (container.getInput().isKeyDown(Input.KEY_RIGHT)) {
			ang += delta * TANK_ROTATE_SPEED;
			updateMovementVector();
		}
		if (container.getInput().isKeyDown(Input.KEY_UP)) {
			if (tryMove(dirX * delta * TANK_MOVE_SPEED, dirY * delta * TANK_MOVE_SPEED)) {
				// if we managed to move update the animation
				player.update(delta);
			}
		}
		if (container.getInput().isKeyDown(Input.KEY_DOWN)) {
			if (tryMove(-dirX * delta * TANK_MOVE_SPEED, -dirY * delta * TANK_MOVE_SPEED)) {
				// if we managed to move update the animation
				player.update(delta);
			}
		}
	}

	/**
	 * Check if a specific location of the tank would leave it 
	 * on a blocked tile
	 * 
	 * @param x The x coordinate of the tank's location
	 * @param y The y coordinate of the tank's location
	 * @return True if the location is blocked
	 */
	private boolean blocked(float x, float y) {
		return blocked[(int) x][(int) y];
	}
	
	/**
	 * Try to move in the direction specified. If it's blocked, try sliding. If that
	 * doesn't work just don't bother
	 * 
	 * @param x The amount on the X axis to move
	 * @param y The amount on the Y axis to move
	 * @return True if we managed to move
	 */
	private boolean tryMove(float x, float y) {
		float newx = playerX + x;
		float newy = playerY + y;
		
		// first we try the real move, if that doesn't work
		// we try moving on just one of the axis (X and then Y) 
		// this allows us to slide against edges
		if (blocked(newx,newy)) {
			if (blocked(newx, playerY)) {
				if (blocked(playerX, newy)) {
					// can't move at all!
					return false;
				} else {
					playerY = newy;
					return true;
				}
			} else {
				playerX = newx;
				return true;
			}
		} else {
			playerX = newx;
			playerY = newy;
			return true;
		}
	}
	
	/**
	 * Update the direction that will be moved in based on the
	 * current angle of rotation
	 */
	private void updateMovementVector() {
		dirX = (float) Math.sin(Math.toRadians(ang));
		dirY = (float) -Math.cos(Math.toRadians(ang));
	}
	
	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		// draw the appropriate section of the tilemap based on the centre (hence the -(TANK_SIZE/2)) of
		// the player
		int playerTileX = (int) playerX;
		int playerTileY = (int) playerY;
		
		// caculate the offset of the player from the edge of the tile. As the player moves around this
		// varies and this tells us how far to offset the tile based rendering to give the smooth
		// motion of scrolling
		int playerTileOffsetX = (int) ((playerTileX - playerX) * TILE_SIZE);
		int playerTileOffsetY = (int) ((playerTileY - playerY) * TILE_SIZE);
		
		// render the section of the map that should be visible. Notice the -1 and +3 which renders
		// a little extra map around the edge of the screen to cope with tiles scrolling on and off
		// the screen
		map.render(playerTileOffsetX - (TANK_SIZE / 2), playerTileOffsetY - (TANK_SIZE / 2), 
				   playerTileX - leftOffsetInTiles - 1, 
				   playerTileY - topOffsetInTiles - 1,
				   widthInTiles + 3, heightInTiles + 3);
		
		// draw entities relative to the player that must appear in the centre of the screen
		g.translate(400 - (int) (playerX * 32), 300 - (int) (playerY * 32));
		
		drawTank(g, playerX, playerY, ang);
		// draw other entities here if there were any
		
		g.resetTransform();
	}

	/**
	 * Draw a single tank to the game
	 *  
	 * @param g The graphics context on which we're drawing
	 * @param xpos The x coordinate in tiles the tank is at
	 * @param ypos The y coordinate in tiles the tank is at
	 * @param rot The rotation of the tank
	 */
	public void drawTank(Graphics g, float xpos, float ypos, float rot) {
		// work out the centre of the tank in rendering coordinates and then
		// spit onto the screen
		int cx = (int) (xpos * 32);
		int cy = (int) (ypos * 32);
		g.rotate(cx,cy,rot);
		player.draw(cx-16,cy-16);
		g.rotate(cx,cy,-rot);
	}
	
	/**
	 * Entry point to the scroller example
	 * 
	 * @param argv The argument passed on the command line (if any)
	 */
	public static void main(String[] argv) {
		try {
			// create a new container for our example game. This container
			// just creates a normal native window for rendering OpenGL accelerated
			// elements to
			AppGameContainer container = new AppGameContainer(new Scroller(), 800, 600, false);
			container.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
