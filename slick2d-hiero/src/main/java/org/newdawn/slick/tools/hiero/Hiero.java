
package org.newdawn.slick.tools.hiero;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.GlyphPage;
import org.newdawn.slick.font.HieroSettings;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.font.effects.EffectUtil;
import org.newdawn.slick.font.effects.GradientEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.font.effects.OutlineWobbleEffect;
import org.newdawn.slick.font.effects.OutlineZigzagEffect;
import org.newdawn.slick.font.effects.ShadowEffect;
import org.newdawn.slick.font.effects.ConfigurableEffect.Value;
import org.newdawn.slick.util.Log;

/**
 * A tool to visualize settings for {@link UnicodeFont} and to export BMFont files for use with {@link AngelCodeFont}.
 * @author Nathan Sweet <misc@n4te.com>
 */
public class Hiero extends JFrame {
	static final String NEHE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" //
		+ "abcdefghijklmnopqrstuvwxyz\n1234567890\n" //
		+ "\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*\u007F";

	Game game;
	CanvasGameContainer canvasContainer;
	volatile UnicodeFont newUnicodeFont;
	UnicodeFont unicodeFont;
	Color renderingBackgroundColor = Color.black;
	List effectPanels = new ArrayList();
	Preferences prefs;
	ColorEffect colorEffect;

	JScrollPane appliedEffectsScroll;
	JPanel appliedEffectsPanel;
	JButton addEffectButton;
	JTextPane sampleTextPane;
	JSpinner padAdvanceXSpinner;
	JList effectsList;
	JPanel gamePanel;
	JTextField fontFileText;
	JRadioButton fontFileRadio;
	JRadioButton systemFontRadio;
	JSpinner padBottomSpinner;
	JSpinner padLeftSpinner;
	JSpinner padRightSpinner;
	JSpinner padTopSpinner;
	JList fontList;
	JSpinner fontSizeSpinner;
	DefaultComboBoxModel fontListModel;
	JLabel backgroundColorLabel;
	JButton browseButton;
	JSpinner padAdvanceYSpinner;
	JCheckBox italicCheckBox;
	JCheckBox boldCheckBox;
	JLabel glyphsTotalLabel;
	JLabel glyphPagesTotalLabel;
	JComboBox glyphPageHeightCombo;
	JComboBox glyphPageWidthCombo;
	JComboBox glyphPageCombo;
	JPanel glyphCachePanel;
	JRadioButton glyphCacheRadio;
	JRadioButton sampleTextRadio;
	DefaultComboBoxModel glyphPageComboModel;
	JButton resetCacheButton;
	JButton sampleAsciiButton;
	JButton sampleNeheButton;
	DefaultComboBoxModel effectsListModel;
	JMenuItem openMenuItem;
	JMenuItem saveMenuItem;
	JMenuItem exitMenuItem;
	JMenuItem saveBMFontMenuItem;
	File saveBmFontFile;

	public Hiero () throws SlickException {
		super("Hiero v2.0 - Bitmap Font Tool");
		Splash splash = new Splash(this, "splash.jpg", 2000);
		try {
			initialize();
		} catch (SlickException ex) {
			dispose();
			throw ex;
		}
		splash.close();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		prefs = Preferences.userNodeForPackage(Hiero.class);
		java.awt.Color backgroundColor = EffectUtil.fromString(prefs.get("background", "000000"));
		backgroundColorLabel.setIcon(getColorIcon(backgroundColor));
		renderingBackgroundColor = new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
		fontList.setSelectedValue(prefs.get("system.font", "Arial"), true);
		fontFileText.setText(prefs.get("font.file", ""));

		java.awt.Color foregroundColor = EffectUtil.fromString(prefs.get("foreground", "ffffff"));
		colorEffect = new ColorEffect();
		colorEffect.setColor(foregroundColor);
		effectsListModel.addElement(colorEffect);
		effectsListModel.addElement(new GradientEffect());
		effectsListModel.addElement(new OutlineEffect());
		effectsListModel.addElement(new OutlineWobbleEffect());
		effectsListModel.addElement(new OutlineZigzagEffect());
		effectsListModel.addElement(new ShadowEffect());
		new EffectPanel(colorEffect);

		setVisible(true);
		gamePanel.add(canvasContainer);
		gamePanel.setVisible(false);
		canvasContainer.start();
	}

