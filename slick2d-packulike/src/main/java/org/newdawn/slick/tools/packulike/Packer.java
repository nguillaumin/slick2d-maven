package org.newdawn.slick.tools.packulike;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

/**
 * A simple GUI on the front of the packing tool
 * 
 * @author kevin
 */
public class Packer extends JFrame {
	/** The panel showing the currently generated sprite sheet */
	private SheetPanel sheetPanel;
	/** The sprites currently displayed */
	private DefaultListModel sprites = new DefaultListModel();
	/** The visual list of the sprites */
	private JList list = new JList(sprites);
	/** The width of the texture being generated */
	private int twidth;
	/** The height of the texture being generated */
	private int theight;
	
	/** The width texture sizes model */
	private DefaultComboBoxModel sizes = new DefaultComboBoxModel();
	/** The height texture sizes model */
	private DefaultComboBoxModel sizes2 = new DefaultComboBoxModel();
	/** The visual selection for texture width */
	private JComboBox widths = new JComboBox(sizes);
	/** The visual selection for texture height */
	private JComboBox heights = new JComboBox(sizes2);
	/** The visual selection for border size */
	private JSpinner border = new JSpinner(new SpinnerNumberModel(0,0,50,1));
	
	/** The chooser used to select sprites */
	private JFileChooser chooser = new JFileChooser(".");
	/** The chooser used to save the sprite sheet */
	private JFileChooser saveChooser = new JFileChooser(".");
	/** The packing tool */
	private Pack pack = new Pack();
	
	/**
	 * Create the new GUI for packer
	 */
	public Packer() {
		super("Pack-U-Like");
		
		saveChooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				
				return (f.getName().endsWith(".png"));
			}

