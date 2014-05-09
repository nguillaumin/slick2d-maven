package org.newdawn.slick.tests;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * A test for the AWT Canvas container
 *
 * @author kevin
 */
public class CanvasContainerTest extends BasicGame {
	/** The TGA image loaded */
	private Image tga;
	/** The TGA image loaded */
	private Image scaleMe;
	/** The TGA image loaded */
	private Image scaled;
	/** The GIF version of the image */
	private Image gif;
	/** The image we're currently displaying */
	private Image image;
	/** A sub part of the logo image */
	private Image subImage;
	/** The current rotation of our test image */
	private float rot;

	/** The fixed width of our game (but not necessarily our window). */
	public static final int GAME_WIDTH = 800;
	/** The fixed height of our game (but not necessarily our window). */
	public static final int GAME_HEIGHT = 600;
	
	/**
	 * Create a new image rendering test
	 */
	public CanvasContainerTest() {
		super("Canvas Container Test");
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		container.getGraphics().setBackground(Color.darkGray);
		image = tga = new Image("testdata/logo.tga");
		scaleMe = new Image("testdata/logo.tga", true, Image.FILTER_NEAREST);
		gif = new Image("testdata/logo.gif");
		scaled = gif.getScaledCopy(120, 120);
		subImage = image.getSubImage(200,0,70,260);
		rot = 0;
	}

	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		// generally speaking, GAME_WIDTH should be used instead of
		// container.getWidth(), since our game now expects a fixed resolution
		image.draw(GAME_WIDTH-image.getWidth(),0);
		
		image.draw(0,0);
		scaleMe.draw(500,100,200,100);
		scaled.draw(400,500);
		Image flipped = scaled.getFlippedCopy(true, false);
		flipped.draw(520,500);
		Image flipped2 = flipped.getFlippedCopy(false, true);
		flipped2.draw(520,380);
		Image flipped3 = flipped2.getFlippedCopy(true, false);
		flipped3.draw(400,380);
		
		for (int i=0;i<3;i++) {
			subImage.draw(200+(i*30),300);
		}
		
		g.translate(500, 200);
		g.rotate(50, 50, rot);
		g.scale(0.3f,0.3f);
		image.draw();
		g.resetTransform();
	}

	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		rot += delta * 0.1f;
		if (rot > 360) {
			rot -= 360;
		}
	}
	
	

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			// below we're utilizing Swing layout to keep our game at a fixed
			// resolution, regardless of JFrame size!

			final Game game = new CanvasContainerTest();
			final CanvasGameContainer canvasPanel = new CanvasGameContainer(game);
			final JFrame frame = new JFrame(game.getTitle());
			
			// exit on close
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					// to avoid ugly flicker when closing, we 
					// can hide the window before destroying OpenGL
					frame.setVisible(false);
					
					// destroys GL/AL context
					canvasPanel.getContainer().exit();
				}
			});
			canvasPanel.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						GameContainer container = canvasPanel.getContainer();
						if (container.running())
							container.exit();
						else {
							try {
								canvasPanel.start();
								System.out.println("starting");
							} catch (SlickException e1) {
								container.exit();
								e1.printStackTrace();
							}
						}
					}
				}
			});

			// background color of frame
			frame.getContentPane().setBackground(java.awt.Color.black);

			// the size of our game
			Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
			canvasPanel.setPreferredSize(size);
			canvasPanel.setMinimumSize(size);
			canvasPanel.setMaximumSize(size);

			// layout our game canvas so that it's centred
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.CENTER;
			frame.getContentPane().setLayout(new GridBagLayout());
			frame.getContentPane().add(canvasPanel, c);
			
			frame.pack();
			frame.setResizable(true);
			// centre the frame to the screen
			frame.setLocationRelativeTo(null);
			
			// request focus so that it begins rendering immediately
			// alternatively we could use GameContainer.setAlwaysRender(true)
			canvasPanel.requestFocusInWindow();
			frame.setVisible(true);
			canvasPanel.start();
		} catch (SlickException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_SPACE) {
			if (image == gif) {
				image = tga;
			} else {
				image = gif;
			}
		}
	}
}