	void initialize () throws SlickException {
		initializeComponents();
		initializeMenus();
		initializeEvents();

		setSize(800, 600);
		setLocationRelativeTo(null);

		Input.disableControllers();

		sampleNeheButton.doClick();

		game = new BasicGame("Hiero") {
			String sampleText;

			public void init (final GameContainer container) throws SlickException {
				container.setShowFPS(false);
				container.setVerbose(false);
				container.setTargetFrameRate(60);
				container.setClearEachFrame(false);
				container.setAlwaysRender(true);
				gamePanel.setVisible(true);
			}

			public void update (GameContainer container, int delta) throws SlickException {
				if (newUnicodeFont != null) {
					if (unicodeFont != null) unicodeFont.destroy();
					unicodeFont = newUnicodeFont;
					newUnicodeFont = null;
				}

				// BOZO - Fix no effects.
				if (unicodeFont.loadGlyphs(25)) {
					glyphPageComboModel.removeAllElements();
					int pageCount = unicodeFont.getGlyphPages().size();
					int glyphCount = 0;
					for (int i = 0; i < pageCount; i++) {
						glyphPageComboModel.addElement("Page " + (i + 1));
						glyphCount += ((GlyphPage)unicodeFont.getGlyphPages().get(i)).getGlyphs().size();
					}
					glyphPagesTotalLabel.setText(String.valueOf(pageCount));
					glyphsTotalLabel.setText(String.valueOf(glyphCount));
				}

				if (saveBmFontFile != null) {
					try {
						BMFontUtil bmFont = new BMFontUtil(unicodeFont);
						bmFont.save(saveBmFontFile);
					} catch (Exception ex) {
						Log.error("Error saving BMFont files: " + saveBmFontFile.getAbsolutePath(), ex);
					} finally {
						saveBmFontFile = null;
					}
				}
			}

			public void render (GameContainer container, Graphics g) throws SlickException {
				if (unicodeFont == null) return;

				try {
					sampleText = sampleTextPane.getText();
				} catch (Exception ex) {
				}

				if (sampleTextRadio.isSelected()) {
					g.setBackground(renderingBackgroundColor);
					g.clear();
					int offset = unicodeFont.getYOffset(sampleText);
					if (offset > 0) offset = 0;
					unicodeFont.drawString(0, -offset, sampleText, Color.white, 0, sampleText.length());
				} else {
					g.setBackground(Color.white);
					g.clear();
					unicodeFont.addGlyphs(sampleText);
					g.setColor(renderingBackgroundColor);
					g.fillRect(0, 0, unicodeFont.getGlyphPageWidth() + 2, unicodeFont.getGlyphPageHeight() + 2);
					int index = glyphPageCombo.getSelectedIndex();
					List pages = unicodeFont.getGlyphPages();
					if (index >= 0 && index < pages.size())
						((GlyphPage)pages.get(glyphPageCombo.getSelectedIndex())).getImage().draw(1, 1);
				}
			}
		};

		canvasContainer = new CanvasGameContainer(game) {
			public int getWidth () {
				int width = super.getWidth();
				return width <= 0 ? 1 : width;
			}

			public int getHeight () {
				int height = super.getHeight();
				return height <= 0 ? 1 : height;
			}
		};
	}

	private void updateFont () {
		updateFont(false);
	}

	private void updateFont (boolean ignoreFileText) {
		UnicodeFont unicodeFont;

		int fontSize = ((Integer)fontSizeSpinner.getValue()).intValue();

		File file = new File(fontFileText.getText());
		if (!ignoreFileText && file.exists() && file.isFile()) {
			// Load from file.
			fontFileRadio.setSelected(true);
			fontList.setEnabled(false);
			systemFontRadio.setEnabled(false);
			try {
				unicodeFont = new UnicodeFont(fontFileText.getText(), fontSize, boldCheckBox.isSelected(), italicCheckBox
					.isSelected());
			} catch (Exception ex) {
				ex.printStackTrace();
				updateFont(true);
				return;
			}
		} else {
			// Load from java.awt.Font (kerning not available!).
			fontList.setEnabled(true);
			systemFontRadio.setEnabled(true);
			systemFontRadio.setSelected(true);
			unicodeFont = new UnicodeFont(Font.decode((String)fontList.getSelectedValue()), fontSize, boldCheckBox.isSelected(),
				italicCheckBox.isSelected());
		}
		unicodeFont.setPaddingTop(((Integer)padTopSpinner.getValue()).intValue());
		unicodeFont.setPaddingRight(((Integer)padRightSpinner.getValue()).intValue());
		unicodeFont.setPaddingBottom(((Integer)padBottomSpinner.getValue()).intValue());
		unicodeFont.setPaddingLeft(((Integer)padLeftSpinner.getValue()).intValue());
		unicodeFont.setPaddingAdvanceX(((Integer)padAdvanceXSpinner.getValue()).intValue());
		unicodeFont.setPaddingAdvanceY(((Integer)padAdvanceYSpinner.getValue()).intValue());
		unicodeFont.setGlyphPageWidth(((Integer)glyphPageWidthCombo.getSelectedItem()).intValue());
		unicodeFont.setGlyphPageHeight(((Integer)glyphPageHeightCombo.getSelectedItem()).intValue());

		for (Iterator iter = effectPanels.iterator(); iter.hasNext();) {
			EffectPanel panel = (EffectPanel)iter.next();
			unicodeFont.getEffects().add(panel.getEffect());
		}

		int size = sampleTextPane.getFont().getSize();
		if (size < 14) size = 14;
		sampleTextPane.setFont(unicodeFont.getFont().deriveFont((float)size));

		this.newUnicodeFont = unicodeFont;
	}

