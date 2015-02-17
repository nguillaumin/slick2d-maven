package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tests.states.TestState1;
import org.newdawn.slick.tests.states.TestState2;
import org.newdawn.slick.tests.states.TestState3;
import org.newdawn.slick.util.Log;

/**
 * A test for pausing states
 * 
 * @author kevin
 */
public class StateBasedPauseTest extends StateBasedGame {
	
	/**
	 * Create a new test
	 */
	public StateBasedPauseTest() {
		super("State Based Test");
	}
	
	/**
	 * @see org.newdawn.slick.state.StateBasedGame#initStatesList(org.newdawn.slick.GameContainer)
	 */
	public void initStatesList(GameContainer container) {
		addState(new TestState1());
		addState(new TestState2());
		addState(new TestState3());
	}
	
	protected void preUpdateState(GameContainer container, int delta) throws SlickException {
		
		// if A is pressed we toggle to update or not update any states. this means you can still switch states but the
		// update method of the current state will never be called.
		if(container.getInput().isKeyPressed(Input.KEY_A)) {
			setUpdatePaused(!isUpdatePaused());
		}
		// same goes for render
		if(container.getInput().isKeyPressed(Input.KEY_S)) {
			setRenderPaused(!isRenderPaused());
		}
		
		// now we just stop the second state. The other will still update
		if(container.getInput().isKeyPressed(Input.KEY_D)) {
			getState(2).setUpdatePaused(!getState(2).isUpdatePaused());
			
		}
		
		// with this we stop rendering on the third state.
		if(container.getInput().isKeyPressed(Input.KEY_F)) {
			getState(3).setRenderPaused(!getState(3).isRenderPaused());
			
		}
	}
	
	protected void postRenderState(GameContainer container, Graphics g) throws SlickException {
		// draw some info
		g.resetTransform();
		g.resetFont();
		g.setColor(Color.white);
		
		g.drawString("Current State:" + getCurrentStateID(), 10, 25);
		
		g.drawString("Press A to pause/unpause update calls on the current state. Paused: " + isUpdatePaused(), 10, container.getHeight() - 100);
		g.drawString("Press S to pause/unpause render calls on the current state. Paused: " + isRenderPaused(), 10, container.getHeight() - 85);
		g.drawString("Press D to pause/unpause update on the state #2. Paused: " + getState(2).isUpdatePaused(), 10, container.getHeight() - 70);
		g.drawString("Press F to pause/unpause render on the state #3. Paused: " + getState(3).isRenderPaused(), 10, container.getHeight() - 55);
	}
	
	/**
	 * Entry point
	 * 
	 * @param argv
	 *            The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new StateBasedPauseTest());
			container.setDisplayMode(800, 600, false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
