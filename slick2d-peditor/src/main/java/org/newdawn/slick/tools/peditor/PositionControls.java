package org.newdawn.slick.tools.peditor;

import org.newdawn.slick.particles.ConfigurableEmitter;

/**
 * A set of controls for the spawning position of particles from the emitter
 *
 * @author kevin
 */
public class PositionControls extends ControlPanel implements InputPanelListener {
	/** The x offset panel */
	private MinMaxPanel xoffset;
	/** The y offset panel */
	private MinMaxPanel yoffset;
	
	/**
	 * Create a new set of particle spawn poistion controls
	 */
	public PositionControls() {
		setLayout(null);
	
		xoffset = new MinMaxPanel("X Offset",-10000,10000,1,1,"The offset on the x-axis at which particles will appear");
		addMinMax("x", xoffset);
		yoffset = new MinMaxPanel("Y Offset",-10000,10000,1,1,"The offset on the y-axis at which particles will appear");
		addMinMax("y", yoffset);
		addValue("spread", new ValuePanel("Spread Angle (degrees)",0,360,360,"The range of angles the particles can spew out in",false));
		addValue("angularOffset", new ValuePanel("Angular Offset (degrees)",0,360,360,"The direction the particles should spill out at",false));
		addMinMax("initialDistance", new MinMaxPanel("Initial Distance",0,10000,0,0,"The distance from the emitter center particles will appear at"));
	}

	/**
	 * Set the position of the emitter
	 * 
	 * @param x The x position of the emitter
	 * @param y The y position of the emitter
	 */
	public void setPosition(int x,int y) {
		int cx = (xoffset.getMin() + xoffset.getMax()) / 2;
		int cy = (yoffset.getMin() + yoffset.getMax()) / 2;
		
		int dx = x - cx;
		int dy = y - cy;
		
		xoffset.setMin(xoffset.getMin() + dx);
		xoffset.setMax(xoffset.getMax() + dx);
		yoffset.setMin(yoffset.getMin() + dy);
		yoffset.setMax(yoffset.getMax() + dy);
		
		xoffset.fireUpdated(null);
		yoffset.fireUpdated(null);
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		link(emitter.xOffset, "x");
		link(emitter.yOffset, "y");
		link(emitter.spread, "spread");
		link(emitter.angularOffset, "angularOffset");
		link(emitter.initialDistance, "initialDistance");
	}

}
