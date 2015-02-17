package org.newdawn.slick.opengl;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.input.Cursor.CURSOR_8_BIT_ALPHA;
import static org.lwjgl.input.Cursor.CURSOR_ONE_BIT_TRANSPARENCY;

/**
 * A utility to load cursors (thanks go to Kappa for the animated cursor
 * loader)
 * 
 * @author Kevin Glass
 * @author Kappa-One
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CursorLoader {
	/** The single instance of this loader to exist */
	private static CursorLoader single = new CursorLoader();

	/**
	 * Retrieve the single instance of this loader - convenient huh?
	 * 
	 * @return The single instance of the cursor loader
	 */
	public static CursorLoader get() {
		return single;
	}
    
    /**
     * Create a new cursor loader 
     */
	private CursorLoader() {
	}

    /**
     * The transparency threshold.
     */
    private float transparencyThreshold = 0.8f;

    /**
     * Get the current transparency threshold value.
     *
     * @return the transparency threshold
     * @see #setTransparencyThreshold(float)
     */
    public float getTransparencyThreshold() {
        return transparencyThreshold;
    }

    /**
     * Set the threshold value for the conversation between eight and one bit alpha. In case the opacity of a pixel is
     * greater then the value applied here (1.0 fully opaque, 0.0 fully transparent) the pixel is altered as needed in
     * case the host system does not support 8bit alpha cursors.
     *
     * @param value the threshold value
     * @throws IllegalArgumentException in case the value is less then 0 or greater then 1
     */
    public void setTransparencyThreshold(final float value) {
        if (value < 0.f || value > 1.f) {
            throw new IllegalArgumentException("Value is outside of valid range.");
        }
        transparencyThreshold = value;
    }

    /**
     * Apply the threshold value to the alpha value. This is needed to display cursors with 8bit alpha properly on
     * systems that support only one bit alpha.
     *
     * @param alpha the real alpha value
     * @return the one bit alpha value
     */
    private byte applyThreshold(final byte alpha) {
        int value = alpha;
        if (value < 0) {
            value = 256 + value;
        }
        if (value > 256 * transparencyThreshold) {
            return (byte) -1;
        } else {
            return (byte) 0;
        }
    }
	
	/**
	 * Get a cursor based on a image reference on the classpath
	 * 
	 * @param ref The reference to the image to be loaded
	 * @param x The x-coordinate of the cursor hotspot (left -> right)
	 * @param y The y-coordinate of the cursor hotspot (bottom -> top)
	 * @return The create cursor
	 * @throws IOException Indicates a failure to load the image
	 * @throws LWJGLException Indicates a failure to create the hardware cursor
     * @throws IllegalArgumentException in case the width or the height is greater then the image width or height or in
     *          case height or width is 0 or less.
	 */
	public Cursor getCursor(final String ref, final int x, final int y) throws IOException, LWJGLException {
		LoadableImageData imageData = null;
		
		imageData = ImageDataFactory.getImageDataFor(ref);
		imageData.configureEdging(false);

        ByteBuffer buff = imageData.loadImage(ResourceLoader.getResourceAsStream(ref), true, true, null);
        return getCursor(buff, x, y, imageData.getWidth(), imageData.getHeight());
	}

	/**
	 * Get a cursor based on a set of image data
	 * 
	 * @param buf The image data (stored in RGBA) to load the cursor from
	 * @param x The x-coordinate of the cursor hotspot (left -> right)
	 * @param y The y-coordinate of the cursor hotspot (bottom -> top)
	 * @param width The width of the image data provided
	 * @param height The height of the image data provided
	 * @return The create cursor
	 * @throws IOException Indicates a failure to load the image
	 * @throws LWJGLException Indicates a failure to create the hardware cursor
	 */
	public Cursor getCursor(final ByteBuffer buf, final int x, final int y, final int width, final int height) throws IOException, LWJGLException {
        return getCursor(buf, x, y, width, height, width, height);
    }

    /**
     * Get a cursor based on a set of image data
     *
     * @param buf The image data (stored in RGBA) to load the cursor from
     * @param x The x-coordinate of the cursor hotspot (left -> right)
     * @param y The y-coordinate of the cursor hotspot (bottom -> top)
     * @param width The width of the image data provided
     * @param height The height of the image data provided
     * @param imageWidth The width of the actual image, the pixels outside of this width are considered blank
     * @param imageHeight The height of the actual image, the pixels outside of this height are considered blank
     * @return The create cursor
     * @throws IOException Indicates a failure to load the image
     * @throws LWJGLException Indicates a failure to create the hardware cursor
     * @throws IllegalArgumentException in case the width or the height is greater then the image width or height or in
     *          case height or width is 0 or less.
     */
    public Cursor getCursor(ByteBuffer buf, int x, int y, int width, int height, int imageWidth, int imageHeight) throws IOException, LWJGLException {
        if (height < imageHeight) {
            throw new IllegalArgumentException("The image height can't be larger then the actual texture size.");
        }
        if (width < imageWidth) {
            throw new IllegalArgumentException("The image width can't be larger then the actual texture size.");
        }
        if (width <= 0 || height <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            throw new IllegalArgumentException("Zero is a illegal value for height and width values");
        }

        final int capabilities = Cursor.getCapabilities();
        final boolean transparencySupport = (capabilities & CURSOR_ONE_BIT_TRANSPARENCY) != 0;
        final boolean fullTransparencySupport = (capabilities & CURSOR_8_BIT_ALPHA) != 0;

        if (!transparencySupport) {
            Log.info("Your system does not support cursors with transparency. The mouse cursor may look messy.");
        }

        for (int i=0;i<buf.limit();i+=4) {
            byte red = buf.get(i);
            byte green = buf.get(i+1);
            byte blue = buf.get(i+2);
            byte alpha = buf.get(i+3);

            buf.put(i+2, red);
            buf.put(i+1, green);
            buf.put(i, blue);

            if (fullTransparencySupport) {
                buf.put(i+3, alpha);
            } else if (transparencySupport) {
                buf.put(i+3, applyThreshold(alpha));
            } else {
                buf.put(i+3, (byte) -1);
            }
        }

        final int maxSize = Cursor.getMaxCursorSize();
        final int minSize = Cursor.getMinCursorSize();

        int cursorTextureHeight = height;
        int cursorTextureWidth = width;

        int ySpot = imageHeight - y - 1;
        int xSpot = x;
        if (ySpot < 0) {
            ySpot = 0;
        }

        if ((cursorTextureHeight > maxSize) || (cursorTextureWidth > maxSize)) {
            final int targetHeight = Math.min(maxSize, cursorTextureHeight);
            final int targetWidth = Math.min(maxSize, cursorTextureWidth);

            ySpot -= imageHeight - targetHeight;
            xSpot -= imageWidth - targetWidth;

            final byte pixelBuffer[] = new byte[4];
            ByteBuffer tempBuffer = BufferUtils.createByteBuffer(targetHeight * targetWidth * 4);
            BufferUtils.zeroBuffer(tempBuffer);
            for (int tempX = 0; tempX < targetHeight; tempX++) {
                for (int tempY = 0; tempY < targetWidth; tempY++) {
                    buf.position((tempX + tempY * cursorTextureWidth) * 4);
                    buf.get(pixelBuffer);

                    tempBuffer.position((tempX + tempY * targetWidth) * 4);
                    tempBuffer.put(pixelBuffer);
                }
            }

            cursorTextureHeight = targetHeight;
            cursorTextureWidth = targetWidth;
            buf = tempBuffer;
        }

        if ((cursorTextureHeight < minSize) || (cursorTextureWidth < minSize)) {
            final int targetHeight = Math.max(minSize, cursorTextureHeight);
            final int targetWidth = Math.max(minSize, cursorTextureWidth);

            final byte pixelBuffer[] = new byte[4];
            ByteBuffer tempBuffer = BufferUtils.createByteBuffer(targetHeight * targetWidth * 4);
            BufferUtils.zeroBuffer(tempBuffer);
            for (int tempX = 0; tempX < imageWidth; tempX++) {
                for (int tempY = 0; tempY < imageHeight; tempY++) {
                    buf.position((tempX + tempY * cursorTextureWidth) * 4);
                    buf.get(pixelBuffer);

                    tempBuffer.position((tempX + tempY * targetWidth) * 4);
                    tempBuffer.put(pixelBuffer);
                }
            }

            cursorTextureHeight = targetHeight;
            cursorTextureWidth = targetWidth;
            buf = tempBuffer;
        }

        try {
            buf.position(0);
            return new Cursor(cursorTextureWidth, cursorTextureHeight, xSpot, ySpot, 1, buf.asIntBuffer(), null);
        } catch (Throwable e) {
            Log.info("Chances are you cursor is too small for this platform");
            throw new LWJGLException(e);
        }
	}
	
	/**
	 * Get a cursor based on a set of image data
	 * 
	 * @param imageData The data from which the cursor can read it's contents
	 * @param x The x-coordinate of the cursor hotspot (left -> right)
	 * @param y The y-coordinate of the cursor hotspot (bottom -> top)
	 * @return The create cursor
	 * @throws IOException Indicates a failure to load the image
	 * @throws LWJGLException Indicates a failure to create the hardware cursor
     * @throws IllegalArgumentException in case the width or the height is greater then the image width or height or in
     *          case height or width is 0 or less.
	 */
	public Cursor getCursor(ImageData imageData,int x,int y) throws IOException, LWJGLException {
        return getCursor(imageData.getImageBufferData(), x, y, imageData.getTexWidth(), imageData.getTexHeight(),
                imageData.getWidth(), imageData.getHeight());
	}
	
	/**
	 * Get a cursor based on a image reference on the classpath. The image 
	 * is assumed to be a set/strip of cursor animation frames running from top to 
	 * bottom.
	 * 
	 * @param ref The reference to the image to be loaded
	 * @param x The x-coordinate of the cursor hotspot (left -> right)
	 * @param y The y-coordinate of the cursor hotspot (bottom -> top)
	 * @param width The x width of the cursor
	 * @param height The y height of the cursor
	 * @param cursorDelays image delays between changing frames in animation
	 * 					
	 * @return The created cursor
	 * @throws IOException Indicates a failure to load the image
	 * @throws LWJGLException Indicates a failure to create the hardware cursor
     * @throws IllegalArgumentException in case the width or the height is greater then the image width or height or in
     *          case height or width is 0 or less.
	 */
	public Cursor getAnimatedCursor(String ref,int x,int y, int width, int height, int[] cursorDelays) throws IOException, LWJGLException {
		IntBuffer cursorDelaysBuffer = ByteBuffer.allocateDirect(cursorDelays.length*4).order(ByteOrder.nativeOrder()).asIntBuffer();
		for (int i=0;i<cursorDelays.length;i++) {
			cursorDelaysBuffer.put(cursorDelays[i]);
		}
		cursorDelaysBuffer.flip();

		LoadableImageData imageData = new TGAImageData();
		ByteBuffer buf = imageData.loadImage(ResourceLoader.getResourceAsStream(ref), false, null);
					
		return new Cursor(width, height, x, y, cursorDelays.length, buf.asIntBuffer(), cursorDelaysBuffer);
	}
}
