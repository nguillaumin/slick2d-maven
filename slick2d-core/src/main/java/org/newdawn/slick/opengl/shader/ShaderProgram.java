package org.newdawn.slick.opengl.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * A simple wrapper utility for creating and reusing shaders and shader programs.
 * 
 * @author davedes
 */
public class ShaderProgram {

	/** The vertex shader type (GL20.GL_VERTEX_SHADER). */
	public static final int VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
	/** The fragment shader type (GL20.GL_FRAGMENT_SHADER). */
	public static final int FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;
	
	private static boolean strict = true;
	
	/**
	 * Returns true if GLSL shaders are supported in hardware on this system. This checks for
	 * the following OpenGL extensions: GL_ARB_shader_objects, GL_ARB_vertex_shader,
	 * GL_ARB_fragment_shader
	 * 
	 * @return true if shaders are supported
	 */
	public static boolean isSupported() {
		ContextCapabilities c = GLContext.getCapabilities();
		return c.GL_ARB_shader_objects && c.GL_ARB_vertex_shader && c.GL_ARB_fragment_shader;
	}
	
	/**
	 * Whether shader programs are to use "strict" uniform/attribute name
	 * checking. That is, when strict mode is enabled, trying to modify or retrieve uniform/attribute
	 * data by name will fail and throw an IllegalArgumentException if there exists no
	 * 'active' uniforms/attributes by the given name. (In GLSL, declared uniforms might still be
	 * "inactive" if they are not used.) If strict mode is disabled, getting/setting uniform/attribute
	 * data will fail silently if the name is not found.
	 * @param enabled true to enable strict mode
	 */
	public static void setStrictMode(boolean enabled) {
		strict = enabled;
	}
	
	/**
	 * Returns <tt>true</tt> if shader programs are to use "strict" uniform/attribute name
	 * checking. That is, when strict mode is enabled, trying to modify or retrieve uniform/attribute
	 * data by name will fail and throw an IllegalArgumentException if there exists no
	 * 'active' uniforms/attributes by the given name. (In GLSL, declared uniforms might still be
	 * "inactive" if they are not used.) If strict mode is disabled, getting/setting uniform/attribute
	 * data will fail silently if the name is not found.
	 * @return true if strict mode is enabled
	 */
	public static boolean isStrictMode() {
		return strict;
	}
	
	/**
	 * Disables shaders.
	 */
	public static void unbindAll() {
		ARBShaderObjects.glUseProgramObjectARB(0);
	}
	
	/** The OpenGL handle for this shader program object. */
	protected int program;
	/** The log for this program. */
	protected String log = "";
	/** A map of uniforms by <name, int>. */
	protected HashMap<String, Integer> uniforms = new HashMap<String, Integer>();
	/** A map of attributes by <name, int>. */
	protected HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	/** The vertex shader source. */
	protected String vertShaderSource;
	/** The fragment shader source. */
	protected String fragShaderSource;
	/** The OpenGL handle for this program's vertex shader object. */
	protected int vert;
	/** The OpenGL handle for this program's fragment shader object. */
	protected int frag;
	
	private FloatBuffer buf4;
	private IntBuffer ibuf4;
	
	/**
	 * A convenience method to load a ShaderProgram from two text files.
	 * @param vertFile the location of the vertex shader source
	 * @param fragFile the location of the frag shader source
	 * @return the compiled and linked ShaderProgram
	 * @throws SlickException if there was an issue reading the file, compiling the source,
	 * 				or linking the program
	 */
	public static ShaderProgram loadProgram(String vertFile, String fragFile) throws SlickException {
		return new ShaderProgram(readFile(vertFile), readFile(fragFile));
	}
	
	/**
	 * Loads the given input stream into a source code string.
	 * @param ref the location of the text file
	 * @return the resulting source code String 
	 * @throws SlickException if there was an issue reading the source
	 */
	public static String readFile(String ref) throws SlickException {
		InputStream in = ResourceLoader.getResourceAsStream(ref);
		try { return readFile(in); }
		catch (SlickException e) { 
			throw new SlickException("could not load source file: "+ref);
		}
	}
	
	/**
	 * Loads the given input stream into a source code string.
	 * @param in the input stream
	 * @return the resulting source code String 
	 * @throws SlickException if there was an issue reading the source
	 * @author Nitram
	 */
    public static String readFile(InputStream in) throws SlickException {
		try {
			final StringBuffer sBuffer = new StringBuffer();
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));
			final char[] buffer = new char[1024];

