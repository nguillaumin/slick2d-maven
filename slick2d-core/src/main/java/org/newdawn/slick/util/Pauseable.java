package org.newdawn.slick.util;

/**
 * Interface for a pauseable state
 * 
 * @author regiden
 */
public interface Pauseable {
	
	/**
	 * pauses update.
	 */
	public void pauseUpdate();
	/**
	 * pauses render.
	 */
	public void pauseRender();
	/**
	 * unpauses update.
	 */
	public void unpauseUpdate();
	/**
	 * unpauses update.
	 */
	public void unpauseRender();
	
	/**
	 * @return true if update is paused.
	 */
	public boolean isUpdatePaused();
	/**
	 * @return true if render is paused.
	 */
	public boolean isRenderPaused();
	
	/**
	 * @param pause true if update should be paused.
	 */
	public void setUpdatePaused(boolean pause);
	/**
	 * @param pause true if render should be paused.
	 */
	public void setRenderPaused(boolean pause);
	
}
