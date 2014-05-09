package org.newdawn.slick.tests;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

public class ImageCopyAreaTest extends BasicGame {
	private Image logo, background;

	private Graphics renderGraphics;
	private Image renderImage;

	// in my real app, we have many of these...
	private Image copiedImage;
	
	private Image postCopy;

	/**
	 * Create a new image rendering test
	 */
	public ImageCopyAreaTest() {
		super("ImageCopyAreaTest");
	}

	public void init(GameContainer container) throws SlickException {
		logo = new Image("testdata/logo.png");
		
		background = new Image("testdata/sky.jpg");
		
		//We use this to more efficiently create an image for offscreen rendering via getGraphics
		renderImage = Image.createOffscreenImage(256, 356);
		renderGraphics = renderImage.getGraphics();
		renderGraphics.setColor(Color.pink);
		renderGraphics.fillRoundRect(0, 0, 256, 256, 15);
		renderGraphics.drawImage(logo, 0, 0);
		
		//Since copyArea doesn't use getGraphics (it does not require FBO/PBuffer) we should
		//just create the empty OpenGL image data like usual:
		copiedImage = new Image(256, 256);
		postCopy = new Image(256, 256);
		
		//now that we've drawn all that to the buffer, copy what we've got
		renderGraphics.copyArea(copiedImage, 50, 50, 0, 0, 50, 50);
		renderGraphics.copyArea(copiedImage, 0, 0, 50, 0, 50, 50);
		renderGraphics.flush();
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		background.draw(0, 0, container.getWidth(), container.getHeight());
		
		// Draw the one we rendered to, then the copy in a new image.
		g.drawImage(renderImage, 100, 172);
		g.drawImage(copiedImage, 444, 172);
		
		//copy some of our screen
		g.copyArea(postCopy, 100, 172);
		postCopy.draw(444, 350);
	}

	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(
					new ImageCopyAreaTest());
			container.setDisplayMode(800, 600, false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void update(GameContainer container, int delta)
			throws SlickException {
	}
}