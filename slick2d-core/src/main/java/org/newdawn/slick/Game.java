package org.newdawn.slick;

/**
 * The main game interface that should be implemented by any game being developed
 * using the container system. There will be some utility type sub-classes as development
 * continues.
 * 
 * @see org.newdawn.slick.BasicGame
 *
 * @author kevin
 * @author tyler
 */
public interface Game {
	void init(GameContainer container);
	void update(GameContainer container, int delta) throws SlickException;
	void render(GameContainer container, Graphics g) throws SlickException;
	boolean closeRequested();
	String getTitle();
	void bindControls();
}
