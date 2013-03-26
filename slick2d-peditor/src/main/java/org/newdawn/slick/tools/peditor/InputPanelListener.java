package org.newdawn.slick.tools.peditor;

/**
 * A listener to be notified of changes to the editor specific control panels
 *
 * @author kevin
 */
public interface InputPanelListener {
	/** 
	 * Notification a min max panel was updated
	 * 
	 * @param source The panel that was updated
	 */
	public void minMaxUpdated(MinMaxPanel source);

	/** 
	 * Notification a value panel was updated
	 * 
	 * @param source The panel that was updated
	 */
	public void valueUpdated(ValuePanel source);
}
