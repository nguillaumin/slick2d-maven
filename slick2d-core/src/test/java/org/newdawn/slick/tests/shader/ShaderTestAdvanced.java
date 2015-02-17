package org.newdawn.slick.tests.shader;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;
import org.newdawn.slick.util.Log;

/**
 * A simple shader test that blurs an image.
 * @author davedes
 */
public class ShaderTestAdvanced extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new ShaderTestAdvanced(), 800, 600, false).start();
	}
	
	public ShaderTestAdvanced() {
		super("Advanced Shader Test");
	}

	private Image logo;
	private ShaderProgram blurHoriz, blurVert;
	private String log;
	private boolean shaderWorks, useBlur=true;
	private boolean supported = false;
	
	private float rot, radius=1.2f;
	
	private GameContainer container;
	private Image postImageA, postImageB;
	private Graphics postGraphicsA, postGraphicsB;
	
	
	
	
	
	@Override
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		logo = new Image("testdata/logo.png");
		container.setClearEachFrame(false);
		
		supported = ShaderProgram.isSupported();
		
		if (supported) {
			try {
				//first we will try to create our offscreen image
				//this may fail in very very rare cases if the user has no FBO/PBuffer support
				postImageA = Image.createOffscreenImage(container.getWidth(), container.getHeight());
				postGraphicsA = postImageA.getGraphics();
				postImageB = Image.createOffscreenImage(container.getWidth(), container.getHeight());
				postGraphicsB = postImageB.getGraphics();
				
				String h = "testdata/shaders/hblur.frag";
				String v = "testdata/shaders/vblur.frag";
				String vert = "testdata/shaders/blur.vert";
				
				blurHoriz = ShaderProgram.loadProgram(vert, h);
				blurVert = ShaderProgram.loadProgram(vert, v);
				shaderWorks = true;
				
				//good idea to print/display the log anyways incase there are warnings..
				log = blurHoriz.getLog()+"\n"+blurVert.getLog();
				
				//note that strict mode is ENABLED so these uniforms must be active in shader
				
				//set up our uniforms for horizontal blur...
				blurHoriz.bind();
				blurHoriz.setUniform1i("tex0", 0); //texture 0
				blurHoriz.setUniform1f("resolution", container.getWidth()); //width of img
				blurHoriz.setUniform1f("radius", radius);
				
				//set up our uniforms for vertical blur... 
				blurVert.bind();
				blurVert.setUniform1i("tex0", 0); //texture 0
				blurVert.setUniform1f("resolution", container.getHeight()); //height of img
				blurVert.setUniform1f("radius", radius);
				
				ShaderProgram.unbindAll();
			} catch (SlickException e) {
				log = e.getMessage();
				Log.error(log);
				shaderWorks = false;
			}		
		}
	}
	
	public void renderScene(GameContainer container, Graphics g) throws SlickException {
		logo.setRotation(0f);
		g.drawImage(logo, 100, 300);
		logo.setRotation(rot);
		g.drawImage(logo, 400, 200);
		
		g.setColor(Color.white);
		g.fillRect(450, 350, 100, 100);
	}
	
	@Override
	public void render(GameContainer container, Graphics screenGraphics) throws SlickException {
		screenGraphics.clear();
		
		//for sake of example, only bother rendering if shader works
		if (shaderWorks && useBlur) {
			//1. first we render our scene without blur into an off-screen buffer
			
			// this is just to be safe when using multiple contexts
			Graphics.setCurrent(postGraphicsA);
			
			postGraphicsA.clear();
			
			// this is where we'd draw sprites, entities, etc.
			renderScene(container, postGraphicsA);
			
			// flush it after drawing
			postGraphicsA.flush();
			
			//if we were using a single pass (i.e. only horizontal blur) then we could render
			//directly into the screen at this point. but since we are using two passes, we first need to
			//sample from the normal texture, then blur it horizontally onto to another texture, then sample 
			//from the blurred texture as we blur it again (vertically) onto the screen.
			
			//2. enable our first shader, the horizontal blur
			blurHoriz.bind();
			blurHoriz.setUniform1f("radius", radius);

			//3. sample from A, render to B
			Graphics.setCurrent(postGraphicsB);
			postGraphicsB.clear();
			postGraphicsB.fillRect(0, 0, 800, 600);
			postGraphicsB.drawImage(postImageA, 0f, 0f);
			postGraphicsB.flush();
			
			blurHoriz.unbind();
			
			//4. enable our second shader, the vertical blur
			blurVert.bind();
			blurVert.setUniform1f("radius", radius);
			
			//5. sample from B, render to screen
			Graphics.setCurrent(screenGraphics);
			screenGraphics.drawImage(postImageB, 0, 0);
			//flushing the screen graphics doesn't do anything, so it's unnecessary
			//screenGraphics.flush();

			//stop using shaders
			ShaderProgram.unbindAll();
		} else {
			//simply render the scene to the screen
			renderScene(container, screenGraphics);
		}
		

		screenGraphics.setColor(Color.white);
		
		//now we can render on top of post processing (on screen)
		if (shaderWorks)
			screenGraphics.drawString("B to toggle blur" + (useBlur?" (enabled)":"") +
					"\nUP/DOWN to change radius: "+radius, 10, 25);
		else if (!supported)
			screenGraphics.drawString("Your drivers do not support OpenGL Shaders, sorry!", 10, 25);
		else
			screenGraphics.drawString("Oops, shader didn't load!", 10, 25);
		if (log!=null && log.trim().length()!=0)
			screenGraphics.drawString("Shader Log:\n"+log, 10, 75);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_B)) 
			useBlur = !useBlur;
		if (container.getInput().isKeyDown(Input.KEY_DOWN)) {
			radius = Math.max(0.0f, radius-0.0003f*delta);
		} else if (container.getInput().isKeyDown(Input.KEY_UP)) {
			radius = Math.min(5f, radius+0.0003f*delta);
		}
		rot += 0.03f*delta;
	}
}
