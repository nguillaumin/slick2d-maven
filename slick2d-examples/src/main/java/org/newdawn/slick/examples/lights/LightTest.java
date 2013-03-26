package org.newdawn.slick.examples.lights;

import java.util.ArrayList;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Bootstrap;

/**
 * This example shows using vertex colours on a tile map to producing a lighting
 * effect. The approach is very efficient and pretty flexible. It can be extended 
 * in many ways. 
 * 
 * The down side is that the resolution of your lighting map is the same as that of your
 * tile map. So the small area you'll see a change in lighting across is 32x32 pixels in
 * this case (the size of a single tile). This can be worked round by rendering black tiles
 * over an existing map at a different resolution. For instance, you may use 16x16 black tiles
 * over a 32x32 pixels tiled map to get a better resolution of lighting.
 * 
 * This example essentially generates a random map of tiles, creates a set of lights and calculates
 * their effect on each of vertexs in the tiled map. When rendering we apply those calculated values
 * to the tile image vertex colours before rendering it. This gives us the effect of lighting
 * the tiles.
 * 
 * @author kevin
 */
public class LightTest extends BasicGame {
	/** The width of the tile map in tiles */
	private static final int WIDTH = 15;
	/** The height of the tile map in tiles */
	private static final int HEIGHT = 15;

	/** True if we're going to render with lighting */
	private boolean lightingOn = true;
	/** True if we're going to render coloured lighting .... oooooh disco inferno! */
	private boolean colouredLights = false;
	
	/** The sprite sheet we're using for our tiles */
	private SpriteSheet tiles;
	/** The tile map we'll randomly generate and render */
	private int[][] map = new int[WIDTH][HEIGHT];
	/** 
	 * The values calculated for each vertex of the tile map, 
	 * note how it's one more to account for the bottom corner of the map. 
	 * The 3 dimension is for colour components (red, green, blue) used for coloured lighting
	 */
	private float[][][] lightValue = new float[WIDTH+1][HEIGHT+1][3];
	/** The lights we've defined */
	private ArrayList lights = new ArrayList();
	/** The main light that we'll move around with the mouse, held seperately so we can update it */
	private Light mainLight;
	
	/**
	 * Create the example game
	 */
	public LightTest() {
		super("Light Test");
	}

	/**
	 * Initialise our resources for the example
	 * 
	 * @param container The game container the game is running in
	 */
	public void init(GameContainer container) throws SlickException {
		tiles = new SpriteSheet("testdata/tiles.png", 32,32);
		generateMap();
	}

	/**
	 * Randomly generate a tile map
	 */
	private void generateMap() {
		// cycle through the map placing a random tile in each location
		for (int y=0;y<HEIGHT;y++) {
			for (int x=0;x<WIDTH;x++) {
				map[x][y] = 0;
				
				// 20% of tiles have features
				if (Math.random() > 0.8) {
					map[x][y] = 1 + (int) (Math.random() * 7);
				}
			}
		}
		
		// create and add our lights
		lights.clear();
		
		mainLight = new Light(8f,7f,4f,Color.white);
		lights.add(mainLight);
		lights.add(new Light(2,2,2f,Color.red));
		lights.add(new Light(2,11,1.5f,Color.yellow));
		lights.add(new Light(12,2,3f,Color.green));
		
		// finally update the lighting map for the first time
		updateLightMap();
	}
	
	/**
	 * Update the vertex values for lighting based on the current
	 * light configuration.
	 */
	private void updateLightMap() {
		// for every vertex on the map (notice the +1 again accounting for the trailing vertex)
		for (int y=0;y<HEIGHT+1;y++) {
			for (int x=0;x<WIDTH+1;x++) {
				// first reset the lighting value for each component (red, green, blue)
				for (int component=0;component<3;component++) {
					lightValue[x][y][component] = 0;
				}
				
				// next cycle through all the lights. Ask each light how much effect
				// it'll have on the current vertex. Combine this value with the currently
				// existing value for the vertex. This lets us blend coloured lighting and 
				// brightness
				for (int i=0;i<lights.size();i++) {
					float[] effect = ((Light) lights.get(i)).getEffectAt(x, y, colouredLights);
					for (int component=0;component<3;component++) {
						lightValue[x][y][component] += effect[component];
					}
				}
				
				// finally clamp the components to 1, since we don't want to 
				// blow up over the colour values
				for (int component=0;component<3;component++) {
					if (lightValue[x][y][component] > 1) {
						lightValue[x][y][component] = 1;
					}
				}
			}
		}
	}
	
