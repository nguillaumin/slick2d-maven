package org.newdawn.slick.tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;


/**
 * A test of mipmapping with Slick for smoother down-scaling.
 * 
 * @author davedes
 */
public class MipmapTest extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new MipmapTest(), 800, 600, false).start();
	}
	
	public MipmapTest() { 
		super("TestClass");
	}
	
	Image image;
	Image mippedImage;
	float scale = 1f;
	boolean supported = false;
	
	public void init(GameContainer c) throws SlickException {
		//if mipmap generation is unsupported, the filtering falls back to the given magFilter
		//we need either: GL 1.4 or higher, or GL_EXT_framebuffer_object
		supported = InternalTextureLoader.isGenerateMipmapSupported();
		
		//load the same image into two different by giving it a difference cache name
		String ref = "testdata/hiero.png";
		image = new Image(ref);
		mippedImage = supported ? createMipmapImage(ref) : image;
		
	}
	
	private Image createMipmapImage(String ref) throws SlickException {
		// this implementation is subject to change...
		try {
			InputStream in = ResourceLoader.getResourceAsStream(ref);
			LoadableImageData imageData = ImageDataFactory.getImageDataFor(ref);
			ByteBuffer buf = imageData.loadImage(new BufferedInputStream(in),
					false, null);
			ImageData.Format fmt = imageData.getFormat();
			int minFilter = GL11.GL_LINEAR_MIPMAP_LINEAR;
			int magFilter = GL11.GL_LINEAR;

			Texture tex = InternalTextureLoader.get().createTexture(
					imageData, // the image data holding width/height/format
					buf, // the buffer of data
					ref, // the ref for the TextureImpl
					GL11.GL_TEXTURE_2D, // what you will usually use
					minFilter, magFilter, // min and mag filters
					true, // generate mipmaps automatically
					fmt); // the internal format for the texture
			return new Image(tex);
		} catch (IOException e) {
			Log.error("error loading image", e);
			throw new SlickException("error loading image " + e.getMessage());
		}
	}
	
	public void render(GameContainer c, Graphics g) throws SlickException {
		if (!supported) {
			g.drawString("Your OpenGL version does not support automatic mipmap generation", 10, 25);
		} else {
			g.drawString("Left = no mipmapping, right = automatically generated mipmaps", 10, 25);
		}
		image.draw(10, 80, scale);
		mippedImage.draw(image.getWidth()*scale + 25, 80, scale);
	}
	
	public void update(GameContainer c, int delta) throws SlickException {
		Input in = c.getInput();
		if (in.isKeyDown(Input.KEY_UP))
			scale = Math.min(2f, scale+0.001f);
		else if (in.isKeyDown(Input.KEY_DOWN))
			scale = Math.max(0.01f, scale-0.001f);
	}
}
