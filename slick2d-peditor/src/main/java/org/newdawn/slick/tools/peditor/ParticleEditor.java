package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.lwjgl.LWJGLException;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.InputAdapter;
import org.newdawn.slick.util.Log;

/**
 * The bootstrap and main frame for the particle editor Pedigree.
 *
 * @author kevin
 */
public class ParticleEditor extends JFrame {
	/** The canvas displaying the particles */
	private ParticleGame game;
	/** Create a new system */
	private JMenuItem newSystem = new JMenuItem("New System");
	/** Load a complete particle system */
	private JMenuItem load = new JMenuItem("Load System");
	/** Save a complete particle system */
	private JMenuItem save = new JMenuItem("Save System");
	/** Load a single particle emitter */
	private JMenuItem imp = new JMenuItem("Import Emitter");
	/** Clone a single particle emitter */
	private JMenuItem clone = new JMenuItem("Clone Emitter");
	/** Save a single particle emitter */
	private JMenuItem exp = new JMenuItem("Export Emitter");
	/** Toggle the HUD  */
	private JMenuItem hud = new JMenuItem("Toggle Overlay");
	/** Toggle the HUD  */
	private JMenuItem loadBackground = new JMenuItem("Load Background Image");
	/** Toggle the HUD  */
	private JMenuItem clearBackground = new JMenuItem("Clear Background Image");
	/** Toggle the graphice editor  */
	private JMenuItem whiskas = new JMenuItem("Show/Hide Graph Editor");
	/** Exit the editor */
	private JMenuItem quit = new JMenuItem("Exit");

	/** The visual list of emitters */
	private EmitterList emitters;
	/** The controls for the initial emission settings */
	private EmissionControls emissionControls;
	/** The positional controls for spawnng particles */
	private PositionControls positionControls;
	/** The global settings for the emitter */
	private SettingsPanel settingsPanel;
	/** The color controls for particles */
	private ColorPanel colorPanel;
	/** The limiting controls for particles */
	private LimitPanel limitPanel;
	/** The whiskas panel */
	private WhiskasPanel whiskasPanel;
	
	/** Control for the type of particle system blending */
	private JCheckBox additive = new JCheckBox("Additive Blending");
	/** Control for the type of particle point usage */
	private JCheckBox pointsEnabled = new JCheckBox("Use Points");
	/** The currently selected particle emitter */
	private ConfigurableEmitter selected;
	/** Chooser used to load/save/import/export */
	private JFileChooser chooser = new JFileChooser(new File("."));
	/** Reset the particle counts on the canvas */
	private JButton reset = new JButton("Reset Max");
	/** Play or Pause the current rendering */
	private JButton pause = new JButton("Play/Pause");
	/** The slider defining the movement of the system */
	private JSlider systemMove = new JSlider(-100,100,0);
	
	/** The graph editor frame **/
	private JFrame graphEditorFrame;
	/** The filter in use */
	private FileFilter xmlFileFilter;
	
	/**
	 * Create a new editor
	 * 
	 * @throws LWJGLException Indicates a failure to create an OpenGL context
	 * @throws SlickException 
	 */
	public ParticleEditor() throws LWJGLException, SlickException {
		super("Pedigree - Whiskas flavoured");

		xmlFileFilter = new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				
				if (f.getName().endsWith(".xml")) {
					return true;
				}
				
				return false;
			}

			public String getDescription() {
				return "XML Files";
			}
		};
		chooser.setFileFilter(xmlFileFilter);
		