	/**
	 * Update the game
	 * 
	 * @param container The container the game is running in
	 * @param delta The amount of time that passed since last update (in seconds)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
		// toggle the lighting on/off
		if (container.getInput().isKeyPressed(Input.KEY_L)){
			lightingOn = !lightingOn;
		}
		// toggle the use of coloured lighting on/off
		if (container.getInput().isKeyPressed(Input.KEY_C)){
			colouredLights = !colouredLights;
			// we need to recaculate the lighting values because
			// colours may now be involved
			updateLightMap();
		}
	}

	/**
	 * Notification that the mouse was dragged
	 * 
	 * @param oldx The old x coordinate of the mouse
	 * @param oldy The old y coordinate of the mouse
	 * @param newx The new x coordinate of the mouse
	 * @param newy The new y coordinate of the mouse
	 */
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		mousePressed(0, newx, newy);
	}

	/**
	 * Notification that mouse was pressed
	 * 
	 * @param button The button that was pressed
	 * @param x The x coordinate the mouse was pressed at
	 * @param y The y coordinate the mouse was pressed at
	 */
	public void mousePressed(int button, int x, int y) {
		mainLight.setLocation((x-64)/32.0f,(y-50)/32.0f);
		updateLightMap();
	}

	/**
	 * Render the tile map and lighting to the game window
	 * 
	 * @param container The container the game is running in
	 * @param g The graphics context to which we can render
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		// display some instructions on how to use the example
		g.setColor(Color.white);
		g.drawString("Lighting Example", 440, 5);
		g.drawString("Press L to toggle light", 80, 560);
		g.drawString("Press C to toggle coloured lights", 80, 575);
		g.drawString("Click or Drag to move the main light", 80, 545);
		
		// move the display to nicely position the tilemap
		g.translate(64,50);
		
		tiles.startUse();
		// cycle round every tile in the map
		for (int y=0;y<HEIGHT;y++) {
			for (int x=0;x<WIDTH;x++) {
				// get the appropriate image to draw for the current tile
				int tile = map[x][y];
				Image image = tiles.getSubImage(tile % 4, tile / 4);
				
				if (lightingOn) {
					// if lighting is on apply the lighting values we've 
					// calculated for each vertex to the image. We can apply
					// colour components here as well as just a single value.
					image.setColor(Image.TOP_LEFT, lightValue[x][y][0], lightValue[x][y][1], lightValue[x][y][2], 1);
					image.setColor(Image.TOP_RIGHT, lightValue[x+1][y][0], lightValue[x+1][y][1], lightValue[x+1][y][2], 1);
					image.setColor(Image.BOTTOM_RIGHT, lightValue[x+1][y+1][0], lightValue[x+1][y+1][1], lightValue[x+1][y+1][2], 1);
					image.setColor(Image.BOTTOM_LEFT, lightValue[x][y+1][0], lightValue[x][y+1][1], lightValue[x][y+1][2], 1);
				} else {
					// if lighting is turned off then use "1" for every value
					// so we just have full colour everywhere.
					float light = 1;
					image.setColor(Image.TOP_LEFT, light, light, light, 1);
					image.setColor(Image.TOP_RIGHT, light, light, light, 1);
					image.setColor(Image.BOTTOM_RIGHT, light, light, light, 1);
					image.setColor(Image.BOTTOM_LEFT, light, light, light, 1);
				}
							
				// draw the image with it's newly declared vertex colours
				// to the display
				image.drawEmbedded(x*32,y*32,32,32);
			}
		}
		tiles.endUse();
	}
	
	/**
	 * Entry point to the example game
	 * 
	 * @param argv The arguments provided at the command line
	 */
	public static void main(String[] argv) {
		Bootstrap.runAsApplication(new LightTest(), 600, 600, false);
	}
}

