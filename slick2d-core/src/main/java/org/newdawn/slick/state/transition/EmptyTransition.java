package org.newdawn.slick.state.transition;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A transition that has no effect and instantly finishes. Used as a utility for the people
 * not using transitions
 *
 * @author kevin
 */
public class EmptyTransition implements Transition {
	public boolean isComplete() {
		return true;
	}

	public void postRender(StateBasedGame game, GameContainer container, Graphics g) {
		// no op
	}

	public void preRender(StateBasedGame game, GameContainer container, Graphics g) {
		// no op
	}

	public void update(StateBasedGame game, GameContainer container, int delta) {
		// no op
	}

	public void init(GameState firstState, GameState secondState) {
		// TODO Auto-generated method stub
		
	}
}
