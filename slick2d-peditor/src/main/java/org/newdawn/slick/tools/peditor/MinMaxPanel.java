package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel allowing the definition of a range of values
 *
 * @author kevin
 */
public class MinMaxPanel extends DefaultPanel {
	/** The spinner for the lower bound */
	private JSpinner minSpinner;
	/** The spinner for the upper bound */
	private JSpinner maxSpinner;
	/** The listeners to be notified of changes */
	private ArrayList listeners = new ArrayList();
	/** Indicates if we should ignore updates */
	private boolean updateDisable = false;
	/** Checkbox for enablement type fields */
	private JCheckBox enabled = new JCheckBox("Enabled");
	/** The value to report when the panel is disabled */
	private int offValue;
	/** True if this panel support enabledment */
	private boolean enablement;
	
	/**
	 * Create a new panel for a range definition
	 * 
	 * @param name The name to display for this panel
	 * @param min The minimum lower bound
	 * @param max The maximum upper bound
	 * @param defMin The default lower bound
	 * @param defMax The default upper bound
	 * @param toolTip The tooltip describing this function
	 */
	public MinMaxPanel(String name, int min, int max, int defMin, int defMax, String toolTip) {
		this(name, min, max, defMin, defMax, false, 0, toolTip);
	}

	/**
	 * Create a new panel for a range definition
	 * 
	 * @param name The name to display for this panel
	 * @param min The minimum lower bound
	 * @param max The maximum upper bound
	 * @param defMin The default lower bound
	 * @param defMax The default upper bound
	 * @param enablement Indicates if this panel supports being enabled
	 * @param offValue The value to report when disabled
	 * @param toolTip The tooltip describing this function
	 */
	public MinMaxPanel(String name, int min, int max, int defMin, int defMax, boolean enablement, int offValue, String toolTip) {
		setLayout(null);

		this.setToolTipText(toolTip);
		this.offValue = offValue;
		this.enablement = enablement;
		int offset = 0;
		if (enablement) {
			enabled.setBounds(10,20,200,20);
			add(enabled);
			offset += 20;
			
			enabled.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					minSpinner.setEnabled(enabled.isSelected());
					maxSpinner.setEnabled(enabled.isSelected());
					fireUpdated(null);
				}
				
			});
		}
		
		minSpinner = new JSpinner(new SpinnerNumberModel(defMin,min,max,1));
		maxSpinner = new JSpinner(new SpinnerNumberModel(defMax,min,max,1));
		
		minSpinner.setBounds(50,20+offset,80,20);
		maxSpinner.setBounds(190,20+offset,80,20);
		
		minSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireUpdated(e.getSource());
			}
		});
		maxSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireUpdated(e.getSource());
			}
		});
		
		JLabel minLabel = new JLabel("Min");
		minLabel.setBounds(10,20+offset,40,20);
		add(minLabel);
		JLabel maxLabel = new JLabel("Max");
		maxLabel.setBounds(150,20+offset,40,20);
		add(maxLabel);
		
		add(minSpinner);
		add(maxSpinner);
		setBorder(BorderFactory.createTitledBorder(name));
		
		if (enablement) {
			minSpinner.setEnabled(false);
			maxSpinner.setEnabled(false);
		}
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.DefaultPanel#setEnabled(boolean)
	 */
	public void setEnabled(boolean e) {
		enabled.setEnabled(e);
		minSpinner.setEnabled(enabled.isSelected() || !enablement);
		maxSpinner.setEnabled(enabled.isSelected() || !enablement);
	}

	/**
	 * Force the state of this component
	 * 
	 * @param e True if we want this component to be enabled
	 */
	public void setEnabledForced(boolean e)
	{
		enabled.setEnabled(e);
		minSpinner.setEnabled(e);
		maxSpinner.setEnabled(e);
	}

	/**
	 * Set the minimum value
	 * 
	 * @param value The value to use as the lower bound
	 */
	public void setMin(int value) {
		updateDisable = true;
		minSpinner.setValue(new Integer(value));
		updateDisable = false;
	}

	/**
	 * Set the maximum value
	 * 
	 * @param value The value to use as the upper bound
	 */
	public void setMax(int value) {
		updateDisable = true;
		maxSpinner.setValue(new Integer(value));
		updateDisable = false;
	}
	
	/**
	 * Add a listener to be notified of changes
	 * 
	 * @param listener The listener to be notified of changes
	 */
	public void addListener(InputPanelListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Get the offset on the y axis for layout
	 * 
	 * @return The offset on the y axis for layout
	 */
	public int getOffset() {
		return enablement ? 65 : 45;
	}
	
	/**
	 * Notify listeners a change has occured on this panel
	 * 
	 * @param source The source of this event
	 */
	void fireUpdated(Object source) {
		if (updateDisable) {
			return;
		}
		
		if (source == maxSpinner) {
			if (getMax() < getMin()) {
				setMin(getMax());
			}
		}
		if (source == minSpinner) {
			if (getMax() < getMin()) {
				setMax(getMin());
			}
		}
		
		for (int i=0;i<listeners.size();i++) {
			((InputPanelListener) listeners.get(i)).minMaxUpdated(this);
		}
	}
	
	/**
	 * Check if this panel is enabled
	 * 
	 * @return True if this panel is enabeld
	 */
	public boolean getEnabled() {
		return !enablement || enabled.isSelected();
	}
	
	/**
	 * Indicate if this panel should be enabled
	 * 
	 * @param e True if this panel option should be enabled
	 */
	public void setEnabledValue(boolean e) {
		if (enablement) {
			enabled.setSelected(e);
		}
		minSpinner.setEnabled(enabled.isSelected() || !enablement);
		maxSpinner.setEnabled(enabled.isSelected() || !enablement);
	}
	
	/**
	 * Get the defined upper bound
	 * 
	 * @return The defined upper bound
	 */
	public int getMax() {
		if ((enablement) && (!enabled.isSelected())) {
			return offValue;
		}
		
		return ((Integer) maxSpinner.getValue()).intValue();
	}

	/**
	 * Get the defined lower bound
	 * 
	 * @return The defined lower bound
	 */
	public int getMin() {
		if ((enablement) && (!enabled.isSelected())) {
			return offValue;
		}
		
		return ((Integer) minSpinner.getValue()).intValue();
	}
}
