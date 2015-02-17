package org.newdawn.slick.tests.shader;

import org.lwjgl.Sys;
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
 * Lesson 1 from the shader tutorial series on the wiki.
 * 
 * @author davedes
 */
public class ShaderLesson1 extends BasicGame {

	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new ShaderLesson1(), 800, 600, false).start();
	}
	
	public ShaderLesson1() {
		super("Shader Lesson 1");
	}

	private ShaderProgram program;
	private boolean shaderWorks = false;
	
	@Override
	public void init(GameContainer container) throws SlickException {
		// this test requires shaders
		if (!ShaderProgram.isSupported()) {
			// Sys is part of LWJGL -- it's a handy way to show an alert
			Sys.alert("Error", "Your graphics card doesn't support OpenGL shaders.");
			container.exit();
			return;
		}
	
		// load our shader program
		try {
			// load our vertex and fragment shaders
			final String VERT = "testdata/shaders/pass.vert";
			final String FRAG = "testdata/shaders/lesson1.frag";
			program = ShaderProgram.loadProgram(VERT, FRAG);
			shaderWorks = true;
		} catch (SlickException e) {
			// there was a problem compiling our source! show the log
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		//start using our program
		if (shaderWorks)
			program.bind();
		
		//render our shapes with the shader enabled
		g.fillRect(220, 200, 50, 50);
		
		//stop using our program
		if (shaderWorks)
			program.unbind();
		
		String txt = shaderWorks ? "Shader works!" : "Shader did not compile, check log";
		g.drawString(txt, 10, 25);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
	}
}