			public String getDescription() {
				return "PNG Images (*.png)";
			}
			
		});
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				
				return (f.getName().endsWith(".png") ||
						f.getName().endsWith(".jpg") ||
						f.getName().endsWith(".gif"));
			}

			public String getDescription() {
				return "Images (*.jpg, *.png, *.gif)";
			}
			
		});
		
		sizes.addElement(new Integer(64));
		sizes.addElement(new Integer(128));
		sizes.addElement(new Integer(256));
		sizes.addElement(new Integer(512));
		sizes.addElement(new Integer(1024));
		sizes.addElement(new Integer(2048));
		sizes2.addElement(new Integer(64));
		sizes2.addElement(new Integer(128));
		sizes2.addElement(new Integer(256));
		sizes2.addElement(new Integer(512));
		sizes2.addElement(new Integer(1024));
		sizes2.addElement(new Integer(2048));
		
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		bar.add(file);
		JMenuItem save = new JMenuItem("Save");
		file.add(save);
		file.addSeparator();
		JMenuItem quit = new JMenuItem("Quit");
		file.add(quit);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		setJMenuBar(bar);
		JPanel panel = new JPanel();
		panel.setLayout(null);
	
		sheetPanel = new SheetPanel(this);
		JScrollPane pane = new JScrollPane(sheetPanel);
		pane.setBounds(5,5,530,530);
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setBounds(540,5,200,350);
		list.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				Object[] values = list.getSelectedValues();
				ArrayList sprites = new ArrayList();
				for (int i=0;i<values.length;i++) {
					sprites.add(values[i]);
				}
				
				list.removeListSelectionListener(this);
				select(sprites);
				list.addListSelectionListener(this);
			}
		});
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setCellRenderer(new FileListRenderer());
		panel.add(pane);
		panel.add(listScroll);
		
		JButton add = new JButton("Add");
		add.setFont(add.getFont().deriveFont(Font.BOLD));
		add.setMargin(new Insets(0,0,0,0));
		add.setBounds(745,5,40,30);
		panel.add(add);
		
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int resp = chooser.showOpenDialog(Packer.this);
				if (resp == JFileChooser.APPROVE_OPTION) {
					File[] selected = chooser.getSelectedFiles();
					for (int i=0;i<selected.length;i++) {
						try {
							sprites.addElement(new Sprite(selected[i]));
						} catch (IOException x) {
							x.printStackTrace();
							JOptionPane.showMessageDialog(Packer.this, "Unable to load: "+selected[i].getName());
						}
					}
				}
				regenerate();
			}
		});
		JButton remove = new JButton("Del");
		remove.setFont(add.getFont().deriveFont(Font.BOLD));
		remove.setMargin(new Insets(0,0,0,0));
		remove.setBounds(745,35,40,30);
		panel.add(remove);
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] selected = list.getSelectedValues();
				for (int i=0;i<selected.length;i++) {
					sprites.removeElement(selected[i]);
				}
				regenerate();
			}
		});
		
		JLabel label;

		label = new JLabel("Border");
		label.setBounds(540,375,200,25);
		panel.add(label);
		border.setBounds(540,400,200,25);
		panel.add(border);
		border.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				regenerate();
			}
		});
		
		label = new JLabel("Width");
		label.setBounds(540,425,200,25);
		panel.add(label);
		widths.setBounds(540,450,200,25);
		panel.add(widths);
		widths.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				twidth = ((Integer) widths.getSelectedItem()).intValue();
				sheetPanel.setTextureSize(twidth, theight);
				regenerate();
			}
		});
		
		label = new JLabel("Height");
		label.setBounds(540,475,200,25);
		panel.add(label);
		heights.setBounds(540,500,200,25);
		heights.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				theight = ((Integer) heights.getSelectedItem()).intValue();
				sheetPanel.setTextureSize(twidth, theight);
				regenerate();
			}
		});
		
		panel.add(heights);
		
		twidth = 512;
		theight = 512;
		sheetPanel.setTextureSize(twidth, theight);
		widths.setSelectedItem(new Integer(twidth));
		heights.setSelectedItem(new Integer(theight));
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setContentPane(panel);
		setSize(800,600);
		setResizable(false);
		setVisible(true);
	}
	
	/**
	 * Get the sprite a given location on the current sheet
	 * 
	 * @param x The x coordinate to look for the sprite
	 * @param y The y coordinate to look for the sprite
	 * @return The sprite found at the given location or null if no sprite can be found
	 */
	public Sprite getSpriteAt(int x, int y) {
		for (int i=0;i<sprites.size();i++) {
			if (((Sprite) sprites.get(i)).contains(x,y)) {
				return ((Sprite) sprites.get(i));
			}
		}
		
		return null;
	}
	
	/**
	 * Select a series of sprites
	 * 
	 * @param selection The series of sprites to be selected (Sprite objects)
	 */
	public void select(ArrayList selection) {
		list.clearSelection();
		int[] selected = new int[selection.size()];
		for (int i=0;i<selection.size();i++) {
			selected[i] = sprites.indexOf(selection.get(i));
		}
		list.setSelectedIndices(selected);
		
		sheetPanel.setSelection(selection);
	}
	
	/**
	 * Save the sprite sheet
	 */
	private void save() {
		int resp = saveChooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File out = saveChooser.getSelectedFile();
			
			ArrayList list = new ArrayList();
			for (int i=0;i<sprites.size();i++) {
				list.add(sprites.elementAt(i));
			}
			
			try {
				int b = ((Integer) border.getValue()).intValue();
				pack.packImages(list, twidth, theight, b, out);
			} catch (IOException e) {
				// shouldn't happen 
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to write output");
			}
		}
	}
	
	/**
	 * Regenerate the sprite sheet that is being displayed
	 */
	private void regenerate() {
		try {
			ArrayList list = new ArrayList();
			for (int i=0;i<sprites.size();i++) {
				list.add(sprites.elementAt(i));
			}
			
			int b = ((Integer) border.getValue()).intValue();
			Sheet sheet = pack.packImages(list, twidth, theight, b, null);
			sheetPanel.setImage(sheet);
		} catch (IOException e) {
			// shouldn't happen 
			e.printStackTrace();
		}
	}
	
	/**
	 * A list cell renderer to show just the plain names
	 * 
	 * @author kevin
	 */
	private class FileListRenderer extends DefaultListCellRenderer {
		/**
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			
			Sprite sprite = (Sprite) value;
			label.setText(sprite.getName());
			
			return label;
		}
	}
	
	/**
	 * Entry point to the simple UI
	 * 
	 * @param argv The arguments to the program
	 */
	public static void main(String[] argv) {
		new Packer();
	}
}
