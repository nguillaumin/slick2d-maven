package org.newdawn.slick.tools.hiero.truetype;

/**
 * Simple placeholder for the original logger
 *
 * @author kevin
 */
public class Log {
	/** 
	 * Log an error message 
	 * 
	 * @param msg The message to log
	 */
	public void error(String msg) {
		//System.err.println(msg);
	}

	/** 
	 * Log an debug message 
	 * 
	 * @param msg The message to log
	 */
	public void debug(String msg) {
		//System.err.println(msg);
	}

	/** 
	 * Log an warn message 
	 * 
	 * @param msg The message to log
	 */
	public void warn(String msg) {
		//System.err.println(msg);
	}

	/** 
	 * Log an fatal message 
	 * 
	 * @param msg The message to log
	 */
	public void fatal(String msg) {
		//System.err.println(msg);
	}

	/** 
	 * Log an info message 
	 * 
	 * @param msg The message to log
	 */
	public void info(String msg) {
		//System.err.println(msg);
	}

	/** 
	 * Log an trace message 
	 * 
	 * @param msg The message to log
	 */
	public void trace(String msg) {
		//System.err.println(msg);
	}
	
	/**
	 * Check if the debug reporting is enabled
	 * 
	 * @return True if debug logging is enabled
	 */
	public boolean isDebugEnabled() {
		return true;
	}

	/**
	 * Check if the trace reporting is enabled
	 * 
	 * @return True if trace logging is enabled
	 */
	public boolean isTraceEnabled() {
		return false;
	}
}
