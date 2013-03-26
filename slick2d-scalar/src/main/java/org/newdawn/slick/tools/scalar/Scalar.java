package org.newdawn.slick.tools.scalar;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

/**
 * A simple tool for applying Scale2X and Scale3X for small cartoon
 * style images. The Scale2X and 3X algorithms provide a better scaling
 * for block/cartoon images since they're based on pattern analysis rather
 * than simple pixel scaling and filtering.
 * 
 * @author kevin
 */
public class Scalar extends JFrame {
	/** The chooser used to load image */
	private JFileChooser loadChooser = new JFileChooser(".");
	/** The chooser used to saveimage */
	private JFileChooser saveChooser = new JFileChooser(".");
	/** The panel displaying the current image */
	private ImagePanel imagePanel;
	/** The last selectd file */
	private File lastSelected;
	
	/**
	 * Create the scalar window and tool
	 */
	public Scalar() {
		super("Scalar");

		saveChooser.addChoosableFileFilter(new FileFilter() {

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.getName().endsWith(".jpg")) {
					return true;
				}
				if (f.getName().endsWith(".gif")) {
					return true;
				}
				if (f.getName().endsWith(".png")) {
					return true;
				}
				
				return false;
			}

			public String getDescription() {
				return "Image files (*.png, *.jpg, *.gif)";
			}
			
		});
		loadChooser.addChoosableFileFilter(new FileFilter() {

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.getName().endsWith(".jpg")) {
					return true;
				}
				if (f.getName().endsWith(".gif")) {
					return true;
				}
				if (f.getName().endsWith(".png")) {
					return true;
				}
				
				return false;
			}

			public String getDescription() {
				return "Image files (*.png, *.jpg, *.gif)";
			}
			
		});
		
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem load = new JMenuItem("Load Image");
		JMenuItem save = new JMenuItem("Save Image");
		JMenuItem scale2x = new JMenuItem("Apply Scale2X");
		JMenuItem scale3x = new JMenuItem("Apply Scale3X");
		JMenuItem quit = new JMenuItem("Exit");
		
		file.add(load);
		file.add(save);
		file.addSeparator();
		file.add(scale2x);
		file.add(scale3x);
		file.addSeparator();
		file.add(quit);
		bar.add(file);
		setJMenuBar(bar);
		
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		scale2x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scale2x();
			}
		});
		scale3x.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scale3x();
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		imagePanel = new ImagePanel();
		setContentPane(new JScrollPane(imagePanel));
		
		setSize(600,600);
    	Dimension dims = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dims.width - getWidth())/2, (dims.height - getHeight()) / 2);
        setVisible(true);
	}
	
	/**
	 * Load the current image
	 */
	public void load() {
		int resp = loadChooser.showOpenDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			lastSelected = loadChooser.getSelectedFile();
			saveChooser.setCurrentDirectory(loadChooser.getCurrentDirectory());
			try {
				BufferedImage image = ImageIO.read(lastSelected);
				imagePanel.setImage(image);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Unable to load image "+lastSelected.getName()+" ");
			}
		}
	}
	
	/**
	 * Save the current image
	 */
	public void save() {
		int resp = saveChooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = saveChooser.getSelectedFile();
			String type = null;
			if (file.getName().endsWith(".png")) {
				type = "PNG";
			}
			if (file.getName().endsWith(".gif")) {
				type = "GIF";
			}
			if (file.getName().endsWith(".jpg")) {
				type = "JPG";
			}
			if (type == null) {
				file = new File(file.getAbsolutePath()+".png");
				type = "PNG";
			}
			
			try {
				ImageIO.write(imagePanel.getImage(), type, file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Unable to save file "+file.getName());
			}
		}
	}
	
	/**
	 * Scale the current image using scale 2X
	 */
	public void scale2x() {
		imagePanel.setImage(new ImageScale2x(imagePanel.getImage()).getScaledImage());
	}
	
	/**
	 * Scale the current image using scale 3X
	 */
	public void scale3x() {
		imagePanel.setImage(new ImageScale3x(imagePanel.getImage()).getScaledImage());
	}
	
	/**
	 * Quit the tool
	 */
	public void quit() {
		System.exit(0);
	}
	
	/**
	 * Entry point to the scalar tool
	 * 
	 * @param argv The arguments passed into the application
	 */
	public static void main(String[] argv) {
		new Scalar();
	}
	
}
