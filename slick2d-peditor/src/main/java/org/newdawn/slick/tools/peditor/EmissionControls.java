package org.newdawn.slick.tools.peditor;

import org.newdawn.slick.particles.ConfigurableEmitter;

/**
 * A panel to control has the particles are initially emitted from the source and what 
 * the range of their initial values are
 *
 * @author kevin
 */
public class EmissionControls extends ControlPanel {
	/** The panel controlling the initial size of the particles */
	private MinMaxPanel initialSize;
	
	/**
	 * Create a new panel to control emission settings
	 */
	public EmissionControls() {
		setLayout(null);
	
		addMinMax("spawnInterval", new MinMaxPanel("Spawn Interval (ms)",1,100000,100,100, "The interval between the production of particles"));
		addMinMax("spawnCount", new MinMaxPanel("Spawn Count (# of particles)",1,100,1,1, "The number of particles created each spawn"));
		initialSize= new MinMaxPanel("Initial Size",1,100000,1,1,"The initial size of the produced particles");
		addMinMax("initialSize", initialSize);
		addMinMax("initialLife", new MinMaxPanel("Initial Life (ms)",1,100000,1,1,"The lifetime the particles will exist for in milliseconds"));
		addMinMax("speed", new MinMaxPanel("Particle Speed",-10000,10000,0,0,"The speed at which the particles will come out of the emitter"));
		addValue("growth", new ValuePanel("Growth Factor",-200,200,0,"The amount and sign of the growth particles will undergo during their lifetime",false));
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		link(emitter.spawnInterval, "spawnInterval");
		link(emitter.spawnCount, "spawnCount");
		link(emitter.initialSize, "initialSize");
		link(emitter.initialLife, "initialLife");
		link(emitter.speed, "speed");
		link(emitter.growthFactor, "growth");
	}

	/**
	 * Get the panel displaying the initial size of the particles
	 * 
	 * @return The panel displaying the initial size
	 */
	public MinMaxPanel getInitialSize() {
		return initialSize;
	}
}
