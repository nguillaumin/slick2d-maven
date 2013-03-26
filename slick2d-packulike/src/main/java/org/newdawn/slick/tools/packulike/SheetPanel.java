package org.newdawn.slick.tools.packulike;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * The panel displaying the currently generated sprite sheet
 * 
 * @author kevin
 */
public class SheetPanel extends JPanel {
	/** The image currently generated */
	private Image image;
	/** The background paint to show transparency */
	private TexturePaint background;
	/** The width of the image */
	private int width;
	/** The height of the image */
	private int height;
	/** The sheet that has been generated */
	private Sheet sheet;
	/** The packer tool this panel is part of */
	private Packer packer;
	/** The list of selected sprites */
	private ArrayList selected = new ArrayList();
	
	/**
	 * Create a panel showing a sprite sheet
	 * 
	 * @param p The GUI tool this panel is part of
	 */
	public SheetPanel(Packer p) {
		this.packer = p;
		
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
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Sprite sprite = packer.getSpriteAt(e.getX(), e.getY());
				
				if (sprite != null) {
					ArrayList selection = new ArrayList();
					if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
						selection.addAll(selected);
					}
					selection.add(sprite);
					
					packer.select(selection);
				}
			}
		});
	}
	
	/**
	 * Set the list of selected sprites
	 * 
	 * @param sprites THe list of selected sprites
	 */
	public void setSelection(ArrayList sprites) {
		this.selected = sprites;
		repaint(0);
	}
	
	/**
	 * Set the size of the sprite sheet
	 * 
	 * @param width The width of the sheet
	 * @param height The height of the sheet
	 */
	public void setTextureSize(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Set the image to be displayed (the sprite sheet)
	 * 
	 * @param sheet The sheet to be displayed
	 */
	public void setImage(Sheet sheet) {
		this.sheet = sheet;
		this.image = sheet.getImage();
		repaint(0);
	}
	
	/**
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1d) {
        Graphics2D g = (Graphics2D) g1d;
        
		g.setPaint(background);
		g.fillRect(0,0,getWidth(), getHeight());
		g.setColor(Color.yellow);
		g.drawRect(0,0,width,height);
		
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
		
		g.setColor(Color.green);
		for (int i=0;i<selected.size();i++) {
			Sprite sprite = (Sprite) selected.get(i);
			
			g.drawRect(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		}
	}
}
