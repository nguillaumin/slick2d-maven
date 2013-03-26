package org.newdawn.slick.tools.peditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.newdawn.slick.particles.ConfigurableEmitter;

/**
 * A visual list of the emitter being editing
 *
 * @author kevin
 */
public class EmitterList extends JPanel {
	/** The actual list of emitters */
	private DefaultListModel emitters = new DefaultListModel();
	/** The visual list of controllable emitters */
	private JList list = new JList(emitters);
	/** Button to add an emitter to the list */
	private JButton add = new JButton("Add");
	/** Button to remove an emitter from the list */
	private JButton remove = new JButton("Remove");
	/** The map from emitter to JCheckBox in the list */
	private HashMap checks = new HashMap();
	/** The last selected emitter (index into the list) */
	private int lastSelect = -1;
	
	/**
	 * Create a new visual list of emitters
	 * 
	 * @param editor The editor to report updates to
	 */
	public EmitterList(final ParticleEditor editor) {
		setLayout(null);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setBounds(5,25,285,100);
		scroll.setBorder(BorderFactory.createEtchedBorder());
		add.setBounds(150,125,60,20);
		remove.setBounds(209,125,80,20);
			
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.addEmitter(new ConfigurableEmitter("NewEmitter_"+System.currentTimeMillis()));
			}
		});
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurableEmitter emitter = (ConfigurableEmitter) list.getSelectedValue();
				if (emitter != null) {
					editor.removeEmitter(emitter);
				}
			}
		});
		
		list.setCellRenderer(new Renderer());
		list.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());

				if (index != -1) {
					if (list.getCellBounds(index, index).contains(e.getPoint())) {
						if (lastSelect == list.getSelectedIndex()) {
							ConfigurableEmitter emitter = (ConfigurableEmitter) list.getModel().getElementAt(index);
							JCheckBox box = (JCheckBox) checks.get(emitter);
							emitter.setEnabled(!emitter.isEnabled());
							box.setSelected(emitter.isEnabled());
							
							repaint();
						} else {
							lastSelect = list.getSelectedIndex();
						}
					}
				}
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				editor.setCurrentEmitter((ConfigurableEmitter) list.getSelectedValue());
			}
		});
		
		add(scroll);
		add(add);
		add(remove);		
	}

	/**
	 * Set the currently selected emitter
	 * 
	 * @param emitter The emitter to be selected
	 */
	public void setSelected(ConfigurableEmitter emitter) {
		list.setSelectedValue(emitter, true);
	}
	
	/**
	 * Set the currently selected index in the list
	 * 
	 * @param index The index to be selected
	 */
	public void setSelected(int index) {
		if (index < emitters.size()) {
			list.setSelectedIndex(index);
		}
	}
	
	/**
	 * Update the visual state of the specified emitter in the list
	 * 
	 * @param emitter The emitter to be updated
	 */
	public void update(ConfigurableEmitter emitter) {
		emitters.set(emitters.indexOf(emitter), emitter);
	}
	
	/**
	 * Remove all emitters from the list
	 *
	 */
	public void clear() {
		emitters.clear();
	}
	
	/**
	 * Add an emitter to the list
	 * 
	 * @param emitter The emitter to add
	 */
	public void add(ConfigurableEmitter emitter) {
		emitters.addElement(emitter);
	}

	/**
	 * Remove an emitter from the list
	 * 
	 * @param emitter The emitter to remove
	 */
	public void remove(ConfigurableEmitter emitter) {
		emitters.removeElement(emitter);
	}
	
	/**
	 * Renderer for the emitter list, shows check boxes
	 *
	 * @author kevin
	 */
	public class Renderer extends DefaultListCellRenderer {
		/**
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, final Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			
			final JCheckBox box = new JCheckBox(label.getText());
			box.setBackground(label.getBackground());
			
			box.setSelected(((ConfigurableEmitter) value).isEnabled());
			checks.put(value, box);
			
			return box;
		}
		
	}
}
