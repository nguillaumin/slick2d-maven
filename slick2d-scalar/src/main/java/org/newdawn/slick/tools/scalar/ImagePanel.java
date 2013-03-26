package org.newdawn.slick.tools.scalar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A panel to display the current image
 *
 * @author kevin
 */
public class ImagePanel extends JPanel {
	/** The image to be displayed */
	private BufferedImage image;
	/** The backgroud paint */
	private TexturePaint background;
	
	/**
	 * Create a new empty image panel
	 */
	public ImagePanel() {
		super();
		
		Color base = Color.gray;
		BufferedImage image = new BufferedImage(50, 50,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(base);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(base.darker());
		g.fillRect(image.getWidth() / 2, 0, image.getWidth() / 2, image
				.getHeight() / 2);
		g.fillRect(0, image.getHeight() / 2, image.getWidth() / 2, image
				.getHeight() / 2);

		background = new TexturePaint(image, new Rectangle(0, 0, image
				.getWidth(), image.getHeight()));
		
    	setBackground(Color.black);
	}
	
	/**
	 * Set the image to be displayed
	 * 
	 * @param image The image to be displayed
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		setSize(new Dimension(image.getWidth(), image.getHeight()));
		getParent().repaint(0);
	}
	
	/**
	 * Get the image currently being displayed
	 * 
	 * @return The image currently being displayed
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1d) {
		Graphics2D g = (Graphics2D) g1d;
		super.paint(g);
		
		g.setPaint(background);
		g.fillRect(0,0,getWidth(),getHeight());
		
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}
}