			int cnt;
			while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
				sBuffer.append(buffer, 0, cnt);
			}
			br.close();
			return sBuffer.toString();
		} catch (IOException e) {
			throw new SlickException("could not load source file");
		}
	}
    

    /**
     * Creates a new shader program with the given vertex and fragment shader
     * source code. The given source code is compiled, then the shaders attached
     * and linked. 
     * 
     * If shaders are not supported on this system (isSupported returns false), 
     * a SlickException will be thrown.
     * 
     * If one of the shaders does not compile successfully, a SlickException will be thrown.
     * 
     * If there was a problem in linking the shaders to the program, a SlickException will
     * be thrown and the program will be deleted.
     * 
     * @param vertexShaderSource the shader code to compile, attach and link
     * @param fragShaderSource the frag code to compile, attach and link
     * @throws SlickException if there was an issue
     * @throws IllegalArgumentException if there was an issue
     */
    public ShaderProgram(String vertexShaderSource, String fragShaderSource) throws SlickException {
    	if (vertexShaderSource==null || fragShaderSource==null) 
			throw new IllegalArgumentException("shader source must be non-null");
    	if (!isSupported())
			throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
		
    	this.vertShaderSource = vertexShaderSource;
    	this.fragShaderSource = fragShaderSource;
    	vert = compileShader(VERTEX_SHADER, vertexShaderSource);
    	frag = compileShader(FRAGMENT_SHADER, fragShaderSource);
		program = createProgram();
		try {
			linkProgram();
		} catch (SlickException e) {
			release();
			throw e;
		}
		if (log!=null && log.length()!=0)
			Log.warn("GLSL Info: "+log);
    }
    
	/**
	 * Subclasses may wish to implement this to manually handle program/shader creation, compiling, and linking.
	 * This constructor does nothing; users will need to call compileShader, createProgram and linkProgram manually.
	 * @throws SlickException
	 */
	protected ShaderProgram() {
	}
	
	/**
	 * Creates a shader program and returns its OpenGL handle. If the result is zero, an exception will be thrown.
	 * @return the OpenGL handle for the newly created shader program
	 * @throws SlickException if the result is zero
	 */
	protected int createProgram() throws SlickException {
		if (!isSupported())
			throw new SlickException("no shader support found; driver does not support extension GL_ARB_shader_objects");
		int program = ARBShaderObjects.glCreateProgramObjectARB();
		if (program == 0)
			throw new SlickException("could not create program; check ShaderProgram.isSupported()");
		return program;
	}
	
	private String shaderTypeString(int type) {
		if (type==FRAGMENT_SHADER) return "FRAGMENT_SHADER";
		else if (type==VERTEX_SHADER) return "VERTEX_SHADER";
		else if (type==GL32.GL_GEOMETRY_SHADER) return "GEOMETRY_SHADER";
		else return "shader";
	}
	
	/**
	 * Compiles a shader from source and returns its handle. If the compilation failed, 
	 * a SlickException will be thrown. If the compilation had error, info or warnings messages,
	 * they will be appended to this program's log.
	 *  
	 * @param type the type to use in compilation
	 * @param source the source code to compile
	 * @return the resulting ID
	 * @throws SlickException if compilation was unsuccessful
	 */
	protected int compileShader(int type, String source) throws SlickException {
		int shader = ARBShaderObjects.glCreateShaderObjectARB(type);
		if (shader==0) 
			throw new SlickException("could not create shader object; check ShaderProgram.isSupported()");
		ARBShaderObjects.glShaderSourceARB(shader, source);
		ARBShaderObjects.glCompileShaderARB(shader);
		int comp = ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_COMPILE_STATUS);
		int len = ARBShaderObjects.glGetObjectParameteriARB(shader, GL20.GL_INFO_LOG_LENGTH);
		String t = shaderTypeString(type);
		String err = ARBShaderObjects.glGetInfoLogARB(shader, len); 
		if (err!=null&&err.length()!=0) 
			log += t+" compile log:\n"+err+"\n";
		if (comp==GL11.GL_FALSE)
			throw new SlickException(log);
		return shader;
	}
	
	/**
	 * Called to attach vertex and fragment; users may override this for more specific purposes.
	 */
	protected void attachShaders() {
		ARBShaderObjects.glAttachObjectARB(getID(), vert);
		ARBShaderObjects.glAttachObjectARB(getID(), frag);
	}
	
	/**
	 * Calls attachShaders and links the program.
	 * 
	 * @throws SlickException
	 *             if this program is invalid (released) or
	 *             if the link was unsuccessful
	 */
	protected void linkProgram() throws SlickException {
		if (!valid())
			throw new SlickException("trying to link an invalid (i.e. released) program");
		
		uniforms.clear();
		attributes.clear();
		
		attachShaders();

        ARBShaderObjects.glLinkProgramARB(program);
        int comp = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_LINK_STATUS);
        int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_INFO_LOG_LENGTH);
		String err = ARBShaderObjects.glGetInfoLogARB(program, len);
		if (err!=null&&err.length()!=0) log = err + "\n" + log;
		if (log!=null) log = log.trim();
        if (comp==GL11.GL_FALSE) 
			throw new SlickException(log);
        
        fetchUniforms();
		fetchAttributes();
	}
	
	/**
	 * Returns the full log of compiling/linking errors, info, warnings, etc.
	 * @return the full log of this ShaderProgram
	 */
	public String getLog() {
		return log;
	}
	
	/**
	 * Enables this shader for use -- only one shader can be bound at a time. Calling
	 * bind() when another program is bound will simply make this object the active program.
	 * @throw IllegalStateException if this program is invalid
	 */
	public void bind() {
		if (!valid())
			throw new IllegalStateException("trying to enable a program that is not valid");
		ARBShaderObjects.glUseProgramObjectARB(program);
	}

	/**
	 * Unbinds all shaders; this is the equivalent of ShaderProgram.unbindAll(), and only included
	 * for consistency with bind() and the rest of the API (i.e. startUse/endUse). Users do not need to unbind
	 * one shader before binding a new one.
	 */
	public void unbind() {
		ShaderProgram.unbindAll();
	}
	
	/**
	 * Disables shaders (unbind), then detaches and releases the shaders associated with this program. 
	 * This can be called after linking a program in order to free up memory (as the shaders are no longer needed),
	 * however, since it is not a commonly used feature and thus not well tested on all drivers,
	 * it should be used with caution.
	 * Shaders shouldn't be used after being released.
	 */
	public void releaseShaders() {
		unbind();
		if (vert!=0) {
			ARBShaderObjects.glDetachObjectARB(getID(), vert);
			ARBShaderObjects.glDeleteObjectARB(vert);
			vert = 0;
		}
		if (frag!=0) {
			ARBShaderObjects.glDetachObjectARB(getID(), frag);
			ARBShaderObjects.glDeleteObjectARB(frag);
			frag = 0;
		}
	}
	
	/**
	 * If this program has not yet been released, this will disable shaders (unbind), 
	 * then releases this program and its shaders. To only release
	 * the shaders (not the program itself), call releaseShaders().
	 * Programs will be considered "invalid" after being released, and should no longer be used.
	 */
	public void release() {
		if (program!=0) {
			unbind();
			releaseShaders();
			ARBShaderObjects.glDeleteObjectARB(program);
			program = 0;
		}
	}
	
	/**
	 * Returns the OpenGL handle for this program's vertex shader.
	 * @return the vertex ID
	 */
	public int getVertexShaderID() {
		return vert;
	}

	/**
	 * Returns the OpenGL handle for this program's fragment shader.
	 * @return the fragment ID
	 */
	public int getFragmentShaderID() {
		return frag;
	}

	/**
	 * Returns the source code for the vertex shader.
	 * @return the source code
	 */
	public String getVertexShaderSource() {
		return vertShaderSource;
	}

	/**
	 * Returns the source code for the fragment shader.
	 * @return the source code
	 */
	public String getFragmentShaderSource() {
		return fragShaderSource;
	}
	
	/**
	 * Returns the OpenGL handle for this shader program
	 * @return the program ID
	 */
	public int getID() {
		return program;
	}
	
	/**
	 * A shader program is "valid" if it's ID is not zero. Upon
	 * releasing a program, the ID will be set to zero. 
	 * 
	 * @return whether this program is valid
	 */
	public boolean valid() {
		return program != 0;
	}
	
	private void fetchUniforms() {
		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_UNIFORMS);
		//max length of all uniforms stored in program
		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
		
		for (int i=0; i<len; i++) {
			String name = ARBShaderObjects.glGetActiveUniformARB(program, i, strLen);
			int id = ARBShaderObjects.glGetUniformLocationARB(program, name);
			uniforms.put(name, id);
		}
	}
	
	private void fetchAttributes() {
		int len = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTES);
		//max length of all attributes stored in program
		int strLen = ARBShaderObjects.glGetObjectParameteriARB(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH); 
		for (int i=0; i<len; i++) {
			String name = ARBVertexShader.glGetActiveAttribARB(program, i, strLen);
			int id = ARBVertexShader.glGetAttribLocationARB(program, name);
			attributes.put(name, id);
		}
	}

	/**
	 * Returns the ID of the given uniform.
	 * @param name the uniform name
	 * @return the ID (location) in the shader program
	 */
	public int getUniformID(String name) {
		Integer locI = uniforms.get(name);
		int location = locI==null ? -1 : locI.intValue();
		if (location!=-1)
			return location;
		location = ARBShaderObjects.glGetUniformLocationARB(program, name);
		if (location == -1 && strict)
			throw new IllegalArgumentException("no active uniform by name '"+name+"' (disable strict compiling to suppress warnings)");
		uniforms.put(name, location); 
		return location;
	}

	/**
	 * Returns the ID of the given attribute.
	 * @param name the attribute name
	 * @return the ID (location) in the shader program
	 */
	public int getAttributeID(String name) {
		int location = attributes.get(name);
		if (location!=-1)
			return location;
		location = ARBVertexShader.glGetAttribLocationARB(program, name);
		if (location == -1 && strict)
			throw new IllegalArgumentException("no active attribute by name '"+name+"'");
		attributes.put(name, location); 
		return location;
	}

	/**
	 * Returns the names of all active attributes that were found
	 * when linking the program.
	 * @return an array list of active attribute names
	 */
	public String[] getAttributes() {
		return attributes.keySet().toArray(new String[attributes.size()]);
	}
	
	/**
	 * Returns the names of all active uniforms that were found
	 * when linking the program.
	 * @return an array list of active uniform names
	 */
	public String[] getUniformNames() {
		return uniforms.keySet().toArray(new String[uniforms.size()]);
	}
	
	/**
	 * Enables the vertex array -- in strict mode, if the vertex attribute
	 * is not found (or it's inactive), an IllegalArgumentException will
	 * be thrown. If strict mode is disabled and the vertex attribute 
	 * is not found, this method will return <tt>false</tt> otherwise it
	 * will return <tt>true</tt>.
	 * 
	 * @param name the name of the vertex attribute to enable
	 * @return false if strict mode is disabled and this attribute couldn't be found
	 */
	public boolean enableVertexAttribute(String name) {
		int id = getAttributeID(name);
		if (id==-1) return false;
		ARBVertexShader.glEnableVertexAttribArrayARB(id);
		return true;
	}
	
	/**
	 * Disables the vertex array -- in strict mode, if the vertex attribute
	 * is not found (or it's inactive), an IllegalArgumentException will
	 * be thrown. If strict mode is disabled and the vertex attribute 
	 * is not found, this method will return <tt>false</tt> otherwise it
	 * will return <tt>true</tt>.
	 * 
	 * @param name the name of the vertex attribute to disable
	 * @return false if strict mode is disabled and this attribute couldn't be found
	 */
	public boolean disableVertexAttribute(String name) {
		int id = getAttributeID(name);
		if (id==-1) return false;
		ARBVertexShader.glDisableVertexAttribArrayARB(id);
		return true;
	}
	