	void save (File file) throws IOException {
		HieroSettings settings = new HieroSettings();
		settings.setFontSize(((Integer)fontSizeSpinner.getValue()).intValue());
		settings.setBold(boldCheckBox.isSelected());
		settings.setItalic(italicCheckBox.isSelected());
		settings.setPaddingTop(((Integer)padTopSpinner.getValue()).intValue());
		settings.setPaddingRight(((Integer)padRightSpinner.getValue()).intValue());
		settings.setPaddingBottom(((Integer)padBottomSpinner.getValue()).intValue());
		settings.setPaddingLeft(((Integer)padLeftSpinner.getValue()).intValue());
		settings.setPaddingAdvanceX(((Integer)padAdvanceXSpinner.getValue()).intValue());
		settings.setPaddingAdvanceY(((Integer)padAdvanceYSpinner.getValue()).intValue());
		settings.setGlyphPageWidth(((Integer)glyphPageWidthCombo.getSelectedItem()).intValue());
		settings.setGlyphPageHeight(((Integer)glyphPageHeightCombo.getSelectedItem()).intValue());
		for (Iterator iter = effectPanels.iterator(); iter.hasNext();) {
			EffectPanel panel = (EffectPanel)iter.next();
			settings.getEffects().add(panel.getEffect());
		}
		settings.save(file);
	}

	void open (File file) throws SlickException {
		EffectPanel[] panels = (EffectPanel[])effectPanels.toArray(new EffectPanel[effectPanels.size()]);
		for (int i = 0; i < panels.length; i++)
			panels[i].remove();

		HieroSettings settings = new HieroSettings(file.getAbsolutePath());
		fontSizeSpinner.setValue(new Integer(settings.getFontSize()));
		boldCheckBox.setSelected(settings.isBold());
		italicCheckBox.setSelected(settings.isItalic());
		padTopSpinner.setValue(new Integer(settings.getPaddingTop()));
		padRightSpinner.setValue(new Integer(settings.getPaddingRight()));
		padBottomSpinner.setValue(new Integer(settings.getPaddingBottom()));
		padLeftSpinner.setValue(new Integer(settings.getPaddingLeft()));
		padAdvanceXSpinner.setValue(new Integer(settings.getPaddingAdvanceX()));
		padAdvanceYSpinner.setValue(new Integer(settings.getPaddingAdvanceY()));
		glyphPageWidthCombo.setSelectedItem(new Integer(settings.getGlyphPageWidth()));
		glyphPageHeightCombo.setSelectedItem(new Integer(settings.getGlyphPageHeight()));
		for (Iterator iter = settings.getEffects().iterator(); iter.hasNext();) {
			ConfigurableEffect settingsEffect = (ConfigurableEffect)iter.next();
			for (int i = 0, n = effectsListModel.getSize(); i < n; i++) {
				ConfigurableEffect effect = (ConfigurableEffect)effectsListModel.getElementAt(i);
				if (effect.getClass() == settingsEffect.getClass()) {
					effect.setValues(settingsEffect.getValues());
					new EffectPanel(effect);
					break;
				}
			}
		}

		updateFont();
	}

