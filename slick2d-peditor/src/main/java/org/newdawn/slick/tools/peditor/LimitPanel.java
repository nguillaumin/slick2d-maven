package org.newdawn.slick.tools.peditor;

import org.newdawn.slick.particles.ConfigurableEmitter;

/**
 * A colletion of controls for the limiting the emitter
 *
 * @author kevin
 */
public class LimitPanel extends ControlPanel {
	/** The length of the effect */
	private MinMaxPanel lengthPanel;
	/** The number of particles that will be emitted during the effect */
	private MinMaxPanel emitCountPanel;
	
	/**
	 * Create a new panel for limiting controls
	 * 
	 * @param l The list to be notified when the name changes
	 */
	public LimitPanel(EmitterList l) {
		setLayout(null);
		
		lengthPanel = new MinMaxPanel("Effect Length",0,100000,1000,1000,true,-1,"The length the effect will last");
		addMinMax("length", lengthPanel);
		emitCountPanel = new MinMaxPanel("Particle Emission Count",0,100000,1000,1000,true,-1,"The number of particles that will be emitted during the effect");
		addMinMax("emitCount", emitCountPanel);
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		link(emitter.length, "length");
		link(emitter.emitCount, "emitCount");
	}

	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#minMaxUpdated(org.newdawn.slick.tools.peditor.MinMaxPanel)
	 */
	public void minMaxUpdated(MinMaxPanel source) {
		super.minMaxUpdated(source);
		
		if (emitter != null) {
			if (source == lengthPanel) {
				emitter.replay();
			}
			if (source == emitCountPanel) {
				emitter.replay();
			}
		}
	}

}
