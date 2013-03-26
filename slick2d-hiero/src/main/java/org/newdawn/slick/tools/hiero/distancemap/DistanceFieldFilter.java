package org.newdawn.slick.tools.hiero.distancemap;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.newdawn.slick.tools.hiero.ProgressListener;

/**
 * A filter to create a distance field from a source image
 * 
 * @author Orangy
 */
public class DistanceFieldFilter
{
	/** The progress indicator */
	private static int progress;
	
	/**
	 * Caclulate the distance between two points
	 * 
	 * @param x1 The x coordinate of the first point
	 * @param y1 The y coordiante of the first point
 	 * @param x2 The x coordinate of the second point
	 * @param y2 The y coordinate of the second point
	 * @return The distance between two point
	 */
	private static float separation(final float x1, final float y1, final float x2, final float y2)
	{
		final float dx = x1 - x2;
		final float dy = y1 - y2;
		return (float)Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Process the image into a distance field
	 * 
	 * @param inImage The image to process
	 * @param outWidth The width of the output field
	 * @param outHeight The height of the output field
	 * @param scanSize The scan size, controls the quality
	 * @param listener The lisetener to report progress to
	 * @return The distance field image
	 */
	public static BufferedImage process(BufferedImage inImage, int outWidth, int outHeight, int scanSize, ProgressListener listener)
	{
		System.out.println("DistanceFieldFilter.process");
		
		BufferedImage outImage = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_4BYTE_ABGR);
		float[][] distances = new float[outImage.getWidth()][outImage.getHeight()];
		
		final int blockWidth = inImage.getWidth() / outImage.getWidth();
		final int blockHeight = inImage.getHeight() / outImage.getHeight();
		
		System.out.println("Block size is "+blockWidth+","+blockHeight);
		
		for (int x=0; x<outImage.getWidth(); x++)
		{
			listener.reportProgress("Finding signed distance", x, outImage.getWidth());
			for (int y=0; y<outImage.getHeight(); y++)
			{
				distances[x][y] = findSignedDistance( (x * blockWidth) + (blockWidth / 2),
													  (y * blockHeight) + (blockHeight / 2),
													  inImage,
													  scanSize, scanSize);
			}
		}
		
		float max = 0;
		for (int x=0; x<distances.length; x++)
		{
			for (int y=0; y<distances[0].length; y++)
			{
				final float d = distances[x][y];
				if (d != Float.MAX_VALUE && d > max)
					max = d;
			}
		}
		float min = 0;
		
		for (int x=0; x<distances.length; x++)
		{
			for (int y=0; y<distances[0].length; y++)
			{
				final float d = distances[x][y];
				if (d != Float.MIN_VALUE && d < min)
					min = d;
			}
		}
		
		final float range = max - min;
		final float scale = Math.max( Math.abs(min), Math.abs(max) );
		
		System.out.println("Max: "+max+", Min:"+min+", Range:"+range);
		
		for (int x=0; x<distances.length; x++)
		{
			for (int y=0; y<distances[0].length; y++)
			{
				float d = distances[x][y];
				
				if (d == Float.MAX_VALUE)
					d = 1.0f;
				else if (d == Float.MIN_VALUE)
					d = 0.0f;
				else
				{
					d /= scale;
					d /= 2;
					d += 0.5f;
				}
				
				distances[x][y] = d;
			}
		}

		for (int x=0; x<distances.length; x++)
		{
			listener.reportProgress("Setting Image", x, outImage.getWidth());
			for (int y=0; y<distances[0].length; y++)
			{
				float d = distances[x][y];
				if (d == Float.NaN)
					d = 0;
				
				// As greyscale
			//	outImage.setRGB(x, y, new Color(d, d, d, 1.0f).getRGB());
				
				// As alpha
				outImage.setRGB(x, y, new Color(1.0f, 1.0f, 1.0f, d).getRGB());
				
				// As both
			//	outImage.setRGB(x, y, new Color(d, d, d, d).getRGB());
			}
		}
		
		return outImage;
	}
	
	/**
	 * Get the progress indicator
	 * 
	 * @return The progress indicator
	 */
	public static int progress() {
		return progress;
	}
	
	/**
	 * Find the signed distance for a given point
	 * 
	 * @param pointX The x coordinate of the point 
	 * @param pointY The y coordinate of the point
	 * @param inImage The image on which the point exists
	 * @param scanWidth The scan line of the image
	 * @param scanHeight The scan height of the image
	 * @return The signed distance
	 */
	private static float findSignedDistance(final int pointX, final int pointY, BufferedImage inImage, final int scanWidth, final int scanHeight)
	{
		Color baseColour = new Color(inImage.getRGB(pointX, pointY) );
		final boolean baseIsSolid = baseColour.getRed() > 0;
		
		float closestDistance = Float.MAX_VALUE;
		boolean closestValid = false;
		
		final int startX = pointX - (scanWidth / 2);
		final int endX  = startX + scanWidth;
		final int startY = pointY - (scanHeight / 2);
		final int endY = startY + scanHeight;
		
		for (int x=startX; x<endX; x++)
		{
			if (x < 0 || x >= inImage.getWidth())
				continue;
			
			for (int y=startY; y<endY; y++)
			{			
				if (y < 0 || y >= inImage.getWidth())
					continue;
				
				Color c = new Color(inImage.getRGB(x, y));
				
				if (baseIsSolid)
				{
					if (c.getRed() == 0)
					{
						final float dist = separation(pointX, pointY, x, y);
						if (dist < closestDistance)
						{
							closestDistance = dist;
							closestValid = true;
						}
					}
				}
				else
				{
					if (c.getRed() > 0)
					{
						final float dist = separation(pointX, pointY, x, y);
						if (dist < closestDistance)
						{
							closestDistance = dist;
							closestValid = true;
						}
					}
				}
			}
		}
		
		if (baseIsSolid)
		{
			if (closestValid)
				return closestDistance;
			else
				return Float.MAX_VALUE;
		}
		else
		{
			if (closestValid)
				return -closestDistance;
			else
				return Float.MIN_VALUE;
		}
	}
}