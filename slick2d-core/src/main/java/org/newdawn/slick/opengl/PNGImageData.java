package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.util.Log;

/**
 * The PNG imge data source that is pure java reading PNGs
 * 
 * @author Matthias Mann (original code)
 */
public class PNGImageData implements LoadableImageData {
	/** The width of the data loaded */
	private int width;
	/** The height of the data loaded */
	private int height;
	/** The texture height */
	private int texHeight;
	/** The texture width */
	private int texWidth;
	/** The decoder used to load the PNG */
	private PNGDecoder decoder;
	/** The data format of this PNG */
	private Format format;
	/** The scratch buffer storing the image data */
	private ByteBuffer scratch;
	
    /**
     * @see org.newdawn.slick.opengl.ImageData#getFormat()
     */
	public Format getFormat() {
		return format;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getImageBufferData()
	 */
	public ByteBuffer getImageBufferData() {
		return scratch;
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
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream)
	 */
	public ByteBuffer loadImage(InputStream fis) throws IOException {
		return loadImage(fis, false, null);
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
		
		PNGDecoder decoder = new PNGDecoder(fis);
		
		width = decoder.getWidth();
		height = decoder.getHeight();
		texWidth = get2Fold(width);
		texHeight = get2Fold(height);

        final PNGDecoder.Format decoderFormat;
        if (forceAlpha) {
            if (decoder.isRGB()) {
                decoderFormat = decoder.decideTextureFormat(PNGDecoder.Format.RGBA);
            } else {
                decoderFormat = decoder.decideTextureFormat(PNGDecoder.Format.LUMINANCE_ALPHA);
            }
        } else {
            decoderFormat = decoder.decideTextureFormat(PNGDecoder.Format.LUMINANCE);
        }

        switch (decoderFormat) {
            case RGB:
                format = Format.RGB;
                break;
            case RGBA:
                format = Format.RGBA;
                break;
            case BGRA:
                format = Format.BGRA;
                break;
            case LUMINANCE:
                format = Format.GRAY;
                break;
            case LUMINANCE_ALPHA:
                format = Format.GRAYALPHA;
                break;
            default:
                throw new IOException("Unsupported Image format.");
        }
		
		int perPixel = format.getColorComponents();
		
		// Get a pointer to the image memory
		scratch = BufferUtils.createByteBuffer(texWidth * texHeight * perPixel);

        if (flipped) {
            decoder.decodeFlipped(scratch, texWidth * perPixel, decoderFormat);
        } else {
		    decoder.decode(scratch, texWidth * perPixel, decoderFormat);
        }

		if (height < texHeight-1) {
			int topOffset = (texHeight-1) * (texWidth*perPixel);
			int bottomOffset = (height-1) * (texWidth*perPixel);
			for (int x=0;x<texWidth;x++) {
				for (int i=0;i<perPixel;i++) {
					scratch.put(topOffset+x+i, scratch.get(x+i));
					scratch.put(bottomOffset+(texWidth*perPixel)+x+i, scratch.get(bottomOffset+x+i));
				}
			}
		}
		if (width < texWidth-1) {
			for (int y=0;y<texHeight;y++) {
				for (int i=0;i<perPixel;i++) {
					scratch.put(((y+1)*(texWidth*perPixel))-perPixel+i, scratch.get(y*(texWidth*perPixel)+i));
					scratch.put((y*(texWidth*perPixel))+(width*perPixel)+i, scratch.get((y*(texWidth*perPixel))+((width-1)*perPixel)+i));
				}
			}
		}

        scratch.position(0);
		
		if (transparent != null) {
			// components will now be + 1
			final int components = format.getColorComponents();

            if (transparent.length != components - 1) {
                Log.warn("The amount of color components of the transparent color does not fit the number of color components of the actual image.");
            }

            if (transparent.length < components - 1) {
                Log.error("Failed to apply transparent color, not enough color values in color definition.");
            } else {

                final int size = texWidth * texHeight * components;
                boolean match;

                for (int i = 0; i < size; i += components) {
                    match = true;
                    for (int c = 0; c < components - 1; c++) {
                        if (toInt(scratch.get(i + c)) != transparent[c]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        scratch.put(i + components - 1, (byte) 0);
                    }
                }
            }
		}

		scratch.position(0);
		
		return scratch;
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
     * Get the closest greater power of 2 to the fold number
     * 
     * @param fold The target number
     * @return The power of 2
     */
    private int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }
    
	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#configureEdging(boolean)
	 */
	public void configureEdging(boolean edging) {
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}

