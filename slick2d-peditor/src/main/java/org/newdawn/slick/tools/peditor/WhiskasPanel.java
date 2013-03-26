package org.newdawn.slick.tools.peditor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitter.LinearInterpolator;

/**
 * A panel to display the whiskas controls within pedigree GUI
 * 
 * @author void
 */
public class WhiskasPanel extends ControlPanel {
	/** The window used to configure the linear interpolation graphs */
	private GraphEditorWindow editor;
	/** The offset into the panel */
	private int offset = 25;
	/** key: control, value: name of value */
	private HashMap controlToValueName = new HashMap();
	/** key: value name, value: control */
	private HashMap valueNameToControl = new HashMap();
	/** key: name, value: emitter value */
	private HashMap valueMap = new HashMap();

	/**
	 * Create a new panel for limiting controls
	 * 
	 * @param l The list to be notified when the name changes
	 * @param colorPanel The panel controlling the colours that needs to be controlled based on the enablement here
	 * @param emissionControls The panel controlling the emissions that needs to be controlled based on the enablement here
	 */
	public WhiskasPanel(EmitterList l, final ColorPanel colorPanel,
			final EmissionControls emissionControls) {
		setLayout(null);
		setBorder(BorderFactory
				.createTitledBorder("Particle Life Time Gradients"));

		// add checkbox controls for all linear values
		addEnableControl("Alpha", new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				colorPanel.getStartAlpha().setEnabled(
						e.getStateChange() != ItemEvent.SELECTED);
				colorPanel.getEndAlpha().setEnabled(
						e.getStateChange() != ItemEvent.SELECTED);

				itemStateChangedHandler(e);
			}
		});
		addEnableControl("Size", new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				emissionControls.getInitialSize().setEnabledForced(
						e.getStateChange() != ItemEvent.SELECTED);
				itemStateChangedHandler(e);
			}
		});
		addEnableControl("Velocity", new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				itemStateChangedHandler(e);
			}
		});
		addEnableControl("ScaleY", new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				itemStateChangedHandler(e);
			}
		});
	}

	/**
	 * Add a control for enablement
	 * 
	 * @param text The label to be associated with the check box
	 * @param listener The listener to be notified of updates to the new control
	 */
	private void addEnableControl(String text, ItemListener listener) {
		JCheckBox enableControl = new JCheckBox("Enable " + text);
		enableControl.setBounds(10, offset, 200, 20);
		enableControl.addItemListener(listener);
		add(enableControl);

		controlToValueName.put(enableControl, text);
		valueNameToControl.put(text, enableControl);
		offset += 25;
	}

	/**
	 * Notificaiton that one of the configuration option has changed state
	 * 
	 * @param e The event describing the change of state
	 */
	public void itemStateChangedHandler(ItemEvent e) {
		String valueName = (String) controlToValueName.get(e.getSource());
		LinearInterpolator value = (LinearInterpolator) valueMap.get(valueName);

		if (e.getStateChange() == ItemEvent.SELECTED) {
			value.setActive(true);
			editor.registerValue(value, valueName);
		} else {
			value.setActive(false);
			editor.removeValue(valueName);
		}
	}

	/**
	 * Links this whiskas panel to the given editor
	 * 
	 * @param editor The particle editor in use
	 */
	public void setEditor(GraphEditorWindow editor) {
		this.editor = editor;
	}

	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		this.emitter = emitter;

		// register the new emitter
		editor.setLinkedEmitter(emitter);

		valueMap.clear();
		linkToEmitter("Alpha", emitter.alpha);
		linkToEmitter("Size", emitter.size);
		linkToEmitter("Velocity", emitter.velocity);
		linkToEmitter("ScaleY", emitter.scaleY);

		editor.setFirstProperty();
	}

	/**
	 * Link this set of controls to a linear interpolater within the particle emitter
	 * 
	 * @param name The name of the article emitter being linked
	 * @param interpol The interpolator being configured
	 */
	private void linkToEmitter(String name, LinearInterpolator interpol) {
		// put to value map
		valueMap.put(name, interpol);

		// now update the checkbox to represent the state of the given
		// interpolator
		boolean checked = interpol.isActive();
		JCheckBox enableControl = (JCheckBox) valueNameToControl.get(name);
		enableControl.setSelected(false);
		if (checked)
			enableControl.setSelected(checked);
	}
}