	private void initializeEvents () {
		fontList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged (ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) return;
				prefs.put("system.font", (String)fontList.getSelectedValue());
				updateFont();
			}
		});

		class FontUpdateListener implements ChangeListener, ActionListener {
			public void stateChanged (ChangeEvent evt) {
				updateFont();
			}

			public void actionPerformed (ActionEvent evt) {
				updateFont();
			}

			public void addSpinners (JSpinner[] spinners) {
				for (int i = 0; i < spinners.length; i++) {
					final JSpinner spinner = spinners[i];
					spinner.addChangeListener(this);
					((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
						String lastText;

						public void keyReleased (KeyEvent evt) {
							JFormattedTextField textField = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
							String text = textField.getText();
							if (text.length() == 0) return;
							if (text.equals(lastText)) return;
							lastText = text;
							int caretPosition = textField.getCaretPosition();
							try {
								spinner.setValue(Integer.valueOf(text));
								textField.setCaretPosition(caretPosition);
							} catch (Exception ignored) {
							}
						}
					});
				}
			}
		}
		FontUpdateListener listener = new FontUpdateListener();

		listener.addSpinners(new JSpinner[] {padTopSpinner, padRightSpinner, padBottomSpinner, padLeftSpinner, padAdvanceXSpinner,
			padAdvanceYSpinner});
		fontSizeSpinner.addChangeListener(listener);

		glyphPageWidthCombo.addActionListener(listener);
		glyphPageHeightCombo.addActionListener(listener);
		boldCheckBox.addActionListener(listener);
		italicCheckBox.addActionListener(listener);
		resetCacheButton.addActionListener(listener);

		sampleTextRadio.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				glyphCachePanel.setVisible(false);
			}
		});
		glyphCacheRadio.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				glyphCachePanel.setVisible(true);
			}
		});

		fontFileText.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate (DocumentEvent evt) {
				changed();
			}

			public void insertUpdate (DocumentEvent evt) {
				changed();
			}

			public void changedUpdate (DocumentEvent evt) {
				changed();
			}

			private void changed () {
				File file = new File(fontFileText.getText());
				if (fontList.isEnabled() && (!file.exists() || !file.isFile())) return;
				prefs.put("font.file", fontFileText.getText());
				updateFont();
			}
		});

		fontFileRadio.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				if (fontList.isEnabled()) systemFontRadio.setSelected(true);
			}
		});

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				FileDialog dialog = new FileDialog(Hiero.this, "Choose TrueType font file", FileDialog.LOAD);
				dialog.setLocationRelativeTo(null);
				dialog.setFile("*.ttf");
				dialog.setVisible(true);
				String fileName = dialog.getFile();
				if (fileName == null) return;
				fontFileText.setText(new File(dialog.getDirectory(), fileName).getAbsolutePath());
			}
		});

		backgroundColorLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked (MouseEvent evt) {
				java.awt.Color color = JColorChooser.showDialog(null, "Choose a background color", EffectUtil.fromString(prefs.get(
					"background", "000000")));
				if (color == null) return;
				renderingBackgroundColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
				backgroundColorLabel.setIcon(getColorIcon(color));
				prefs.put("background", EffectUtil.toString(color));
			}
		});

		effectsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged (ListSelectionEvent evt) {
				ConfigurableEffect selectedEffect = (ConfigurableEffect)effectsList.getSelectedValue();
				boolean enabled = selectedEffect != null;
				for (Iterator iter = effectPanels.iterator(); iter.hasNext();) {
					ConfigurableEffect effect = ((EffectPanel)iter.next()).getEffect();
					if (effect == selectedEffect) {
						enabled = false;
						break;
					}
				}
				addEffectButton.setEnabled(enabled);
			}
		});

		effectsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked (MouseEvent evt) {
				if (evt.getClickCount() == 2 && addEffectButton.isEnabled()) addEffectButton.doClick();
			}
		});

		addEffectButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				new EffectPanel((ConfigurableEffect)effectsList.getSelectedValue());
			}
		});

		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				FileDialog dialog = new FileDialog(Hiero.this, "Open Hiero settings file", FileDialog.LOAD);
				dialog.setLocationRelativeTo(null);
				dialog.setFile("*.hiero");
				dialog.setVisible(true);
				String fileName = dialog.getFile();
				if (fileName == null) return;
				File file = new File(dialog.getDirectory(), fileName);
				try {
					open(file);
				} catch (SlickException ex) {
					throw new RuntimeException("Error opening Hiero settings file: " + file.getAbsolutePath(), ex);
				}
			}
		});

		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				FileDialog dialog = new FileDialog(Hiero.this, "Save Hiero settings file", FileDialog.SAVE);
				dialog.setLocationRelativeTo(null);
				dialog.setFile("*.hiero");
				dialog.setVisible(true);
				String fileName = dialog.getFile();
				if (fileName == null) return;
				File file = new File(dialog.getDirectory(), fileName);
				try {
					save(file);
				} catch (IOException ex) {
					throw new RuntimeException("Error saving Hiero settings file: " + file.getAbsolutePath(), ex);
				}
			}
		});

		saveBMFontMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				FileDialog dialog = new FileDialog(Hiero.this, "Save BMFont files", FileDialog.SAVE);
				dialog.setLocationRelativeTo(null);
				dialog.setFile("*.fnt");
				dialog.setVisible(true);
				String fileName = dialog.getFile();
				if (fileName == null) return;
				saveBmFontFile = new File(dialog.getDirectory(), fileName);
			}
		});

		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				dispose();
			}
		});

		sampleNeheButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				sampleTextPane.setText(NEHE);
			}
		});

		sampleAsciiButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(NEHE);
				buffer.append('\n');
				int count = 0;
				for (int i = 33; i <= 255; i++) {
					if (buffer.indexOf(Character.toString((char)i)) != -1) continue;
					buffer.append((char)i);
					if (++count % 30 == 0) buffer.append('\n');
				}
				sampleTextPane.setText(buffer.toString());
			}
		});
	}

	private void initializeComponents () {
		getContentPane().setLayout(new GridBagLayout());
		JPanel leftSidePanel = new JPanel();
		leftSidePanel.setLayout(new GridBagLayout());
		getContentPane().add(
			leftSidePanel,
			new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
		{
			JPanel fontPanel = new JPanel();
			leftSidePanel.add(fontPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
			fontPanel.setLayout(new GridBagLayout());
			fontPanel.setBorder(BorderFactory.createTitledBorder("Font"));
			{
				fontSizeSpinner = new JSpinner(new SpinnerNumberModel(32, 0, 256, 1));
				fontPanel.add(fontSizeSpinner, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
				((JSpinner.DefaultEditor)fontSizeSpinner.getEditor()).getTextField().setColumns(2);
			}
			{
				JScrollPane fontScroll = new JScrollPane();
				fontPanel.add(fontScroll, new GridBagConstraints(1, 1, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
				{
					fontListModel = new DefaultComboBoxModel(GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getAvailableFontFamilyNames());
					fontList = new JList();
					fontScroll.setViewportView(fontList);
					fontList.setModel(fontListModel);
					fontList.setVisibleRowCount(6);
					fontList.setSelectedIndex(0);
					fontScroll.setMinimumSize(new Dimension(220, fontList.getPreferredScrollableViewportSize().height));
				}
			}
			{
				systemFontRadio = new JRadioButton("System:", true);
				fontPanel.add(systemFontRadio, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
					GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
				systemFontRadio.setMargin(new Insets(0, 0, 0, 0));
			}
			{
				fontFileRadio = new JRadioButton("File:");
				fontPanel.add(fontFileRadio, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				fontFileRadio.setMargin(new Insets(0, 0, 0, 0));
			}
			{
				fontFileText = new JTextField();
				fontPanel.add(fontFileText, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
			}
			{
				fontPanel.add(new JLabel("Size:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
			}
			{
				boldCheckBox = new JCheckBox("Bold");
				fontPanel.add(boldCheckBox, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
			}
			{
				italicCheckBox = new JCheckBox("Italic");
				fontPanel.add(italicCheckBox, new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
			}
			{
				browseButton = new JButton("...");
				fontPanel.add(browseButton, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
				browseButton.setMargin(new Insets(0, 0, 0, 0));
			}
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(systemFontRadio);
			buttonGroup.add(fontFileRadio);
		}
		{
			JPanel samplePanel = new JPanel();
			leftSidePanel.add(samplePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
			samplePanel.setLayout(new GridBagLayout());
			samplePanel.setBorder(BorderFactory.createTitledBorder("Sample Text"));
			{
				JScrollPane textScroll = new JScrollPane();
				samplePanel.add(textScroll, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
				{
					sampleTextPane = new JTextPane();
					textScroll.setViewportView(sampleTextPane);
				}
			}
			{
				sampleNeheButton = new JButton();
				sampleNeheButton.setText("NEHE");
				samplePanel.add(sampleNeheButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
			}
			{
				sampleAsciiButton = new JButton();
				sampleAsciiButton.setText("ASCII");
				samplePanel.add(sampleAsciiButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
			}
		}
		{
			JPanel renderingPanel = new JPanel();
			leftSidePanel.add(renderingPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
			renderingPanel.setBorder(BorderFactory.createTitledBorder("Rendering"));
			renderingPanel.setLayout(new GridBagLayout());
			{
				JPanel wrapperPanel = new JPanel();
				renderingPanel.add(wrapperPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
				wrapperPanel.setLayout(new BorderLayout());
				wrapperPanel.setBackground(java.awt.Color.white);
				{
					gamePanel = new JPanel();
					wrapperPanel.add(gamePanel);
					gamePanel.setLayout(new BorderLayout());
					gamePanel.setBackground(java.awt.Color.white);
				}
			}
			{
				glyphCachePanel = new JPanel() {
					private int maxWidth;

					public Dimension getPreferredSize () {
						// Keep glyphCachePanel width from ever going down so the CanvasGameContainer doesn't change sizes and flicker.
						Dimension size = super.getPreferredSize();
						maxWidth = Math.max(maxWidth, size.width);
						size.width = maxWidth;
						return size;
					}
				};
				glyphCachePanel.setVisible(false);
				renderingPanel.add(glyphCachePanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				glyphCachePanel.setLayout(new GridBagLayout());
				{
					glyphCachePanel.add(new JLabel("Glyphs:"), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
				{
					glyphCachePanel.add(new JLabel("Pages:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
				{
					glyphCachePanel.add(new JLabel("Page width:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
				{
					glyphCachePanel.add(new JLabel("Page height:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
				{
					glyphPageWidthCombo = new JComboBox(new DefaultComboBoxModel(new Integer[] {new Integer(32), new Integer(64), new Integer(128), new Integer(256), new Integer(512),
						new Integer(1024), new Integer(2048)}));
					glyphCachePanel.add(glyphPageWidthCombo, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
					glyphPageWidthCombo.setSelectedIndex(1);
				}
				{
					glyphPageHeightCombo = new JComboBox(new DefaultComboBoxModel(new Integer[] {new Integer(32), new Integer(64), new Integer(128), new Integer(256), new Integer(512),
							new Integer(1024), new Integer(2048)}));
					glyphCachePanel.add(glyphPageHeightCombo, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
					glyphPageHeightCombo.setSelectedIndex(1);
				}
				{
					resetCacheButton = new JButton("Reset Cache");
					glyphCachePanel.add(resetCacheButton, new GridBagConstraints(0, 6, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
				{
					glyphPagesTotalLabel = new JLabel("1");
					glyphCachePanel.add(glyphPagesTotalLabel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
				}
				{
					glyphsTotalLabel = new JLabel("0");
					glyphCachePanel.add(glyphsTotalLabel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
				}
				{
					glyphPageComboModel = new DefaultComboBoxModel();
					glyphPageCombo = new JComboBox();
					glyphCachePanel.add(glyphPageCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
					glyphPageCombo.setModel(glyphPageComboModel);
				}
				{
					glyphCachePanel.add(new JLabel("View:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
			}
			{
				JPanel radioButtonsPanel = new JPanel();
				renderingPanel.add(radioButtonsPanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				radioButtonsPanel.setLayout(new GridBagLayout());
				{
					sampleTextRadio = new JRadioButton("Sample text");
					radioButtonsPanel.add(sampleTextRadio, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
					sampleTextRadio.setSelected(true);
				}
				{
					glyphCacheRadio = new JRadioButton("Glyph cache");
					radioButtonsPanel.add(glyphCacheRadio, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
				}
				{
					radioButtonsPanel.add(new JLabel("Background:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
				}
				{
					backgroundColorLabel = new JLabel();
					radioButtonsPanel.add(backgroundColorLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
				}
				ButtonGroup buttonGroup = new ButtonGroup();
				buttonGroup.add(glyphCacheRadio);
				buttonGroup.add(sampleTextRadio);
			}
		}
		JPanel rightSidePanel = new JPanel();
		rightSidePanel.setLayout(new GridBagLayout());
		getContentPane().add(
			rightSidePanel,
			new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0, 0));
		{
			JPanel paddingPanel = new JPanel();
			paddingPanel.setLayout(new GridBagLayout());
			rightSidePanel.add(paddingPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
			paddingPanel.setBorder(BorderFactory.createTitledBorder("Padding"));
			{
				padTopSpinner = new JSpinner();
				paddingPanel.add(padTopSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				((JSpinner.DefaultEditor)padTopSpinner.getEditor()).getTextField().setColumns(2);
			}
			{
				padRightSpinner = new JSpinner();
				paddingPanel.add(padRightSpinner, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
				((JSpinner.DefaultEditor)padRightSpinner.getEditor()).getTextField().setColumns(2);
			}
			{
				padLeftSpinner = new JSpinner();
				paddingPanel.add(padLeftSpinner, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
				((JSpinner.DefaultEditor)padLeftSpinner.getEditor()).getTextField().setColumns(2);
			}
			{
				padBottomSpinner = new JSpinner();
				paddingPanel.add(padBottomSpinner, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				((JSpinner.DefaultEditor)padBottomSpinner.getEditor()).getTextField().setColumns(2);
			}
			{
				JPanel advancePanel = new JPanel();
				FlowLayout advancePanelLayout = new FlowLayout();
				advancePanel.setLayout(advancePanelLayout);
				paddingPanel.add(advancePanel, new GridBagConstraints(0, 4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				{
					advancePanel.add(new JLabel("X:"));
				}
				{
					padAdvanceXSpinner = new JSpinner();
					advancePanel.add(padAdvanceXSpinner);
					((JSpinner.DefaultEditor)padAdvanceXSpinner.getEditor()).getTextField().setColumns(2);
				}
				{
					advancePanel.add(new JLabel("Y:"));
				}
				{
					padAdvanceYSpinner = new JSpinner();
					advancePanel.add(padAdvanceYSpinner);
					((JSpinner.DefaultEditor)padAdvanceYSpinner.getEditor()).getTextField().setColumns(2);
				}
			}
		}
		{
			JPanel effectsPanel = new JPanel();
			effectsPanel.setLayout(new GridBagLayout());
			rightSidePanel.add(effectsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
			effectsPanel.setBorder(BorderFactory.createTitledBorder("Effects"));
			effectsPanel.setMinimumSize(new Dimension(210, 1));
			{
				JScrollPane effectsScroll = new JScrollPane();
				effectsPanel.add(effectsScroll, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
				{
					effectsListModel = new DefaultComboBoxModel();
					effectsList = new JList();
					effectsScroll.setViewportView(effectsList);
					effectsList.setModel(effectsListModel);
					effectsList.setVisibleRowCount(6);
					effectsScroll.setMinimumSize(effectsList.getPreferredScrollableViewportSize());
				}
			}
			{
				addEffectButton = new JButton("Add");
				effectsPanel.add(addEffectButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 5, 6, 5), 0, 0));
				addEffectButton.setEnabled(false);
			}
			{
				appliedEffectsScroll = new JScrollPane();
				effectsPanel.add(appliedEffectsScroll, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
				appliedEffectsScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
				appliedEffectsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				{
					JPanel panel = new JPanel();
					panel.setLayout(new GridBagLayout());
					appliedEffectsScroll.setViewportView(panel);
					{
						appliedEffectsPanel = new JPanel();
						appliedEffectsPanel.setLayout(new GridBagLayout());
						panel.add(appliedEffectsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
						appliedEffectsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, java.awt.Color.black));
					}
				}
			}
		}
	}

	private void initializeMenus () {
		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				JMenu fileMenu = new JMenu();
				menuBar.add(fileMenu);
				fileMenu.setText("File");
				fileMenu.setMnemonic(KeyEvent.VK_F);
				{
					openMenuItem = new JMenuItem("Open Hiero settings file...");
					openMenuItem.setMnemonic(KeyEvent.VK_O);
					openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
					fileMenu.add(openMenuItem);
				}
				{
					saveMenuItem = new JMenuItem("Save Hiero settings file...");
					saveMenuItem.setMnemonic(KeyEvent.VK_S);
					saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
					fileMenu.add(saveMenuItem);
				}
				fileMenu.addSeparator();
				{
					saveBMFontMenuItem = new JMenuItem("Save BMFont files (text)...");
					saveBMFontMenuItem.setMnemonic(KeyEvent.VK_B);
					saveBMFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
					fileMenu.add(saveBMFontMenuItem);
				}
				fileMenu.addSeparator();
				{
					exitMenuItem = new JMenuItem("Exit");
					exitMenuItem.setMnemonic(KeyEvent.VK_X);
					fileMenu.add(exitMenuItem);
				}
			}
		}
	}

	static Icon getColorIcon (java.awt.Color color) {
		BufferedImage image = new BufferedImage(32, 16, BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics g = image.getGraphics();
		g.setColor(color);
		g.fillRect(1, 1, 30, 14);
		g.setColor(java.awt.Color.black);
		g.drawRect(0, 0, 31, 15);
		return new ImageIcon(image);
	}

	private class EffectPanel extends JPanel {
		private final java.awt.Color selectedColor = new java.awt.Color(0xb1d2e9);

		private final ConfigurableEffect effect;
		private List values;

		private JButton deleteButton;
		private JPanel valuesPanel;
		private JLabel nameLabel;

		private EffectPanel (final ConfigurableEffect effect) {
			this.effect = effect;
			effectPanels.add(this);
			effectsList.getListSelectionListeners()[0].valueChanged(null);

			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
			appliedEffectsPanel.add(this, new GridBagConstraints(0, -1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			{
				JPanel titlePanel = new JPanel();
				titlePanel.setLayout(new LayoutManager() {
					public void removeLayoutComponent (Component comp) {
					}

					public Dimension preferredLayoutSize (Container parent) {
						return null;
					}

					public Dimension minimumLayoutSize (Container parent) {
						return null;
					}

					public void layoutContainer (Container parent) {
						Dimension buttonSize = deleteButton.getPreferredSize();
						deleteButton.setBounds(getWidth() - buttonSize.width - 5, 0, buttonSize.width, buttonSize.height);

						Dimension labelSize = nameLabel.getPreferredSize();
						nameLabel.setBounds(5, buttonSize.height / 2 - labelSize.height / 2, getWidth() - buttonSize.width - 5 - 5,
							labelSize.height);
					}

					public void addLayoutComponent (String name, Component comp) {
					}
				});
				{
					deleteButton = new JButton();
					titlePanel.add(deleteButton);
					deleteButton.setText("X");
					deleteButton.setMargin(new Insets(0, 0, 0, 0));
					Font font = deleteButton.getFont();
					deleteButton.setFont(new Font(font.getName(), font.getStyle(), font.getSize() - 2));
				}
				{
					nameLabel = new JLabel(effect.toString());
					titlePanel.add(nameLabel);
					Font font = nameLabel.getFont();
					nameLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
				}
				titlePanel.setPreferredSize(new Dimension(0, Math.max(nameLabel.getPreferredSize().height, deleteButton
					.getPreferredSize().height)));
				add(titlePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 0, 0, 5), 0, 0));
				titlePanel.setOpaque(false);
			}
			{
				valuesPanel = new JPanel();
				valuesPanel.setOpaque(false);
				valuesPanel.setLayout(new GridBagLayout());
				add(valuesPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));
			}

			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent evt) {
					remove();
					updateFont();
				}
			});

			updateValues();
			updateFont();
		}

		public void remove () {
			effectPanels.remove(this);
			appliedEffectsPanel.remove(EffectPanel.this);
			getContentPane().validate();
			effectsList.getListSelectionListeners()[0].valueChanged(null);
		}

		public void updateValues () {
			prefs.put("foreground", EffectUtil.toString(colorEffect.getColor()));
			valuesPanel.removeAll();
			values = effect.getValues();
			for (Iterator iter = values.iterator(); iter.hasNext();)
				addValue((Value)iter.next());
		}

		public void addValue (final Value value) {
			JLabel valueNameLabel = new JLabel(value.getName() + ":");
			valuesPanel.add(valueNameLabel, new GridBagConstraints(0, -1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

			final JLabel valueValueLabel = new JLabel();
			valuesPanel.add(valueValueLabel, new GridBagConstraints(1, -1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
			valueValueLabel.setOpaque(true);
			if (value.getObject() instanceof java.awt.Color)
				valueValueLabel.setIcon(getColorIcon((java.awt.Color)value.getObject()));
			else
				valueValueLabel.setText(value.toString());

			valueValueLabel.addMouseListener(new MouseAdapter() {
				public void mouseEntered (MouseEvent evt) {
					valueValueLabel.setBackground(selectedColor);
				}

				public void mouseExited (MouseEvent evt) {
					valueValueLabel.setBackground(null);
				}

				public void mouseClicked (MouseEvent evt) {
					Object oldObject = value.getObject();
					value.showDialog();
					if (!value.getObject().equals(oldObject)) {
						effect.setValues(values);
						updateValues();
						updateFont();
					}
				}
			});
		}

		public ConfigurableEffect getEffect () {
			return effect;
		}

		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final EffectPanel other = (EffectPanel)obj;
			if (effect == null) {
				if (other.effect != null) return false;
			} else if (!effect.equals(other.effect)) return false;
			return true;
		}
	}

	static private class Splash extends JWindow {
		private final int minMillis;
		private final long startTime;

		public Splash (Frame frame, String imageFile, int minMillis) {
			super(frame);
			this.minMillis = minMillis;
			getContentPane().add(new JLabel(new ImageIcon(Splash.class.getResource(imageFile))), BorderLayout.CENTER);
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
			startTime = System.currentTimeMillis();
		}

		public void close () {
			final long endTime = System.currentTimeMillis();
			new Thread(new Runnable() {
				public void run () {
					if (endTime - startTime < minMillis) {
						addMouseListener(new MouseAdapter() {
							public void mousePressed (MouseEvent evt) {
								dispose();
							}
						});
						try {
							Thread.sleep(minMillis - (endTime - startTime));
						} catch (InterruptedException ignored) {
						}
					}
					EventQueue.invokeLater(new Runnable() {
						public void run () {
							dispose();
						}
					});
				}
			}, "Splash").start();
		}
	}

	public static void main (String[] args) throws Exception {
		LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for (int i = 0, n = lookAndFeels.length; i < n; i++) {
			if ("Nimbus".equals(lookAndFeels[i].getName())) {
				try {
					UIManager.setLookAndFeel(lookAndFeels[i].getClassName());
				} catch (Exception ignored) {
				}
				break;
			}
		}
		new Hiero();
	}
}