//		try {
//			InputStream in = ParticleEditor.class.getClassLoader().getResourceAsStream("org/newdawn/slick/tools/peditor/data/icon.gif");
//			
//			setIconImage(ImageIO.read(in));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		emitters = new EmitterList(this);
		emissionControls = new EmissionControls();
		positionControls = new PositionControls();
		settingsPanel = new SettingsPanel(emitters);
		colorPanel = new ColorPanel();
		limitPanel = new LimitPanel(emitters);
		whiskasPanel= new WhiskasPanel( emitters, colorPanel, emissionControls );
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		JMenu file = new JMenu("File");
		file.add(newSystem);
		file.addSeparator();
		file.add(load);
		file.add(save);
		file.addSeparator();
		file.add(imp);
		file.add(clone);
		file.add(exp);
		file.addSeparator();
		file.add(hud);
		file.add(whiskas);
		file.addSeparator();
		file.add(loadBackground);
		file.add(clearBackground);
		file.addSeparator();
		file.add(quit);

		loadBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadBackground();
			}
		});
		clearBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearBackground();
			}
		});
		
		newSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewSystem();
			}
		});
		hud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.setHud(!game.isHudOn());
			}
		});
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSystem();
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSystem();
			}
		});
		clone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cloneEmitter();
			}
		});
		exp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportEmitter();
			}
		});
		imp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importEmitter();
			}
		});
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		whiskas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphEditorFrame.setVisible(!graphEditorFrame.isVisible());
			}
		});
		
		JMenuBar bar = new JMenuBar();
		bar.add(file);
		setJMenuBar(bar);
		
		game = new ParticleGame(this);
		final CanvasGameContainer container = new CanvasGameContainer(game);
		container.getContainer().setAlwaysRender(true);
		container.setSize(500,600);
		JPanel controls = new JPanel();
		controls.setLayout(null);
		emitters.setBounds(0,0,300,150);
		emitters.setBorder(BorderFactory.createTitledBorder("Emitters"));
		controls.add(emitters);
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBounds(0,150,300,350);
		controls.add(tabs);
		
		tabs.add("Settings", settingsPanel);
		tabs.add("Emission", emissionControls);
		tabs.add("Position", positionControls);
		tabs.add("Rendering", colorPanel);
		tabs.add("Limit", limitPanel);
		tabs.add("Whiskas", whiskasPanel);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		container.setBounds(0,0,500,600);
		controls.setBounds(500,20,300,575);
		reset.setBounds(90,500,90,25);
		controls.add(reset);
		systemMove.setBounds(180,500,120,25);
		controls.add(systemMove);
		pause.setBounds(0,500,90,25);
		controls.add(pause);
		additive.setBounds(500,0,150,25);
		panel.add(additive);
		pointsEnabled.setBounds(650,0,150,25);
		panel.add(pointsEnabled);
		panel.add(container);
		panel.add(controls);

		systemMove.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				game.setSystemMove(systemMove.getValue(),false);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		additive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateBlendMode();
			}
		});
		pointsEnabled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.getSystem().setUsePoints(pointsEnabled.isSelected());
			}
		});
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.resetCounts();
			}
		});
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.setPaused(!game.isPaused());
			}
		});
		
		ConfigurableEmitter test = new ConfigurableEmitter("Default");
		emitters.add(test);
		game.addEmitter(test);
		
		additive.setSelected(true);
		
		setContentPane(panel);
		setSize(800,600);
		setResizable(false);
		setVisible(true);

		InputListener listener = new InputAdapter() {
			public void mousePressed(int x, int y, int button) {
				if (button != 0) {
					positionControls.setPosition(0,0);
				}
				systemMove.setValue(0);
				game.setSystemMove(0,true);
			}
			
			public void mouseMoved(int x, int y, int nx, int ny) {
				if (container.getContainer().getInput().isMouseButtonDown(0)) {
					int xp = nx - 250;
					int yp = ny - 300;
					positionControls.setPosition(xp,yp);
					systemMove.setValue(0);
					game.setSystemMove(0,true);
				}
			}
		};
		game.setListener(listener);
		
		// init graph window
		initGraphEditorWindow();

		emitters.setSelected(0);
		
		try {
			container.start();
		} catch (SlickException e1) {
			Log.error(e1);
		}
	}
	
	/**
	 * Load a background image to display behind the particle system
	 */
	private void loadBackground() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Open");
		int resp = chooser.showOpenDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			game.setBackgroundImage(chooser.getSelectedFile());
		}
	}
	
	/** 
	 * Clear the background image in use
	 */
	private void clearBackground() {
		game.setBackgroundImage(null);
	}
	
	/**
	 * init the graph editor window
	 */
	private void initGraphEditorWindow()
	{
	    // create the window
	    GraphEditorWindow editor = new GraphEditorWindow();

	    whiskasPanel.setEditor(editor);

	    graphEditorFrame= new JFrame("Whiskas Gradient Editor");
	    graphEditorFrame.getContentPane().add(editor);
	    graphEditorFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	    graphEditorFrame.pack();
	    graphEditorFrame.setSize(600, 300);
	    graphEditorFrame.setLocation(this.getX(), this.getY()+this.getHeight());
	    graphEditorFrame.setVisible(true);
	    
//		try {
//			InputStream in = ParticleEditor.class.getClassLoader().getResourceAsStream("org/newdawn/slick/tools/peditor/data/icon.gif");
//			
//			//graphEditorFrame.setIconImage(ImageIO.read(in));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Set the movement of the system
	 * 
	 * @param move The movement of the system
	 */
	public void setSystemMove(int move) {
		systemMove.setValue(move);
	}
	
	/**
	 * Import an emitter XML file
	 */
	public void importEmitter() {
		chooser.setDialogTitle("Open");
		int resp = chooser.showOpenDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			File path = file.getParentFile();
			
			try {
				final ConfigurableEmitter emitter = ParticleIO.loadEmitter(file);
				
				
				if (emitter.getImageName() != null) {
					File possible = new File(path, emitter.getImageName());
					
					if (possible.exists()) {
						emitter.setImageName(possible.getAbsolutePath());
					} else {
						chooser.setDialogTitle("Locate the image: "+emitter.getImageName());
						resp = chooser.showOpenDialog(this);
						FileFilter filter = new FileFilter() {
							public boolean accept(File f) {
								if (f.isDirectory()) {
									return true;
								}
								
								return (f.getName().equals(emitter.getImageName()));
							}
	
							public String getDescription() {
								return emitter.getImageName();
							}
						};
						chooser.addChoosableFileFilter(filter);
						if (resp == JFileChooser.APPROVE_OPTION) {
							File image = chooser.getSelectedFile();
							emitter.setImageName(image.getAbsolutePath());
							path = image.getParentFile();
						}
						chooser.resetChoosableFileFilters();
						chooser.addChoosableFileFilter(xmlFileFilter);
					}
				}
				
				addEmitter(emitter);
				emitters.setSelected(emitter);
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	/**
	 * Clone the selected emitter
	 */
	public void cloneEmitter() {
		if (selected == null) {
			return;
		}
		
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ParticleIO.saveEmitter(bout, selected);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ConfigurableEmitter emitter = ParticleIO.loadEmitter(bin);
			emitter.name = emitter.name + "_clone";
			
			addEmitter(emitter);
			emitters.setSelected(emitter);
		} catch (IOException e) {
			Log.error(e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	/**
	 * Export an emitter XML file
	 */
	public void exportEmitter() {
		if (selected == null) {
			return;
		}

		chooser.setDialogTitle("Save");
		int resp = chooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().endsWith(".xml")) {
				file = new File(file.getAbsolutePath()+".xml");
			}
			
			try {
				ParticleIO.saveEmitter(file, selected);
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	/**
	 * Create a completely new particle system
	 */
	public void createNewSystem() {
		game.clearSystem(additive.isSelected());
		pointsEnabled.setSelected(false);
		emitters.clear();
	}
	
	/**
	 * Load a complete particle system XML description
	 */
	public void loadSystem() {
		chooser.setDialogTitle("Open");
		int resp = chooser.showOpenDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			File path = file.getParentFile();
			
			try {
				ParticleSystem system = ParticleIO.loadConfiguredSystem(file);
				game.setSystem(system);
				emitters.clear();
				
				for (int i=0;i<system.getEmitterCount();i++) {
					final ConfigurableEmitter emitter = (ConfigurableEmitter) system.getEmitter(i);
					
					if (emitter.getImageName() != null) {
						File possible = new File(path, emitter.getImageName());
						if (possible.exists()) {
							emitter.setImageName(possible.getAbsolutePath());
						} else {
							chooser.setDialogTitle("Locate the image: "+emitter.getImageName());
							FileFilter filter = new FileFilter() {
								public boolean accept(File f) {
									if (f.isDirectory()) {
										return true;
									}
									
									return (f.getName().equals(emitter.getImageName()));
								}
	
								public String getDescription() {
									return emitter.getImageName();
								}
							};
							
							chooser.addChoosableFileFilter(filter);
							resp = chooser.showOpenDialog(this);
							if (resp == JFileChooser.APPROVE_OPTION) {
								File image = chooser.getSelectedFile();
								emitter.setImageName(image.getAbsolutePath());
								path = image.getParentFile();
							}
							chooser.setDialogTitle("Open");
							chooser.resetChoosableFileFilters();
							chooser.addChoosableFileFilter(xmlFileFilter);
						}
					}
					
					emitters.add(emitter);
				}
				additive.setSelected(system.getBlendingMode() == ParticleSystem.BLEND_ADDITIVE);
				pointsEnabled.setSelected(system.usePoints());
				
				emitters.setSelected(0);
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}

	/**
	 * Save a complete particle system XML description
	 */
	public void saveSystem() {
		int resp = chooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (!file.getName().endsWith(".xml")) {
				file = new File(file.getAbsolutePath()+".xml");
			}
			
			try {
				ParticleIO.saveConfiguredSystem(file, game.getSystem());
			} catch (IOException e) {
				Log.error(e);
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		}
	}
	
	/**
	 * Add a new emitter to the editor 
	 * 
	 * @param emitter The emitter to add
	 */
	public void addEmitter(ConfigurableEmitter emitter) {
		emitters.add(emitter);
		game.addEmitter(emitter);
	}
	
	/**
	 * Remove a particle emitter from the editor
	 * 
	 * @param emitter The emitter to be removed
	 */
	public void removeEmitter(ConfigurableEmitter emitter) {
		emitters.remove(emitter);
		game.removeEmitter(emitter);
	}
	
	/**
	 * Set the currently selected and edited particle emitter
	 * 
	 * @param emitter The emitter that should be selected or null for none
	 */
	public void setCurrentEmitter(ConfigurableEmitter emitter) {
		this.selected = emitter;
		
		if (emitter == null) {
			emissionControls.setEnabled(false);
			settingsPanel.setEnabled(false);
			positionControls.setEnabled(false);
			colorPanel.setEnabled(false);
			limitPanel.setEnabled(false);
			whiskasPanel.setEnabled(false);
		} else {
			emissionControls.setEnabled(true);
			settingsPanel.setEnabled(true);
			positionControls.setEnabled(true);
			colorPanel.setEnabled(true);
			limitPanel.setEnabled(true);
			whiskasPanel.setEnabled(true);
			
			emissionControls.setTarget(emitter);
			settingsPanel.setTarget(emitter);
			positionControls.setTarget(emitter);
			settingsPanel.setTarget(emitter);
			colorPanel.setTarget(emitter);
			limitPanel.setTarget(emitter);
			whiskasPanel.setTarget(emitter);
		}
	}
	
	/**
	 * Change the visual indicator for the current particle system 
	 * blend mode
	 */
	public void updateBlendMode() {
		if (additive.isSelected()) {
			game.getSystem().setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
 		} else {
			game.getSystem().setBlendingMode(ParticleSystem.BLEND_COMBINE);
 		}
	}
	
	/**
	 * Entry point in the editor
	 * 
	 * @param argv The arguments passed on the command line
	 */
	public static void main(String[] argv) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
			new ParticleEditor();
		} catch (Exception e) {
			Log.error(e);
		}
	}
}
