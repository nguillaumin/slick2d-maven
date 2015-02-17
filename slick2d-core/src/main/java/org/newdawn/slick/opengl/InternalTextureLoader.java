package org.newdawn.slick.opengl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A texture loaded based on many old versions that will load image data from a file
 * and produce OpenGL textures.
 * 
 * @see ImageData
 * 
 * @author kevin
 */
public class InternalTextureLoader {

    /** Useful for debugging; keeps track of the current number of active textures. */
    static int textureCount = 0;
    
    private static boolean forcePOT = true;
    
    public static boolean isPowerOfTwo(int n) {
        return (n & -n) == n;
    }

	
    /**
     * Returns true if we are forcing loaded image data into power-of-two OpenGL textures (by default,
     * this is true). If non-power-of-two textures is not supported in hardware (i.e. isNPOTSupported
     * returns false), then the image data will be forced into POT textures regardless of isForcePOTSize().
     *   
     * @return true if we should ensure POT sized textures, flase if we should attempt to use NPOT if supported
     */
    public static boolean isForcePOT() {
    	return forcePOT;
    }
    
	/**
     * Set whether we are forcing loaded image data into power-of-two OpenGL textures (by default,
     * this is true). If non-power-of-two textures is not supported in hardware (i.e. isNPOTSupported
     * returns false), then the image data will be forced into POT textures regardless of isForcePOTSize().
     *   
     * @param b true if we should ensure POT sized textures, flase if we should attempt to use NPOT if supported
     */
    public static void setForcePOT(boolean b) {
    	forcePOT = b;
    }

    /**
     * Returns the current number of active textures. Calling InternalTextureLoader.createTextureID
     * increases this number. Calling TextureImpl.release or InternalTextureLoader.deleteTextureID 
     * decreases this number.
     * 
     * @return the number of active OpenGL textures
     */
    public static int getTextureCount() {
    	return textureCount;
    }

    /**
     * Create a new texture ID; will increase the value for getTextureCount.
     *
     * @return A new texture ID
     */
    public static int createTextureID() { 
       IntBuffer tmp = createIntBuffer(1); 
       GL.glGenTextures(tmp);
       textureCount++;
       return tmp.get(0);
    } 
    
    /** 
     * Used internally; call TextureImpl.release. 
     * @param id the id of the OpenGL texture 
     */
    public static void deleteTextureID(int id) {
    	IntBuffer texBuf = createIntBuffer(1); 
        texBuf.put(id);
        texBuf.flip();
    	GL.glDeleteTextures(texBuf);
    	textureCount--;
    }
    
	/**
	 * Slick uses glGenerateMipmap() or GL14.GL_GENERATE_MIPMAP to automatically
	 * build mipmaps (for advanced users). If neither of these versions are supported,
	 * the GL_EXT_framebuffer_object is used as a fallback, and if that extension is also
	 * missing, this method returns false.
	 *  
	 * @return whether the version is >= 1.4 or GL_EXT_framebuffer_object extension exists
	 */
	public static boolean isGenerateMipmapSupported() {
		return GLContext.getCapabilities().OpenGL14 || GLContext.getCapabilities().GL_EXT_framebuffer_object;
	}

	/**
	 * Returns true if non-power-of-two textures are supported in hardware via the
	 * GL_ARB_texture_non_power_of_two extension. Non-power-of-two texture loading
	 * is not a current feature of Slick, although it is planned.
	 * 
	 * @return true if the extension is listed
	 */
	public static boolean isNPOTSupported() {
		//don't check GL20, nvidia/ATI usually don't advertise this extension
		//if it means requiring software fallback
		return GLContext.getCapabilities().GL_ARB_texture_non_power_of_two;
	}
	
	/** The renderer to use for all GL operations */
	protected static SGL GL = Renderer.get();
	/** The standard texture loaded used everywhere */
	private static final InternalTextureLoader loader = new InternalTextureLoader();
	
	/**
	 * Get the single instance of this texture loader
	 * 
	 * @return The single instance of the texture loader
	 */
	public static InternalTextureLoader get() {
		return loader;
	}
	
    /** The table of textures that have been loaded in this loader */
    private HashMap texturesLinear = new HashMap();
    /** The table of textures that have been loaded in this loader */
    private HashMap texturesNearest = new HashMap();
    /** The destination pixel format */
    private int dstPixelFormat = SGL.GL_RGBA8;
    /** True if we're using deferred loading */
    private boolean deferred;
    /** True if we should hold texture data */
    private boolean holdTextureData;
    
    /** 
     * Create a new texture loader based on the game panel
     */
    private InternalTextureLoader() {
    }
    
