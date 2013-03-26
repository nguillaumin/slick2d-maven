package org.newdawn.slick.tools.hiero.truetype;

/**
 * A listener to be notified of font loading status
 *
 * @author kevin
 */
public interface StatusListener {
	/**
	 * Update the status message
	 * 
	 * @param msg The message to be displayed
	 */
	public void updateStatus(String msg);
}
