package org.newdawn.slick.tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;

public class URLImageTest extends BasicGame {
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new URLImageTest(), 800, 600, false).start();
	}
	
	public URLImageTest() { 
		super("TestClass");
	}
	
	Texture tex;
	int texWidth, texHeight;
	Image img;
	ByteBuffer buffer;

	public void init(GameContainer c) throws SlickException {
		
	}	
	
	public void render(GameContainer c, Graphics g) throws SlickException {
		if (img!=null)
			img.draw(50, 50);
		g.drawString("Press SPACE to load image from URL", 10, 25);
	}
	
	public void update(GameContainer c, int delta) throws SlickException {
		if (c.getInput().isKeyPressed(Input.KEY_SPACE)){

			try {
				String ref = "http://upload.wikimedia.org/wikipedia/commons/6/63/Wikipedia-logo.png";
				URL u = new URL(ref);
				InputStream is = u.openStream();
				
				//TODO: this is still not always working; maybe a InputStream issue?
				
				BufferedInputStream in = new BufferedInputStream(is);
				in.mark(is.available());
//				System.out.println(is.available());
				
//				is.markSupported();
//				System.out.println(is.markSupported(););
				if (img!=null)
					img.destroy();
				//System.out.println(is.available());
				tex = InternalTextureLoader.get().getTexture(is, ".png", false, GL11.GL_NEAREST);
				is.close();
				img = new Image(tex);
			} catch (Exception e) {
				if (img!=null)
					img.destroy();
				img = null;
				e.printStackTrace();
			}
			
		}
	}
}



