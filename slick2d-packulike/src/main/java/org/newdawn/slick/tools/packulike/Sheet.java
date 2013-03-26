package org.newdawn.slick.tools.packulike;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A sprite sheet generated with pack-u-like
 * 
 * @author kevin
 */
public class Sheet {
	/** The image built for this sheet */
	private BufferedImage image;
	/** The list of sprite descriptors */
	private ArrayList sprites;
	
	/**
	 * Create a new sheet
	 * 
	 * @param image The image built for the sheet
	 * @param sprites The sprite descriptors
	 */
	public Sheet(BufferedImage image, ArrayList sprites) {
		this.image = image;
		this.sprites = sprites;
	}
	
	/**
	 * Get the image built for this sheet
	 * 
	 * @return The image build for this sheet
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * Get the list of sprites for this sheet
	 * 
	 * @return The list of sprites for this sheet
	 */
	public ArrayList getSprites() {
		return sprites;
	}
}
