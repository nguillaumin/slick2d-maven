package org.newdawn.slick.opengl;

import java.nio.ByteBuffer;

import org.newdawn.slick.opengl.renderer.SGL;

/**
 * A description of any class providing ImageData in a form suitable for OpenGL texture
 * creation.
 * 
 * @author kevin
 */
public interface ImageData {    
    /**
     * The format of the image data. This class is used to encapsulate the
     * different image formats that could be load and used by OpenGL.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    public static enum Format {
        /** he format for RGB images (no alpha). */
        RGB(3, 24, false, SGL.GL_RGB),

        /** he format for RGB images with alpha. */
        BGRA(4, 32, true, SGL.GL_BGRA),

        /** he format for RGB images with alpha. */
        RGBA(4, 32, true, SGL.GL_RGBA),
        
        /** The format for alpha-only images. */
        ALPHA(1, 8, true, SGL.GL_ALPHA),

        /** he format for grayscale images (no alpha). */
        GRAY(1, 8, false, SGL.GL_LUMINANCE),

        /** he format for grayscale images with alpha. */
        GRAYALPHA(2, 16, true, SGL.GL_LUMINANCE_ALPHA);

        /**
         * Stores if this format has a alpha component.
         */
        private final boolean alpha;
        
        /**
         * Stores the count of bits used to encode one pixel of the image.
         */
        private final int bitdepth;
        
        /**
         * Stores the count of color components (including the alpha channel) of
         * the image.
         */
        private final int components;
        
        /**
         * Contains the best fitting OpenGL format.
         */
        private final int OGLtype;
        
        /**
         * Constructor used to create the format instances.
         * 
         * @param comp component count
         * @param depth bit depth per pixel
         * @param hasAlpha has a alpha channel?
         * @param openGL openGL format
         */
        private Format(final int comp, final int depth, final boolean hasAlpha, final int openGL) {
            components = comp;
            bitdepth = depth;
            alpha = hasAlpha;
            OGLtype = openGL;
        }
        
        /**
         * Check if the image with this format has a alpha channel.
         * 
         * @return <code>true</code> in case the image has a alpha channel
         */
        public boolean hasAlpha() {
            return alpha;
        }
        
        /**
         * Get the amount of bits used to encode one pixel in case the image
         * uses this format.
         * 
         * @return bits per pixel
         */
        public int getBitPerPixel() {
            return bitdepth;
        }
        
        /**
         * Get the count of bits used to encode one color.
         * 
         * @return the bits used to encode one color
         */
        public int getBitPerColor() {
            return bitdepth / components;
        }
        
        /**
         * The amount of color components (including the alpha channel) of the
         * image using this format.
         * 
         * @return the count of color and alpha components of this image
         */
        public int getColorComponents() {
            return components;
        }
        
        /**
         * The OpenGL type that fits best to load this image.
         * 
         * @return the best fitting OpenGL type
         */
        public int getOGLType() {
            return OGLtype;
        }
    }
    
    /**
     * Get the format of this image.
     * 
     * @return the image format
     */
	public Format getFormat();

	/**
	 * Get the last width read from a TGA
	 * 
	 * @return Get the last width in pixels fread from a TGA
	 */
	public int getWidth();

	/**
	 * Get the last height read from a TGA
	 * 
	 * @return Get the last height in pixels fread from a TGA
	 */
	public int getHeight();

	/**
	 * Get the last required texture width for a loaded image
	 * 
	 * @return Get the ast required texture width for a loaded image
	 */
	public int getTexWidth();

	/**
	 * Get the ast required texture height for a loaded image
	 * 
	 * @return Get the ast required texture height for a loaded image
	 */
	public int getTexHeight();
	
	/**
	 * Get the store image
	 * 
	 * @return The stored image
	 */
	public ByteBuffer getImageBufferData();

}