    /**
     * Indicate where texture data should be held for reinitialising at a future
     * point.
     * 
     * @param holdTextureData True if we should hold texture data
     */
    public void setHoldTextureData(boolean holdTextureData) {
    	this.holdTextureData = holdTextureData;
    }
    
    /**
     * True if we should only record the request to load in the intention
     * of loading the texture later
     * 
     * @param deferred True if the we should load a token
     */
    public void setDeferredLoading(boolean deferred) {
    	this.deferred = deferred;
    }
    
    /**
     * Check if we're using deferred loading
     * 
     * @return True if we're loading deferred textures
     */
    public boolean isDeferredLoading() {
    	return deferred;
    }
    
    /**
     * Remove a particular named image from the cache (does not release the OpenGL texture)
     * 
     * @param name The name of the image to be cleared
     */
    public void clear(String name) {
    	texturesLinear.remove(name);
    	texturesNearest.remove(name);
    }
    
    /**
     * Clear out the cached textures (does not release the OpenGL textures)
     */
    public void clear() {
    	texturesLinear.clear();
    	texturesNearest.clear();
    }
    
    /**
     * Tell the loader to produce 16 bit textures
     */
    public void set16BitMode() {
    	dstPixelFormat = SGL.GL_RGBA16;
    }
    
    
    /**
     * Get a texture from a specific file
     * 
     * @param source The file to load the texture from
     * @param flipped True if we should flip the texture on the y axis while loading
     * @param filter The filter to use
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public Texture getTexture(File source, boolean flipped,int filter) throws IOException {
    	String resourceName = source.getAbsolutePath();
    	InputStream in = new FileInputStream(source);
    	
    	return getTexture(in, resourceName, flipped, filter, null);
    }
    
    /**
     * Get a texture from a specific file
     * 
     * @param source The file to load the texture from
     * @param flipped True if we should flip the texture on the y axis while loading
     * @param filter The filter to use
	 * @param transparent The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public Texture getTexture(File source, boolean flipped,int filter, int[] transparent) throws IOException {
    	String resourceName = source.getAbsolutePath();
    	InputStream in = new FileInputStream(source);
    	
    	return getTexture(in, resourceName, flipped, filter, transparent);
    }

    /**
     * Get a texture from a resource location
     * 
     * @param resourceName The location to load the texture from
     * @param flipped True if we should flip the texture on the y axis while loading
     * @param filter The filter to use when scaling the texture
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public Texture getTexture(String resourceName, boolean flipped, int filter) throws IOException {
    	InputStream in = ResourceLoader.getResourceAsStream(resourceName);
    	
    	return getTexture(in, resourceName, flipped, filter, null);
    }
    
    /**
     * Get a texture from a resource location
     * 
     * @param resourceName The location to load the texture from
     * @param flipped True if we should flip the texture on the y axis while loading
     * @param filter The filter to use when scaling the texture
	 * @param transparent The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public Texture getTexture(String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
    	InputStream in = ResourceLoader.getResourceAsStream(resourceName);
    	
    	return getTexture(in, resourceName, flipped, filter, transparent);
    }
    /**
     * Get a texture from a image file
     * 
     * @param in The stream from which we can load the image
     * @param resourceName The name to give this image in the internal cache
     * @param flipped True if we should flip the image on the y-axis while loading
     * @param filter The filter to use when scaling the texture
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public Texture getTexture(InputStream in, String resourceName, boolean flipped, int filter) throws IOException {
    	return getTexture(in, resourceName, flipped, filter, null);
    }
    
    /**
     * Get a texture from a image file
     * 
     * @param in The stream from which we can load the image
     * @param resourceName The name to give this image in the internal cache
     * @param flipped True if we should flip the image on the y-axis while loading
     * @param filter The filter to use when scaling the texture
	 * @param transparent The colour to interpret as transparent or null if none
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public TextureImpl getTexture(InputStream in, String resourceName, boolean flipped, int filter, int[] transparent) throws IOException {
    	if (deferred) {
	    	return new DeferredTexture(in, resourceName, flipped, filter, transparent);
	    }
    	
    	HashMap hash = texturesLinear;
        if (filter == SGL.GL_NEAREST) {
        	hash = texturesNearest;
        }
        
        String resName = resourceName;
        if (transparent != null) {
        	resName += ":"+transparent[0]+":"+transparent[1]+":"+transparent[2];
        }
        resName += ":"+flipped;
        
        if (holdTextureData) {
        	TextureImpl tex = (TextureImpl)  hash.get(resName);
        	if (tex != null) {
        		return tex;
        	}
        } else {
	    	SoftReference ref = (SoftReference) hash.get(resName);
	    	if (ref != null) {
		    	TextureImpl tex = (TextureImpl) ref.get();
		        if (tex != null) {
		        	return tex;
		        } else {
		        	hash.remove(resName);
		        }
	    	}
        }
        
        // horrible test until I can find something more suitable
        try {
        	GL.glGetError();
        } catch (NullPointerException e) {
        	throw new RuntimeException("Image based resources must be loaded as part of init() or the game loop. They cannot be loaded before initialisation.");
        }
        
        TextureImpl tex = getTexture(in, resourceName,
                         SGL.GL_TEXTURE_2D, 
                         filter, filter, flipped, transparent);
        
        tex.setCacheName(resName);
        if (holdTextureData) {
        	hash.put(resName, tex);
        } else {
        	hash.put(resName, new SoftReference(tex));
        }
        
        return tex;
    }
    
    private TextureImpl getTexture(InputStream in, String resourceName, 
				            int target,  int minFilter,  int magFilter, 
				    		boolean flipped, int[] transparent) throws IOException {
    	// create the texture ID for this texture 
        ByteBuffer textureBuffer;
        
        LoadableImageData imageData = ImageDataFactory.getImageDataFor(resourceName);
    	textureBuffer = imageData.loadImage(new BufferedInputStream(in), flipped, transparent);

        int textureID = createTextureID();        
        TextureImpl texture = new TextureImpl(resourceName, target, textureID); 
        // bind this texture 
        GL.glEnable(target);
        GL.glBindTexture(target, textureID); 
 
        int width;
        int height;
        int texWidth;
        int texHeight;
        
        ImageData.Format format;
        
    	width = imageData.getWidth();
    	height = imageData.getHeight();
    	format = imageData.getFormat();
    	
    	texture.setTextureWidth(imageData.getTexWidth());
    	texture.setTextureHeight(imageData.getTexHeight());

        texWidth = texture.getTextureWidth();
        texHeight = texture.getTextureHeight();

        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL.glGetInteger(SGL.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if ((texWidth > max) || (texHeight > max)) {
        	throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
        
        int srcPixelFormat = format.getOGLType();
        int componentCount = format.getColorComponents();
        
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setImageFormat(format);
        
        if (holdTextureData) {
        	texture.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, textureBuffer);
        }
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter); 
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter); 
        
        // produce a texture from the byte buffer
        GL.glTexImage2D(target, 
                      0, 
                      dstPixelFormat, 
                      get2Fold(width), 
                      get2Fold(height), 
                      0, 
                      srcPixelFormat, 
                      SGL.GL_UNSIGNED_BYTE, 
                      textureBuffer); 
        return texture; 
    }
    
    /**
     * An advanced texture loading method providing more parameters for glTexImage2D. 
     * The created texture will not be placed in the cache.
     * 
     * If genMipmaps is true, the loader will attempt to automatically build mipmaps
     * with either GL30.glGenerateMipmap() or GL14.GL_GENERATE_MIPMAP. If the GL version
     * is less than 1.4, then no mipmaps will be built and instead the magFilter (one
     * of GL_LINEAR or GL_NEAREST) will be used for both minification and magnification.
     * Users can determine mipmap generation support with isGenerateMipmapSupported().
     * 
     * If the internalFormat is not null, then that will override the default pixel
     * format described by this InternalTextureLoader (either GL_RGBA16 or GL_RGBA8
     * depending on the is16BitMode() value). This parameter can be independent
     * of the format of ImageData -- OpenGL will convert the ImageData format (e.g. BGRA)
     * to the given internalFormat (e.g. RGB). Note that internalFormat is more limited
     * than the ImageData's format; i.e. BGRA as an internal storage format is only 
     * supported if GL_ext_bgra is present.
     * 
     * After calling this, the texture will be bound and the target (e.g. GL_TEXTURE_2D)
     * will be enabled. If you are using a higher priority target, such as 3D textures,
     * you should disable that afterwards to ensure compatibility with Slick.
     * 
     * The ByteBuffer data is assumed to match getTexWidth/getTexHeight in ImageData.
     * 
     * @param data the image data holding width, height, format (ImageData byte buffer is ignored)
     * @param buffer the actual data to send to GL 
     * @param ref The name to give the TextureImpl
     * @param target The texture target we're loading this texture into
     * @param minFilter The scaling down filter
     * @param magFilter The scaling up filter
     * @param genMipmaps true to generate mipmaps (failure will fallback to using magFilter)
     * @param internalFormat the internal format of the texture (or null for default)
     * @return The texture loaded
     * @throws IOException Indicates a failure to load the image
     */
    public TextureImpl createTexture(ImageData data, ByteBuffer buffer,
						  String ref, 
                          int target, 
                          int minFilter, 
                          int magFilter, 
                          boolean genMipmaps,
                          ImageData.Format internalFormat) throws IOException { 
    	int textureID = createTextureID();        
        TextureImpl texture = new TextureImpl(ref, target, textureID); 
        // bind this texture 
        GL.glEnable(target);
        GL.glBindTexture(target, textureID); 
 
        int width = data.getWidth();
        int height = data.getHeight();
        int texWidth = data.getTexWidth();
        int texHeight = data.getTexHeight();
        
        boolean usePOT = !isNPOTSupported() || isForcePOT();
        if (usePOT) {
            texWidth = get2Fold(width);
            texHeight = get2Fold(height);
        }

        int max = GL11.glGetInteger(SGL.GL_MAX_TEXTURE_SIZE);
        if (texWidth>max || texHeight>max) 
        	throw new IOException("Attempt to allocate a texture to big for the current hardware");
        
        ImageData.Format dataFormat = data.getFormat();
        int dstFmt = internalFormat!=null ? internalFormat.getOGLType() : dstPixelFormat; 
        int srcFmt = dataFormat.getOGLType();

        texture.setTextureWidth(texWidth);
        texture.setTextureHeight(texHeight);
        texture.setWidth(width);
        texture.setHeight(height);
        //even though it might really be RGBA16/8, user will expect comparability with Format constants
        texture.setImageFormat(internalFormat!=null ? internalFormat : ImageData.Format.RGBA); 
        
        if (holdTextureData) {
        	// TODO: fix the reload functionality; right now it causes problems and 
        	// should probably just be removed or reworked
            int componentCount = dataFormat.getColorComponents();
        	texture.setTextureData(srcFmt, componentCount, minFilter, magFilter, buffer);
        }
        
        ContextCapabilities cx = GLContext.getCapabilities();
        if (genMipmaps && !isGenerateMipmapSupported()) { //nothing for auto mipmap gen
        	minFilter = magFilter;
        	genMipmaps = false;
        }
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter); 
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter); 
        
        //if we are < 3.0 and have no FBO support, fall back to GL_GENERATE_MIPMAP
        if (genMipmaps && !cx.OpenGL30 && !cx.GL_EXT_framebuffer_object) { 
        	GL.glTexParameteri(target, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
        	genMipmaps = false;
        }
        
        //For now, just assume Slick has decoded image data into POT
        GL.glTexImage2D(target, 0, dstFmt, texWidth, texHeight, 0, srcFmt, SGL.GL_UNSIGNED_BYTE, buffer);
        
//        if (texWidth==width && texHeight==height) {
//        	GL.glTexImage2D(target, 0, dstFmt, texWidth, texHeight, 
//        			0, srcFmt, SGL.GL_UNSIGNED_BYTE, buffer);
//        } else {
//        	//Slick2D decodes NPOT image data into padded byte buffers.
//        	//Once we make the shift to decoding NPOT image data, then we can clean this up
//        	GL.glTexImage2D(target, 0, dstFmt, texWidth, texHeight, 
//        			0, srcFmt, SGL.GL_UNSIGNED_BYTE, buffer);
//        	
//        	//first create the full texture
//        	//we could also use a null ByteBuffer but this seems to be buggy with certain machines
////        	ByteBuffer empty = BufferUtils.createByteBuffer(texWidth * texHeight * 4);
////        	GL.glTexImage2D(target, 0, dstFmt, texWidth, texHeight,
////        			0, SGL.GL_RGBA, SGL.GL_UNSIGNED_BYTE, empty);
////        	//then upload the sub image
////        	GL.glTexSubImage2D(target, 0, 0, 0, width, height, srcFmt, SGL.GL_UNSIGNED_BYTE, buffer);
//        }
        
        if (genMipmaps) {
        	GL11.glEnable(target); //fixes ATI bug
        	if (cx.OpenGL30)
        		GL30.glGenerateMipmap(target);
        	else
        		EXTFramebufferObject.glGenerateMipmapEXT(target);
        }
        return texture; 
    } 
    
    /**
     * Create an empty texture
     * 
     * @param width The width of the new texture
     * @param height The height of the new texture
     * @return The created empty texture
     * @throws IOException Indicates a failure to create the texture on the graphics hardware
     */
    public Texture createTexture(final int width, final int height) throws IOException {
    	return createTexture(width, height, SGL.GL_NEAREST);
    }
    
    /**
     * Create an empty texture
     * 
     * @param width The width of the new texture
     * @param height The height of the new texture
     * @return The created empty texture
     * @throws IOException Indicates a failure to create the texture on the graphics hardware
     */
    public Texture createTexture(final int width, final int height, final int filter) throws IOException {
    	ImageData ds = new EmptyImageData(width, height);
    	
    	return getTexture(ds, filter);
    }
    
    /**
     * Get a texture from an image file. 
     * 
     * @param dataSource The image data to generate the texture from
     * @param filter The filter to use when scaling the texture
     * @return The texture created
     * @throws IOException Indicates the texture is too big for the hardware
     */
    public Texture getTexture(ImageData dataSource, int filter) throws IOException
    { 
    	int target = SGL.GL_TEXTURE_2D;

        ByteBuffer textureBuffer;
    	textureBuffer = dataSource.getImageBufferData();
    	
        // create the texture ID for this texture 
        int textureID = createTextureID(); 
        TextureImpl texture = new TextureImpl("generated:"+dataSource, target ,textureID); 
        
        int minFilter = filter;
        int magFilter = filter;
        boolean flipped = false;
        
        // bind this texture 
        GL.glEnable(target);
        GL.glBindTexture(target, textureID); 
    	
        int width;
        int height;
        int texWidth;
        int texHeight;
        
        ImageData.Format format;
    	
    	width = dataSource.getWidth();
    	height = dataSource.getHeight();
    	format = dataSource.getFormat();
    	
    	texture.setTextureWidth(dataSource.getTexWidth());
    	texture.setTextureHeight(dataSource.getTexHeight());

        texWidth = texture.getTextureWidth();
        texHeight = texture.getTextureHeight();
        
        int srcPixelFormat = format.getOGLType();
        int componentCount = format.getColorComponents();
        
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setImageFormat(format);
        
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL.glGetInteger(SGL.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if ((texWidth > max) || (texHeight > max)) {
        	throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }

        if (holdTextureData) {
        	texture.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, textureBuffer);
        }
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter); 
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter); 
        
        // produce a texture from the byte buffer
        GL.glTexImage2D(target, 
                      0, 
                      dstPixelFormat, 
                      get2Fold(width), 
                      get2Fold(height), 
                      0, 
                      srcPixelFormat, 
                      SGL.GL_UNSIGNED_BYTE, 
                      textureBuffer);
        return texture; 
    } 
    
    /**
     * Get the closest greater power of 2 to the fold number
     * 
     * @param fold The target number
     * @return The power of 2
     */
    public static int get2Fold(int fold) {
    	//new algorithm? -> return 1 << (32 - Integer.numberOfLeadingZeros(n-1));
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    } 
    
    /**
     * Creates an integer buffer to hold specified ints
     * - strictly a utility method
     *
     * @param size how many int to contain
     * @return created IntBuffer
     */
    public static IntBuffer createIntBuffer(int size) {
      ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
      temp.order(ByteOrder.nativeOrder());

      return temp.asIntBuffer();
    }    
    
    /**
     * Reload all the textures loaded in this loader
     */
    public void reload() {
    	Iterator texs = texturesLinear.values().iterator();
    	while (texs.hasNext()) {
    		((TextureImpl) texs.next()).reload();
    	}
    	texs = texturesNearest.values().iterator();
    	while (texs.hasNext()) {
    		((TextureImpl) texs.next()).reload();
    	}
    }

    /**
     * Reload a given texture blob; used internally with setHoldTextureData. 
     * Call TextureImpl.reload instead.
     * 
     * @param texture The texture being reloaded
     * @param srcPixelFormat The source pixel format
     * @param componentCount The component count
     * @param minFilter The minification filter
     * @param magFilter The magnification filter 
     * @param textureBuffer The pixel data 
     * @return The ID of the newly created texture
     */
	public int reload(TextureImpl texture, int srcPixelFormat, int componentCount,
			int minFilter, int magFilter, ByteBuffer textureBuffer) {
    	int target = SGL.GL_TEXTURE_2D;
        int textureID = createTextureID();
        GL.glEnable(target);
        GL.glBindTexture(target, textureID); 
        
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter); 
        GL.glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter); 
        
        // produce a texture from the byte buffer
        GL.glTexImage2D(target, 
                      0, 
                      dstPixelFormat, 
                      texture.getTextureWidth(), 
                      texture.getTextureHeight(), 
                      0, 
                      srcPixelFormat, 
                      SGL.GL_UNSIGNED_BYTE, 
                      textureBuffer);
        return textureID; 
	}
}
