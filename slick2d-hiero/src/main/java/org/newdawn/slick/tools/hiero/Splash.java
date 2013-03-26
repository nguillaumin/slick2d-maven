package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.newdawn.slick.tools.hiero.truetype.FontData;
import org.newdawn.slick.tools.hiero.truetype.StatusListener;

/**
 * A splash screen to show startup progress
 * 
 * @author kevin
 */
public class Splash extends Window implements StatusListener {
	/** The background image to be displayed */
	private BufferedImage image;
	/** The message to be displayed */
	private String message = "Locating fonts..";
	
	/**
	 * Create a new splash screen 
	 */
	Splash() {
		super(new Frame());
		
		FontData.setStatusListener(this);
		
		try {
			image = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/newdawn/slick/tools/hiero/resources/splash.png"));
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			setSize(400,250);
			setLocation((size.width - getWidth())/2, (size.height - getHeight()) / 2);
			setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see java.awt.Container#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		paint(g);
	}
	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(Font.BOLD, 14));
		g.drawString(message, 10, 235);
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.truetype.StatusListener#updateStatus(java.lang.String)
	 */
	public void updateStatus(String msg) {
		message = msg;
		repaint(0);
	}

}
