package org.newdawn.slick.opengl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.imageio.ImageIO;

/**
 * An image data provider that uses ImageIO to retrieve image data in a format
 * suitable for creating OpenGL textures. This implementation is used when
 * formats not natively supported by the library are required.
 *
 * @author kevin
 */
public class ImageIOImageData implements LoadableImageData {
	/** The colour model for a image with the RGBA format. */
    private static final ColorModel glColorModelRGBA = 
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
	            new int[] {8,8,8,8},
	            true,
	            false,
	            ComponentColorModel.TRANSLUCENT,
	            DataBuffer.TYPE_BYTE);

    /** The colour model for a image with the RGB format. */
    private static final  ColorModel glColorModelRGB =
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
    
    /** The colour model for a image with the GRAY+ALPHA format. */
    private static final ColorModel glColorModelGRAYALPHA = 
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[] {8,8,0,0},
                true,
                false,
                ComponentColorModel.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);

    /** The colour model for a image with the GRAY format. */
    private static final  ColorModel glColorModelGRAY =
            new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[] {8,0,0,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
    
    /** The format of this image */
    private Format format;
    /** The height of the image */
    private int height;
    /** The width of the image */
    private int width;
    /** The width of the texture that should be created for the image */
    private int texWidth;
    /** The height of the texture that should be created for the image */
    private int texHeight;
    /** True if we should edge */
    private boolean edging = true;
    
    /**
     * @see org.newdawn.slick.opengl.ImageData#getFormat()
     */
	public Format getFormat() {
		return format;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getTexHeight()
	 */
	public int getTexHeight() {
		return texHeight;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getTexWidth()
	 */
	public int getTexWidth() {
		return texWidth;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream)
	 */
	public ByteBuffer loadImage(InputStream fis) throws IOException {
		return loadImage(fis, true, null);
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream, boolean, int[])
	 */
	public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException {
		return loadImage(fis, flipped, false, transparent);
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream, boolean, boolean, int[])
	 */
	public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
		if (transparent != null) {
			forceAlpha = true;
		}
		
		BufferedImage bufferedImage = ImageIO.read(fis);
		return imageToByteBuffer(bufferedImage, flipped, forceAlpha, transparent);
	}
	
	public ByteBuffer imageToByteBuffer(BufferedImage image, boolean flipped, boolean forceAlpha, int[] transparent) {
	    ByteBuffer imageBuffer = null; 
        WritableRaster raster;
        BufferedImage texImage;
        
        int texWidth = 2;
        int texHeight = 2;
        
        // find the closest power of 2 for the width and height
        // of the produced texture

        while (texWidth < image.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < image.getHeight()) {
            texHeight *= 2;
        }
        
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.texHeight = texHeight;
        this.texWidth = texWidth;
        
        // create a raster that can be used by OpenGL as a source
        // for a texture
        boolean useAlpha = image.getColorModel().hasAlpha() || forceAlpha;
        boolean isRGB = image.getColorModel().getNumColorComponents() == 3;
        
        ColorModel usedModel;
        if (isRGB) {
            if (useAlpha) {
                usedModel = glColorModelRGBA;
                format = Format.RGBA;
            } else {
                usedModel = glColorModelRGB;
                format = Format.RGB;
            }
        } else {
            if (useAlpha) {
                usedModel = glColorModelGRAYALPHA;
                format = Format.GRAYALPHA;
            } else {
                usedModel = glColorModelGRAY;
                format = Format.GRAY;
            }
        }
        
        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,format.getColorComponents(),null);
        texImage = new BufferedImage(usedModel,raster,false,new Hashtable());
            
        // copy the source image into the produced image
        Graphics2D g = (Graphics2D) texImage.getGraphics();
        
        // only need to blank the image for mac compatibility if we're using alpha
        if (useAlpha) {
	        g.setColor(new Color(0f,0f,0f,0f));
	        g.fillRect(0,0,texWidth,texHeight);
        }
        
        if (flipped) {
        	g.scale(1,-1);
        	g.drawImage(image,0,-height,null);
        } else {
        	g.drawImage(image,0,0,null);
        }
        
        if (edging) {
	        if (height < texHeight - 1) {
	        	copyArea(texImage, 0, 0, width, 1, 0, texHeight-1);
	        	copyArea(texImage, 0, height-1, width, 1, 0, 1);
	        }
	        if (width < texWidth - 1) {
	        	copyArea(texImage, 0,0,1,height,texWidth-1,0);
	        	copyArea(texImage, width-1,0,1,height,1,0);
	        }
        }
        
        // build a byte buffer from the temporary image 
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
        
        if (!format.hasAlpha() && transparent != null) {
            final int components = format.getColorComponents();
            final int size = texWidth*texHeight*components;
            boolean match;
            
            for (int i = 0; i < size; i += components) {
                match = true;
                for (int c=0;c<components;c++) {
                    if (toInt(data[i+c]) != transparent[c]) {
                        match = false;
                        break;
                    }
                }
      
                if (match) {
                    data[i+components] = 0;
                }
            }
        }
        
        imageBuffer = ByteBuffer.allocateDirect(data.length); 
        imageBuffer.order(ByteOrder.nativeOrder()); 
        imageBuffer.put(data, 0, data.length); 
        imageBuffer.flip();
        g.dispose();
        
        return imageBuffer; 
	}
    
    /**
     * Safe convert byte to int
     *  
     * @param b The byte to convert
     * @return The converted byte
     */
    private int toInt(byte b) {
        if (b < 0) {
            return 256+b;
        }
        
        return b;
    }
	
	/**
	 * @see org.newdawn.slick.opengl.ImageData#getImageBufferData()
	 */
	public ByteBuffer getImageBufferData() {
		throw new RuntimeException("ImageIOImageData doesn't store it's image.");
	} 
	
	/**
	 * Implement of transform copy area for 1.4
	 * 
	 * @param image The image to copy
 	 * @param x The x position to copy to
	 * @param y The y position to copy to
	 * @param width The width of the image
	 * @param height The height of the image
	 * @param dx The transform on the x axis
	 * @param dy The transform on the y axis
	 */
	private void copyArea(BufferedImage image, int x, int y, int width, int height, int dx, int dy) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.drawImage(image.getSubimage(x, y, width, height),x+dx,y+dy,null);
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#configureEdging(boolean)
	 */
	public void configureEdging(boolean edging) {
		this.edging = edging;
	}
}
