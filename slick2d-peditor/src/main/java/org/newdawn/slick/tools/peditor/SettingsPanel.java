package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.newdawn.slick.particles.ConfigurableEmitter;

/**
 * A colletion of controls for the global settings of an emitter
 *
 * @author kevin
 */
public class SettingsPanel extends ControlPanel {
	/** A field for the symbolic name of the emitter */
	private JTextField name;
	/** A field to select the image to be used by the emitter */
	private JTextField imageName;
	/** The list of emitters to be notified when the name is updated */
	private EmitterList list;
	/** Choose used to select image files */
	private JFileChooser chooser = new JFileChooser(new File("."));
	
	/**
	 * Create a new panel for global settings controls
	 * 
	 * @param l The list to be notified when the name changes
	 */
	public SettingsPanel(EmitterList l) {
		setLayout(null);
		
		this.list = l;
		
		JPanel namePanel = new DefaultPanel();
		namePanel.setBorder(BorderFactory.createTitledBorder("Emitter Name"));
		namePanel.setLayout(null);
		name = new JTextField();
		name.setBounds(10,20,260,25);
		namePanel.add(name);
		name.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if (emitter != null) {
					emitter.name = name.getText();
					list.update(emitter);
				}
			}
		});
		
		namePanel.setBounds(0,0,280,55);
		add(namePanel);
		yPos+=55;
		
		JPanel imagePanel = new DefaultPanel();
		imagePanel.setBorder(BorderFactory.createTitledBorder("Particle Image"));
		imagePanel.setLayout(null);
		imageName = new JTextField();
		imageName.setBounds(10,20,185,25);
		imagePanel.add(imageName);
		JButton browse = new JButton("Browse");
		browse.setBounds(200,20,70,25);
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseForImage();
			}
		});
		
		imagePanel.add(browse);
		imagePanel.setBounds(0,55,280,55);
		add(imagePanel);
		yPos+=45;
		
		addValue("gravity",new ValuePanel("Gravity",-200,200,0,"The gravity effect to apply",false));
		addValue("wind",new ValuePanel("Wind",-200,200,0,"The horizontal force effect to apply",false));
	}
	
	/**
	 * Browse for a particle image and set the value into both the emitter and text field
	 * on successful completion
	 */
	private void browseForImage() {
		if (emitter != null) {
			int resp = chooser.showOpenDialog(this);
			if (resp == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				String path = file.getParentFile().getAbsolutePath();
				String name = file.getName();
				
				ConfigurableEmitter.setRelativePath(path);
				emitter.setImageName(name);
				
				imageName.setText(name);
			}
		}
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		name.setText(emitter.name);
		String value = emitter.getImageName();
		if (value != null) {
			value = value.substring(value.lastIndexOf(File.separatorChar)+1);	
			imageName.setText(value);
		}
		
		link(emitter.gravityFactor, "gravity");
		link(emitter.windFactor, "wind");
	}

}
