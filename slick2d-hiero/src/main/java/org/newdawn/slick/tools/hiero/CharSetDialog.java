package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * A dialog to allow the configuration of a character set 
 *
 * @author kevin
 */
public class CharSetDialog extends JDialog {
	/** The character set being edited */
	private CharSet set;
	/** The new set name */
	private String newSetName;
	
	/**
	 * Create a new dialog
	 * 
	 * @param parent The parent window
	 * @param s The character set being edited
	 */
	public CharSetDialog(Hiero parent, CharSet s) {
		super(parent, "Editing "+s.getName(), true);
	
		this.set = s.copy(); 
		
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveAs = new JMenuItem("Save As");
		JMenuItem close = new JMenuItem("Close");
		file.add(save);
		file.add(saveAs);
		file.addSeparator();
		file.add(close);
		bar.add(file);
		setJMenuBar(bar);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					set.save(set.getSource());
					dispose();
				} catch (IOException x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(CharSetDialog.this, "Failed to resave character set");
				}
			}
		});
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String name = JOptionPane.showInputDialog(CharSetDialog.this, "Enter a name for the new Chararater Set:");
					set.setName(name);
					if ((name != null) && (name.length() != 0)) {
						set.save(HieroConfig.getConfigFile(name+".set"));
					}
					newSetName = name;
					dispose();
				} catch (IOException x) {
					x.printStackTrace();
					JOptionPane.showMessageDialog(CharSetDialog.this, "Failed to resave character set");
				}
			}
		});
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		if (!s.isMutable()) {
			save.setEnabled(false);
		}
		
		setContentPane(new CharsPanel());
		
		setSize(600,600);
		setResizable(false);
	}
	
	/**
	 * Get the new character set created
	 * 
	 * @return The name of the new set or null if no set was generated
	 */
	public String getNewSet() {
		return newSetName;
	}
	
	/**
	 * A panel to display the selected characters
	 *
	 * @author kevin
	 */
	public class CharsPanel extends JPanel {
		/** The last x position changed */
		private int lastx = -1;
		/** The last y position changed */
		private int lasty;
		
		/**
		 * Create a new panel
		 */
		public CharsPanel() {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					select(e.getX(), e.getY(), true);
				}
				
				public void mouseReleased(MouseEvent e) {
					lastx = -1;
				}
			});
			addMouseMotionListener(new MouseMotionListener() {
				public void mouseDragged(MouseEvent e) {
					select(e.getX(), e.getY(), false);
				}
				
				public void mouseMoved(MouseEvent e) {}
			});
		}
		
		/**
		 * Set the tile at a given location
		 * 
		 * @param x The x position 
		 * @param y The y position
		 * @param updateIfSame True if we should update if it's the same as the last update
		 */
		private void select(int x, int y, boolean updateIfSame) {
			x -= 35;
			y -= 10;
			x /= 32;
			y /= 32;
			
			
			if ((x >= 0) && (x < 16) && (y >= 0) && (y < 16)) {
				if (!updateIfSame) {
					if ((lastx == x) && (lasty == y)) {
						return;
					}
				}
				lastx = x;
				lasty = y;
				
				int i = x + (y*16);
				set.set(i, !set.includes((char) i));
				repaint(0);
			}
		}
		/**
		 * @see javax.swing.JComponent#paint(java.awt.Graphics)
		 */
		public void paint(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0,0,getWidth(),getHeight());
			g.translate(35,10);
			g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 25));
			for (int i=0;i<256;i++) {
				int x = i % 16;
				int y = i / 16;
				
				char c = (char) i;

				g.setColor(Color.white);
				if (set.includes(c)) {
					g.fillRect(x*32,y*32,32,32);
					g.setColor(Color.black);
					g.drawRect(x*32,y*32,32,32);
				} else {
					g.drawRect(x*32,y*32,32,32);
				}
				
				g.drawString(""+c, (x*32)+5, (y*32)+28);
			}
		}
	}
}
