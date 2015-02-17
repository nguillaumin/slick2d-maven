package org.newdawn.slick.tests.shader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

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
 * A simple shader test that inverts the color of an image.
 * @author davedes
 */
public class ShaderTest extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new ShaderTest(), 800, 600, false).start();
	}
	
	public ShaderTest() {
		super("Simple Shader Test");
	}

	private Image logo;
	private ShaderProgram program;
	private String log;
	private boolean shaderWorks, useShader=true;
	private boolean supported = false;
	
	private float elapsed;
	
	private GameContainer container;
	
	@Override
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		logo = new Image("testdata/logo.png");
		container.getGraphics().setBackground(Color.darkGray);
		
		supported = ShaderProgram.isSupported();
		
		if (supported) {
			//we turn off "strict mode" which means the 'tex0' uniform
			//doesn't NEED to exist 
			ShaderProgram.setStrictMode(false);
			reload();
		}
	}
	

	
	private void reload() {
		if (!supported)
			return;
		
		//release the program and everything associated with it
		if (program!=null)
			program.release();
		
		try {
			program = ShaderProgram.loadProgram("testdata/shaders/invert.vert", "testdata/shaders/invert.frag");
			shaderWorks = true;
			
			//good idea to print/display the log anyways incase there are warnings..
			log = program.getLog();
			if (log!=null&&log.length()!=0)
				Log.warn(log);
			
			//set up our uniforms...
			program.bind();
			program.setUniform1i("tex0", 0); //texture 0
			program.unbind();
		} catch (Exception e) {
			log = e.getMessage();
			Log.error(log);
			shaderWorks = false;
		}
	}
	
	//@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		//bind the shader before rendering the image
		if (shaderWorks && useShader) 
			program.bind();
		
		g.drawImage(logo, 100, 300);
		
		//unbind the shader so that usual stuff renders OK
		if (shaderWorks && useShader)
			program.unbind();
		
		if (shaderWorks)
			g.drawString("Space to toggle shader\nPress R to reload shaders", 10, 25);
		else if (!supported)
			g.drawString("Your drivers do not support OpenGL Shaders, sorry!", 10, 25);
		else
			g.drawString("Oops, shader didn't load!", 10, 25);
		if (log!=null && log.length()!=0)
			g.drawString(log, 10, 75);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) 
			useShader = !useShader;
		if (container.getInput().isKeyPressed(Input.KEY_R)) 
			reload();
	}
}
