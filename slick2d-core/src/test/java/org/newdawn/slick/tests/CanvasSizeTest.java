package org.newdawn.slick.tests;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Quick test to confirm canvas size is reported correctly
 * 
 * @author kevin
 */
public class CanvasSizeTest extends BasicGame {
	
	/**
	 * Create test
	 */
	public CanvasSizeTest() {
		super("Test");
	}
	
	private Image image;

	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		System.out.println(container.getWidth() + ", " + container.getHeight());
		container.getGraphics().setBackground(Color.gray);
		
		image = new Image("testdata/logo.png");
	}

	/**
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		image.draw(container.getWidth()/2f-image.getWidth()/2f, container.getHeight()/2f-image.getHeight()/2f);
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	/**
	 * Entry point to the test
	 * 
	 * @param args The command line arguments passed in (none honoured)
	 */
	public static void main(String[] argv) {
		try {
			// since this is just a simple test, don't bother centering the game...
			final Game game = new CanvasSizeTest();
			final CanvasGameContainer container = new CanvasGameContainer(game);
			final JFrame frame = new JFrame(game.getTitle());
			// exit on close
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					// to avoid ugly flicker when closing, we 
					// can hide the window before destroying OpenGL
					frame.setVisible(false);
					
					// destroys GL/AL context
					container.getContainer().exit();
				}
			});
			
			//background color of frame
			frame.getContentPane().setBackground(java.awt.Color.black);
			
			container.setSize(800, 600);
			frame.getContentPane().add(container);
			frame.pack();
			frame.setResizable(true);
			// centre the frame to the screen
			frame.setLocationRelativeTo(null);
			
			// request focus so that it begins rendering immediately
			// alternatively we could use GameContainer.setAlwaysRender(true)
			container.requestFocusInWindow();
			frame.setVisible(true);
			container.start();
		} catch (SlickException ex) {
			ex.printStackTrace();
		}
	}
}