//	public void setVertexAttribute(String name, int size, int type, boolean normalize, int stride, FloatBuffer buffer) {
//		ARBVertexShader.glVertexAttrib
//	}
	
	/**
	 * Sets the value of an RGBA vec4 uniform to the given color
	 * @param name the RGBA vec4 uniform
	 * @param color the color to assign
	 */
	public void setUniform4f(String name, Color color) {
		setUniform4f(name, color.r, color.g, color.b, color.a);
	}
	
	/**
	 * Sets the value of a vec2 uniform to the given Vector2f.
	 * @param name the vec2 uniform
	 * @param vec the vector to use
	 */
	public void setUniform2f(String name, Vector2f vec) {
		setUniform2f(name, vec.x, vec.y);
	}
	
	private FloatBuffer uniformf(String name) {
		if (buf4==null)
			buf4 = BufferUtils.createFloatBuffer(4);
		buf4.clear();
		getUniform(name, buf4);
		return buf4;
	}
	
	private IntBuffer uniformi(String name) {
		//TODO: add setters/getters for ivec2, ivec3, ivec4
		if (ibuf4==null)
			ibuf4 = BufferUtils.createIntBuffer(4);
		ibuf4.clear();
		getUniform(name, ibuf4);
		return ibuf4;
	}

	/**
	 * A convenience method to retrieve an integer/sampler2D uniform.
	 * @param name the uniform name
	 * @return the value
	 */
	public int getUniform1i(String name) {
		return uniformi(name).get(0);
	}

	/**
	 * A convenience method to retrieve an ivec2 uniform;
	 * for maximum performance and memory efficiency you 
	 * should use getUniform(String, IntBuffer) with a shared
	 * buffer.
	 * @param name the name of the uniform
	 * @return a newly created int[] array with 2 elements; e.g. (x, y)
	 */
	public int[] getUniform2i(String name) {
		IntBuffer buf = uniformi(name);
		return new int[] { buf.get(0), buf.get(1) };
	}

	/**
	 * A convenience method to retrieve an ivec3 uniform;
	 * for maximum performance and memory efficiency you 
	 * should use getUniform(String, IntBuffer) with a shared
	 * buffer.
	 * @param name the name of the uniform
	 * @return a newly created int[] array with 3 elements; e.g. (x, y, z)
	 */
	public int[] getUniform3i(String name) {
		IntBuffer buf = uniformi(name);
		return new int[] { buf.get(0), buf.get(1), buf.get(2) };
	}

	/**
	 * A convenience method to retrieve an ivec4 uniform;
	 * for maximum performance and memory efficiency you 
	 * should use getUniform(String, IntBuffer) with a shared
	 * buffer.
	 * @param name the name of the uniform
	 * @return a newly created int[] array with 2 elements; e.g. (r, g, b, a)
	 */
	public int[] getUniform4i(String name) {
		IntBuffer buf = uniformi(name);
		return new int[] { buf.get(0), buf.get(1), buf.get(2), buf.get(3) };
	}
	
	/**
	 * A convenience method to retrieve a float uniform.
	 * @param name the uniform name
	 * @return the value
	 */
	public float getUniform1f(String name) {
		return uniformf(name).get(0);
	}
	
	/**
	 * A convenience method to retrieve a vec2 uniform;
	 * for maximum performance and memory efficiency you 
	 * should use getUniform(String, FloatBuffer) with a shared
	 * buffer.
	 * @param name the name of the uniform
	 * @return a newly created float[] array with 2 elements; e.g. (x, y)
	 */
	public float[] getUniform2f(String name) {
		FloatBuffer buf = uniformf(name);
		return new float[] { buf.get(0), buf.get(1) };
	}

	/**
	 * A convenience method to retrieve a vec3 uniform;
	 * for maximum performance and memory efficiency you 
	 * should use getUniform(String, FloatBuffer) with a shared
	 * buffer.
	 * @param name the name of the uniform
	 * @return a newly created float[] array with 3 elements; e.g. (x, y, z)
	 */
	public float[] getUniform3f(String name) {
		FloatBuffer buf = uniformf(name);
		return new float[] { buf.get(0), buf.get(1), buf.get(2) };
	}

	/**
	 * A convenience method to retrieve a vec4 uniform;
	 * for maximum performance and memory efficiency you 
	 * should use getUniform(String, FloatBuffer) with a shared
	 * buffer.
	 * @param name the name of the uniform
	 * @return a newly created float[] array with 4 elements; e.g. (r, g, b, a)
	 */
	public float[] getUniform4f(String name) {
		FloatBuffer buf = uniformf(name);
		return new float[] { buf.get(0), buf.get(1), buf.get(2), buf.get(3) };
	}
	
	/**
	 * Retrieves data from a uniform and places it in the given buffer. If 
	 * strict mode is enabled, this will throw an IllegalArgumentException
	 * if the given uniform is not 'active' -- i.e. if GLSL determined that
	 * the shader isn't using it. If strict mode is disabled, this method will
	 * return <tt>true</tt> if the uniform was found, and <tt>false</tt> otherwise.
	 * 
	 * @param name the name of the uniform
	 * @param buf the buffer to place the data
	 * @return true if the uniform was found, false if there is no active uniform by that name
	 */
	public boolean getUniform(String name, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return false;
		ARBShaderObjects.glGetUniformARB(program, id, buf);
		return true;
	}
	
	/**
	 * Retrieves data from a uniform and places it in the given buffer. If 
	 * strict mode is enabled, this will throw an IllegalArgumentException
	 * if the given uniform is not 'active' -- i.e. if GLSL determined that
	 * the shader isn't using it. If strict mode is disabled, this method will
	 * return <tt>true</tt> if the uniform was found, and <tt>false</tt> otherwise.
	 * 
	 * @param name the name of the uniform
	 * @param buf the buffer to place the data
	 * @return true if the uniform was found, false if there is no active uniform by that name
	 */
	public boolean getUniform(String name, IntBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return false;
		ARBShaderObjects.glGetUniformARB(program, id, buf);
		return true;
	}
	
	/**
	 * Whether the shader program was linked with the active uniform by the given name. A
	 * uniform might be "inactive" even if it was declared at the top of a shader;
	 * if GLSL finds that a uniform isn't needed (i.e. not used in shader), then
	 * it will not be active.
	 * @param name the name of the uniform
	 * @return true if this shader program could find the active uniform
	 */
	public boolean hasUniform(String name) {
		return uniforms.containsKey(name);
	}
	
	/**
	 * Whether the shader program was linked with the active attribute by the given name. A
	 * attribute might be "inactive" even if it was declared at the top of a shader;
	 * if GLSL finds that a attribute isn't needed (i.e. not used in shader), then
	 * it will not be active.
	 * @param name the name of the attribute
	 * @return true if this shader program could find the active attribute
	 */
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	/**
	 * Sets the value of a float uniform.
	 * @param name the uniform by name
	 * @param f the float value
	 */
	public void setUniform1f(String name, float f) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform1fARB(id, f);
	}
	
	/**
	 * Sets the value of a sampler2D uniform.
	 * @param name the uniform by name
	 * @param i the integer / active texture (e.g. 0 for TEXTURE0)
	 */
	public void setUniform1i(String name, int i) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform1iARB(id, i);
	}
	
	/**
	 * Sets the value of a vec2 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / tex.s
	 * @param b vec.y / tex.t
	 */
	public void setUniform2f(String name, float a, float b) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform2fARB(id, a, b);
	}
	
	/**
	 * Sets the value of a vec3 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r / tex.s
	 * @param b vec.y / color.g / tex.t
	 * @param c vec.z / color.b / tex.p
	 */
	public void setUniform3f(String name, float a, float b, float c) {
		int id = getUniformID(name);
		if (id==-1) return;
		
		ARBShaderObjects.glUniform3fARB(id, a, b, c);
	}

	/**
	 * Sets the value of a vec4 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r
	 * @param b vec.y / color.g
	 * @param c vec.z / color.b 
	 * @param d vec.w / color.a 
	 */
	public void setUniform4f(String name, float a, float b, float c, float d) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform4fARB(id, a, b, c, d);
	}
	
	/**
	 * Sets the value of a ivec2 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / tex.s
	 * @param b vec.y / tex.t
	 */
	public void setUniform2i(String name, int a, int b) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform2iARB(id, a, b);
	}

	/**
	 * Sets the value of a ivec3 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r
	 * @param b vec.y / color.g
	 * @param c vec.z / color.b 
	 */
	public void setUniform3i(String name, int a, int b, int c) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform3iARB(id, a, b, c);
	}
	
	/**
	 * Sets the value of a ivec4 uniform.
	 * @param name the uniform by name
	 * @param a vec.x / color.r
	 * @param b vec.y / color.g
	 * @param c vec.z / color.b 
	 * @param d vec.w / color.a 
	 */
	public void setUniform4i(String name, int a, int b, int c, int d) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniform4iARB(id, a, b, c, d);
	}
	
	/**
	 * Sets a uniform matrix2 with the given name and transpose.
	 * @param name the name to use
	 * @param transpose whether to transpose the matrix
	 * @param buf the buffer representing the matrix2
	 */
	public void setMatrix2(String name, boolean transpose, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniformMatrix2ARB(id, transpose, buf);
	}

	/**
	 * Sets a uniform matrix3 with the given name and transpose.
	 * @param name the name to use
	 * @param transpose whether to transpose the matrix
	 * @param buf the buffer representing the matrix3
	 */
	public void setMatrix3(String name, boolean transpose, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniformMatrix3ARB(id, transpose, buf);
	}

	/**
	 * Sets a uniform matrix4 with the given name and transpose.
	 * @param name the name to use
	 * @param transpose whether to transpose the matrix
	 * @param buf the buffer representing the matrix4
	 */
	public void setMatrix4(String name, boolean transpose, FloatBuffer buf) {
		int id = getUniformID(name);
		if (id==-1) return;
		ARBShaderObjects.glUniformMatrix4ARB(id, transpose, buf);
	}
}
