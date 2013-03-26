package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A panel to allow editing on a single configurable value
 *
 * @author kevin
 */
public class ValuePanel extends DefaultPanel {
	/** The name given for the particlar value */
	private String name;
	/** The slider to set the value */
	private JSlider slider;
	/** The list of listeners to be notified of updates */
	private ArrayList listeners = new ArrayList();
	/** The button to set this value as linear */
	private JCheckBox linear = new JCheckBox();
	
	/**
	 * Create a new value panel for a single configurable emitter setting
	 * 
	 * @param name The name of the value
	 * @param min The minimum value allowed
	 * @param max The maximum value allowed
	 * @param value The initial value
	 * @param toolTip The description of the setting
	 * @param linearEnabled True if this range should be able to be set to linear
	 */
	public ValuePanel(String name, int min, int max, int value, String toolTip, boolean linearEnabled) {
		setLayout(null);
		
		setToolTipText(toolTip);
		
		int offset = 0;
		
		if (linearEnabled) {
			JLabel label = new JLabel("Linear");
			label.setBounds(242,15,30,20);
			add(label);
			linear.setBounds(245,25,30,30);
			linear.setOpaque(false);
			add(linear);
			offset = 30;
			linear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireUpdated(e.getSource());
				}
			});
		}
		
		this.name = name;
		slider = new JSlider(min, max, value);
		slider.setBounds(10,20,260-offset,40);
		slider.setFocusable(false);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing((max-min) / 3);
		slider.setMinorTickSpacing((max-min) / 10);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireUpdated(e.getSource());
			}
		});
		
		add(slider);
		setBorder(BorderFactory.createTitledBorder(name));
	}
	
	/**
	 * Indicate if this value is set to linear random numbers
	 * 
	 * @param linear True if this vlaue is set to give linear random numbers
	 */
	public void setLinear(boolean linear) {
		this.linear.setSelected(linear);
	}
	
	/**
	 * True if this value should give linear random numbers
	 * 
	 * @return True if this value should give linear random numbers
	 */
	public boolean isLinear() {
		return linear.isSelected();
	}
	
	/**
	 * Set the new value
	 * 
	 * @param value The value to be assigned
	 */
	public void setValue(int value) {
		slider.setValue(value);
	}
	
	/**
	 * Get the current value
	 * 
	 * @return The current value
	 */
	public int getValue() {
		return slider.getValue();
	}
	
	/**
	 * Add a listener to be notified of updates to this panel
	 * 
	 * @param listener The listener to be notified of updates to this panel
	 */
	public void addListener(InputPanelListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Fire notification of updates to all listeners
	 * 
	 * @param source The source of the update
	 */
	private void fireUpdated(Object source) {
		for (int i=0;i<listeners.size();i++) {
			((InputPanelListener) listeners.get(i)).valueUpdated(this);
		}
	}
}
