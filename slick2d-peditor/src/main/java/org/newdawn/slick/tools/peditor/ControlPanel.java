package org.newdawn.slick.tools.peditor;

import java.util.HashMap;

import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitter.RandomValue;
import org.newdawn.slick.particles.ConfigurableEmitter.Range;
import org.newdawn.slick.particles.ConfigurableEmitter.SimpleValue;
import org.newdawn.slick.particles.ConfigurableEmitter.Value;

/**
 * The common bits between all the different control panels. This gives a way to link controls
 * to data elements within the emitter. Plumbing it this way saves the effort of writing 
 * bespoke code for each editor type/
 *
 * @author kevin
 */
public abstract class ControlPanel extends DefaultPanel implements InputPanelListener {
	/** A map from visual control to data element */
	protected HashMap controlToData = new HashMap();
	/** A map from name to visual control */
	protected HashMap named = new HashMap();
	/** The emitter being configured */
	protected ConfigurableEmitter emitter;
	/** The offset on the y axis for components */
	protected int yPos;
	
	/**
	 * Create a new panel for controls 
	 */
	public ControlPanel() {
		setLayout(null);
	}
	
	/**
	 * Add a configurable value to the mapping table
	 * 
	 * @param name The name of the control
	 * @param valuePanel The panel used to set the value in the emitter
	 */
	protected void addValue(String name, ValuePanel valuePanel) {
		named.put(name, valuePanel);
		
		valuePanel.setBounds(0,10+yPos,280,63);
		valuePanel.addListener(this);
		add(valuePanel);
		
		yPos+=63;
	}
	
	/**
	 * Add a configurable range panel to the mapping table
	 * 
	 * @param name The name of the control
	 * @param minMax The panel used to set the range in the emitter
	 */
	protected void addMinMax(String name, MinMaxPanel minMax) {
		named.put(name, minMax);
		
		minMax.setBounds(0,10+yPos,280,minMax.getOffset());
		minMax.addListener(this);
		add(minMax);
		
		yPos+=minMax.getOffset();
	}

	/**
	 * Set the emitter to be configured 
	 * 
	 * @param emitter The emitter to be configured
	 */
	public final void setTarget(ConfigurableEmitter emitter) {
		this.emitter = emitter;
		
		linkEmitterToFields(emitter);
	}
	
	/**
	 * Link the fields in the emitter to the panels on this control panel
	 * 
	 * @param emitter The emitter to be configured
	 */
	protected abstract void linkEmitterToFields(ConfigurableEmitter emitter);
	
	/**
	 * Link a emitter configurable range to a named component
	 * 
	 * @param range The configurable range from the emitter
	 * @param name The name of the component to link to
	 */
	protected void link(Range range, String name) {
		link(range, (MinMaxPanel) named.get(name));
	}

	/**
	 * Link a emitter configurable value to a named component
	 * 
	 * @param value The configurable value from the emitter
	 * @param name The name of the component to link to
	 */
	protected void link(Value value, String name) {
		link(value, (ValuePanel) named.get(name));
	}
	
	/**
	 * Link a emitter configurable value to a value panel
	 * 
	 * @param value The configurable value from the emitter
	 * @param panel The component to link against
	 */
	private void link(Value value, ValuePanel panel) {
		controlToData.put(panel, value);
		
		if( value instanceof SimpleValue )
			panel.setValue((int) ((SimpleValue)value).getValue( 0 ));
		else if( value instanceof RandomValue )
			panel.setValue((int) ((RandomValue)value).getValue());
	}

	/**
	 * Link a emitter configurable range to a value panel
	 * 
	 * @param range The configurable range from the emitter
	 * @param panel The component to link against
	 */
	private void link(Range range, MinMaxPanel panel) {
		controlToData.put(panel, range);
		panel.setMax((int) range.getMax());
		panel.setMin((int) range.getMin());
		panel.setEnabledValue(range.isEnabled());
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.InputPanelListener#minMaxUpdated(org.newdawn.slick.tools.peditor.MinMaxPanel)
	 */
	public void minMaxUpdated(MinMaxPanel source) {
		if (emitter == null) {
			return;
		}
		
		Range range = (Range) controlToData.get(source);
		if (range != null) {
			range.setMax(source.getMax());
			range.setMin(source.getMin());
			range.setEnabled(source.getEnabled());
		} else {
			throw new RuntimeException("No data set specified for the GUI source");
		}
	}

	/**
	 * @see org.newdawn.slick.tools.peditor.InputPanelListener#valueUpdated(org.newdawn.slick.tools.peditor.ValuePanel)
	 */
	public void valueUpdated(ValuePanel source) {
		if (emitter == null) {
			return;
		}
		
		Value value = (Value) controlToData.get(source);
		if (value != null) {
			if( value instanceof SimpleValue)
				((SimpleValue)value).setValue( source.getValue());
			else if( value instanceof RandomValue )
				((RandomValue)value).setValue( source.getValue());
		} else {
			throw new RuntimeException("No data set specified for the GUI source");
		}
	}
}
