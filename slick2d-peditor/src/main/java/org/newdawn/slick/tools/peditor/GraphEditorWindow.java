package org.newdawn.slick.tools.peditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitter.LinearInterpolator;

/**
 * A visual control to allow the editing of linear interpolated values effecting
 * the generated particles.
 * 
 * @author void
 */
public class GraphEditorWindow extends JPanel {
	/** The list of values placed in the graph */
	private Hashtable values;
	/** The properties listed in the top left */
	private JComboBox properties;
	/** The panel with the main controls on */
	private DefaultPanel top;
	/** The panel displaying the current graph */
	private GraphPanel panel;

	/** The x axis label */
	private JLabel valueXLabel;
	/** The y axis label */
	private JLabel valueYLabel;

	/** The minimum value for the graph control */
	private JSpinner minSpinner;
	/** The maxium value for the graph control */
	private JSpinner maxSpinner;

	/** The color for lines on the graph */
	private static Color COLOR_LINE = new Color(0xEEEEEE);
	/** The color for points on the graph */
	private static Color COLOR_POINT = new Color(0xFFCC66);
	/** The color for point outlines on the graph */
	private static Color COLOR_POINT_OUTLINE = new Color(0x444444);
	/** The color for selectd points on the graph */
	private static Color COLOR_SELECTED_POINT = new Color(0xFFFFFF);
	/** The color for selectd point outlines on the graph */
	private static Color COLOR_SELECTED_POINT_OUTLINE = new Color(0x000000);
	/** The color for the legend text on the graph */
	private static Color COLOR_LEGEND = new Color(0x000000);
	/** The color for the legend background on the graph */
	private static Color COLOR_LEGEND_BACKGROUND = new Color(0x555566);
	/** The color for the legend grid on the graph */
	private static Color COLOR_LEGEND_GRID = new Color(0x333344);
	/** The color for the legend grid crosses on the graph */
	private static Color COLOR_LEGEND_GRID_CROSS = new Color(0x222233);
	/** The color for the background on the graph */
	private static Color COLOR_BACKGROUND = new Color(0x444455);
	/** The color for the labels on the graph */
	private static Color COLOR_LABEL = new Color(0x000000);

	/** The x axis label */
	private static final String TEXT_CURRENT_X = "Time: ";
	/** The y axis label */
	private static final String TEXT_CURRENT_Y = "Value: ";

	/**
	 * Create a new graph editor window
	 */
	public GraphEditorWindow() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(COLOR_BACKGROUND);

		// window is divided into the top panel and the graph panel (see below)
		panel = new GraphPanel();
		top = createTopPanel();
		top.setEnabled(false);

		add(top);
		add(panel);

		// init the values
		values = new Hashtable();
	}

	/**
	 * Set the emitter that is being controlled
	 *  
	 * @param emitter The emitter that is configured by this panel
	 */
	public void setLinkedEmitter(ConfigurableEmitter emitter) {
		// set the title
		Window w = SwingUtilities.windowForComponent(this);
		if (w instanceof Frame)
			((Frame) w).setTitle("Whiskas Gradient Editor (" + emitter.name
					+ ")");

		// clear all values
		properties.removeAllItems();
		values.clear();
		panel.setInterpolator(null);
		enableControls();
	}

	/**
	 * Create the top panel
	 * 
	 * @return A fully configured component
	 */
	private DefaultPanel createTopPanel() {
		DefaultPanel top = new DefaultPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		top.setAlignmentX(Component.LEFT_ALIGNMENT);
		top.setPreferredSize(new Dimension(Short.MAX_VALUE, 40));

		top.add(Box.createRigidArea(new Dimension(5, 0)));

		// property label
		JLabel propLabel = new JLabel("Property");
		propLabel.setBounds(10, 20, 40, 20);
		propLabel.setForeground(COLOR_LABEL);
		top.add(propLabel);
		top.add(Box.createRigidArea(new Dimension(5, 0)));

		// add properties combobox
		properties = new JComboBox();
		properties.setMaximumSize(new Dimension(100, 20));
		properties.setMinimumSize(new Dimension(100, 20));
		properties.setPreferredSize(new Dimension(100, 20));
		properties.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				String item = (String) event.getItem();
				LinearInterpolator currentValue = (LinearInterpolator) values
						.get(item);
				panel.setInterpolator(currentValue);
			}
		});
		top.add(properties);
		top.add(Box.createRigidArea(new Dimension(10, 0)));

		// min label
		JLabel minLabel = new JLabel("Min");
		minLabel.setMaximumSize(new Dimension(100, 20));
		minLabel.setForeground(COLOR_LABEL);
		top.add(minLabel);
		top.add(Box.createRigidArea(new Dimension(5, 0)));

		// min spinner
		minSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
		minSpinner.setMaximumSize(new Dimension(100, 20));
		minSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireUpdated(e.getSource());
			}
		});
		top.add(minSpinner);
		top.add(Box.createRigidArea(new Dimension(5, 0)));

		// max label
		JLabel maxLabel = new JLabel("Max");
		maxLabel.setMaximumSize(new Dimension(100, 20));
		maxLabel.setForeground(COLOR_LABEL);
		top.add(maxLabel);
		top.add(Box.createRigidArea(new Dimension(5, 0)));

		// max spinner
		maxSpinner = new JSpinner(new SpinnerNumberModel(255, -1000, 1000, 1));
		maxSpinner.setMaximumSize(new Dimension(100, 20));
		maxSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireUpdated(e.getSource());
			}
		});
		top.add(maxSpinner);
		top.add(Box.createRigidArea(new Dimension(5, 0)));

		// spacer
		top.add(Box.createHorizontalGlue());

		// value x label
		valueXLabel = new JLabel(TEXT_CURRENT_X);
		valueXLabel.setMaximumSize(new Dimension(100, 20));
		valueXLabel.setForeground(COLOR_LABEL);
		top.add(valueXLabel);
		top.add(Box.createRigidArea(new Dimension(10, 0)));

		// value y label
		valueYLabel = new JLabel(TEXT_CURRENT_Y);
		valueYLabel.setMaximumSize(new Dimension(100, 20));
		valueYLabel.setForeground(COLOR_LABEL);
		top.add(valueYLabel);
		top.add(Box.createRigidArea(new Dimension(10, 0)));

		return top;
	}

	/**
	 * Fire a notification that the panel has been changed
	 * 
	 * @param control The source of the update
	 */
	private void fireUpdated(Object control) {
		if (control.equals(minSpinner)) {
			int minY = ((Integer) minSpinner.getValue()).intValue();
			if (minY < panel.getWorldMaxY()) {
				panel.setWorldMinY(minY);
				panel.makeSureCurveFits();
				panel.repaint();
			} else {
				minSpinner.setValue(new Integer((int) panel.getWorldMinY()));
			}
		} else if (control.equals(maxSpinner)) {
			int maxY = ((Integer) maxSpinner.getValue()).intValue();
			if (maxY > panel.getWorldMinY()) {
				panel.setWorldMaxY(maxY);
				panel.makeSureCurveFits();
				panel.repaint();
			} else {
				maxSpinner.setValue(new Integer((int) panel.getWorldMaxY()));
			}
		}
	}

	/**
	 * Register a configurable value with the graph panel
	 * 
	 * @param value The value to be registered
 	 * @param name The name to display for this value
	 */
	public void registerValue(LinearInterpolator value, String name) {
		// add to properties combobox
		properties.addItem(name);

		// add to value map
		values.put(name, value);

		// set as current interpolator
		panel.setInterpolator(value);

		// enable all input fields
		enableControls();
	}

	/**
	 * Enable the controls for the graph
	 */
	private void enableControls() {
		if (properties.getItemCount() > 0)
			top.setEnabled(true);
		else
			top.setEnabled(false);
	}

	/**
	 * Remove a configurable value from the graph
	 * 
	 * @param name The name of the value to be removed
	 */
	public void removeValue(String name) {
		properties.removeItem(name);
		values.remove(name);

		if (properties.getItemCount() >= 1) {
			properties.setSelectedIndex(0);
		} else {
			panel.setInterpolator(null);
		}

		enableControls();
	}

	/**
	 * Indicate that the first property should be displayed
	 */
	public void setFirstProperty() {
		if (properties.getItemCount() > 0) {
			properties.setSelectedIndex(0);

			LinearInterpolator currentValue = (LinearInterpolator) values
					.get(properties.getSelectedItem());
			panel.setInterpolator(currentValue);
		}
	}

	/**
	 * The actual panel the graph is drawn on
	 * 
	 * @author void
	 */
	public class GraphPanel extends JPanel {
		/** The list of points */
		private ArrayList curve;
		/** The value being configured */
		private LinearInterpolator value;

		/** The graph viewport minimum x value */
		private float viewportMinX;
		/** The graph viewport maximum x value */
		private float viewportMaxX;
		/** The graph viewport minimum y value */
		private float viewportMinY;
		/** The graph viewport maximum y value */
		private float viewportMaxY;
		/** The graph world minimum x value */
		private float worldMinX = 0.0f;
		/** The graph world maximum x value */
		private float worldMaxX = 1.0f;
		/** The graph world minimum y value */
		private float worldMinY = 0.0f;
		/** The graph world maximum y value */
		private float worldMaxY = 255.0f;

		/** The border size on the x axis */
		private float viewBorderX = 50;
		/** The border size of the y axis */
		private float viewBorderY = 25;

		/** The selected point for colour */
		private int colorSelectedPoint;
		/** The current point for colour */
		private int colorPoint;

		/** The mouse X position */
		private float mouseX;
		/** The mouse Y position */
		private float mouseY;

		/** The image displayed int he background of the panel */
		private BufferedImage backgroundImage;

		/**
		 * Create a new graph panel
		 */
		public GraphPanel() {
			setLayout(null);

			// load image
			backgroundImage = loadBackgroundImage();

			// mouse motion listener to forward events to the gradient
			this.addMouseMotionListener(new MouseMotionListener() {
				public void mouseDragged(MouseEvent e) {
					mouseDraggedEvent(e.getX(), e.getY(), e.isShiftDown());
				}

				public void mouseMoved(MouseEvent e) {
					mouseMovedEvent(e.getX(), e.getY());
				}
			});

			// mouse
			this.addMouseListener(new MouseListener() {
				public void mouseEntered(MouseEvent arg0) {
				}

				public void mouseExited(MouseEvent arg0) {
				}

				public void mousePressed(MouseEvent arg0) {
				}

				public void mouseReleased(MouseEvent arg0) {
				}

				public void mouseClicked(MouseEvent e) {
					mousePressedEvent(e.getX(), e.getY(),
							e.getButton() == MouseEvent.BUTTON1,
							e.getButton() == MouseEvent.BUTTON3,
							e.getButton() == MouseEvent.BUTTON2);
				}
			});
		}

		/**
		 * Load a background image and apply it (add some style to whiskas =))
		 * 
		 * @return The image to display in the background
		 */
		private BufferedImage loadBackgroundImage() {
			InputStream in = ParticleEditor.class.getClassLoader()
					.getResourceAsStream(
							"org/newdawn/slick/tools/peditor/data/charlie.png");
			BufferedImage backgroundImage = null;
			try {
				backgroundImage = ImageIO.read(in);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return backgroundImage;
		}

		/**
		 * Set the interpolator being configured
		 * 
		 * @param value The value to be configured
		 */
		public void setInterpolator(LinearInterpolator value) {
			if (value == null) {
				this.value = null;
				this.curve = null;
			} else {
				this.value = value;
				curve = convertToCurvePointCurve(value.getCurve());

				minSpinner.setValue(new Integer(value.getMin()));
				maxSpinner.setValue(new Integer(value.getMax()));

				worldMinY = value.getMin();
				worldMaxY = value.getMax();

				repaint();
			}
		}

		/**
		 * Change the current curve and make sure that the whole curve is in the
		 * worldMinY and worldMaxY interval
		 */
		private void makeSureCurveFits() {
			for (int i = 0; i < curve.size(); i++) {
				CurvePoint point = ((CurvePoint) curve.get(i));

				if (point.y > worldMaxY)
					point.y = worldMaxY;

				if (point.y < worldMinY)
					point.y = worldMinY;
			}
		}

		/**
		 * Find the first selected point in the given curve
		 * 
		 * @param curve The list of points to search
		 * @return The index of the selected point
		 */
		private int findSelectedPoint(ArrayList curve) {
			for (int i = 0; i < curve.size(); i++) {
				if (((CurvePoint) curve.get(i)).isSelected()) {
					return i;
				}
			}

			return -1;
		}

		/**
		 * Return the index of the previous point in the given curve
		 * 
		 * @param curve The list of points to search
		 * @param idx The index of the current point
		 * @return The index of the point that is previous to the point with the
		 *         given index
		 */
		private int getPrev(ArrayList curve, int idx) {
			if (idx == -1)
				return -1;

			if (idx <= 0 || idx >= curve.size() - 1)
				return -1;

			return idx - 1;
		}

		/**
		 * Return the index of the next point in the given curve
		 * 
		 * @param curve The list of points to search
		 * @param idx The index of the current point
		 * @return The index of the point that follows the point with the given
		 *         index
		 */
		private int getNext(ArrayList curve, int idx) {
			if (idx == -1)
				return -1;

			if (idx >= curve.size() - 1)
				return -1;

			return idx + 1;
		}

		/**
		 * Drag a curve point based on the reception of an event
		 * 
		 * @param x The new x position
		 * @param y The new y position
		 * @param shiftDown True if shift is pressed
		 */
		public void mouseDraggedEvent(int x, int y, boolean shiftDown) {
			if (!isActive())
				return;

			int idx = findSelectedPoint(curve);
			if (idx != -1) {
				CurvePoint selected = (CurvePoint) curve.get(idx);
				CurvePoint world = viewToWorld(new CurvePoint(x, y));

				// let the first and last points only change in y
				if (selected == curve.get(0)
						|| selected == curve.get(curve.size() - 1)) {
					selected.y = world.y;
				} else {
					selected.x = world.x;
					selected.y = world.y;
				}

				if (selected.x < worldMinX)
					selected.x = worldMinX;

				if (selected.x > worldMaxX)
					selected.x = worldMaxX;

				if (selected.y < worldMinY)
					selected.y = worldMinY;

				if (selected.y > worldMaxY)
					selected.y = worldMaxY;

				if (!shiftDown) {
					int prev = getPrev(curve, idx);
					int next = getNext(curve, idx);

					if (prev != -1) {
						if (selected.x < ((CurvePoint) curve.get(prev)).x)
							selected.x = ((CurvePoint) curve.get(prev)).x;
					}

					if (next != -1) {
						if (selected.x > ((CurvePoint) curve.get(next)).x)
							selected.x = ((CurvePoint) curve.get(next)).x;
					}
				}

				mouseX = x;
				mouseY = y;

				sortPoints();
				repaint();
			}
		}

		/**
		 * Notified that the mouse pointer has moved
		 * 
		 * @param x The x position of the new mouse position
		 * @param y The y position of the new mouse position
		 */
		public void mouseMovedEvent(int x, int y) {
			if (!isActive())
				return;

			CurvePoint posView = new CurvePoint(x, y);

			for (int i = 0; i < curve.size(); i++) {
				CurvePoint pWorld = (CurvePoint) curve.get(i);
				CurvePoint pView = worldToView((CurvePoint) curve.get(i));

				if ((posView.x > (pView.x - 12 / 2))
						&& (posView.x < (pView.x + 12 / 2))
						&& (posView.y > (pView.y - 12 / 2))
						&& (posView.y < (pView.y + 12 / 2))) {
					((CurvePoint) curve.get(i)).selected = true;
				} else {
					((CurvePoint) curve.get(i)).selected = false;
				}
			}

			mouseX = x;
			mouseY = y;

			repaint();
		}

		/**
		 * Notification that the mouse was pressed on the panel
		 * 
		 * @param leftButton True if the left button was pressed
		 * @param rightButton True if the right button was pressed
		 * @param middleButton True if the middle button was pressed
		 * @param x The x position of the mouse press
		 * @param y The y position of the mouse press
		 */
		public void mousePressedEvent(int x, int y, boolean leftButton,
				boolean rightButton, boolean middleButton) {
			if (!isActive())
				return;

			// right button -> remove selected point
			if (rightButton) {
				int idx = findSelectedPoint(curve);
				if (idx != -1) {
					// can't remove first and last points
					if (idx != 0 && idx != curve.size() - 1) {
						curve.remove(idx);
						repaint();
					}
				}
			} else if (leftButton) {
				int idx = findSelectedPoint(curve);
				if (idx == -1) {
					// only add points when nothing is selected
					if (x >= viewportMinX && x <= viewportMaxX
							&& y >= viewportMaxY && y <= viewportMinY) {
						CurvePoint p = viewToWorld(new CurvePoint(x, y));
						curve.add(1, p);

						sortPoints();
						repaint();
					}
				}
			}
		}

		/**
		 * Convert given control point from world to viewport coordinates
		 * 
		 * @param in The point to convert  
		 * @return A new control point representing the point in view space
		 */
		private CurvePoint worldToView(CurvePoint in) {
			float sx = (viewportMaxX - viewportMinX) / (worldMaxX - worldMinX);
			float sy = (viewportMaxY - viewportMinY) / (worldMaxY - worldMinY);

			float cx = -sx * worldMinX + viewportMinX;
			float cy = -sy * worldMinY + viewportMinY;

			return new CurvePoint(sx * in.x + cx, sy * in.y + cy);
		}

		/**
		 * Convert the given point in view space into world space
		 * 
		 * @param view The point to convert 
		 * @return A new control point that represent the point in world space
		 */
		public CurvePoint viewToWorld(CurvePoint view) {
			float sx = (viewportMaxX - viewportMinX) / (worldMaxX - worldMinX);
			float sy = (viewportMaxY - viewportMinY) / (worldMaxY - worldMinY);

			float cx = -sx * worldMinX + viewportMinX;
			float cy = -sy * worldMinY + viewportMinY;

			return new CurvePoint((view.x - cx) / sx, (view.y - cy) / sy);
		}

		/**
		 * Draw the legen of the graph
		 * 
		 * @param g The graphics context on which to draw the legend
		 * @param vx0 The view top-left x coordinate
		 * @param vy0 The view top-left y coordinate
		 * @param vx1 The view bottom-right x coordinate
		 * @param vy1 The view bottom-right y coordinate
		 */
		private void drawLegend(Graphics2D g, float vx0, float vy0, float vx1,
				float vy1) {
			g.setColor(COLOR_BACKGROUND);
			g.fillRect((int) vx0, (int) (vy1 - viewBorderY),
					(int) (vx1 - vx0 + viewBorderX),
					(int) (vy0 - vy1 + viewBorderY * 2));

			int legendLineMain = (int) viewBorderY;
			int legendLineMin = 8;

			g.setColor(COLOR_LEGEND_BACKGROUND);
			g.fillRect((int) vx0, (int) vy0, (int) (vx1 - vx0 + viewBorderX),
					(int) (vy0));

			// x line bottom (min y)
			g.setColor(COLOR_LEGEND);
			g.drawLine((int) vx0, (int) vy0, (int) (vx1 + viewBorderX),
					(int) vy0);
			g.drawLine((int) vx0, (int) vy0 + 1, (int) (vx1 + viewBorderX),
					(int) vy0 + 1);

			// let's first calculate how much space is available
			float space = vx1 - vx0;

			// now estimate that we need at least pixel per interval
			int intervalCountX = ((int) space / 50);
			if ((intervalCountX & 1) == 1)
				intervalCountX--;

			// x
			for (int x = 0; x < intervalCountX + 1; x++) {
				float xi0 = (worldMaxX - worldMinX) / intervalCountX
						* x;
				CurvePoint c = worldToView(new CurvePoint(xi0, 0));
				c.y = vy0;

				g.setColor(COLOR_LEGEND);
				g.drawLine((int) c.x, (int) c.y, (int) c.x, (int) c.y
						+ legendLineMain);

				g.setColor(COLOR_LEGEND_GRID);
				g.drawLine((int) c.x, (int) (viewportMaxY - viewBorderY),
						(int) (c.x), (int) (viewportMinY));

				String text = convertFloat(xi0 * 1.0f);

				g.setColor(COLOR_LEGEND);
				g
						.drawString(text, (int) c.x + 2, (int) (c.y
								+ viewBorderY - 2));

				if (x <= (intervalCountX - 1)) {
					for (int xsub = 0; xsub < 2; xsub++) {
						float xi1 = (worldMaxX - worldMinX)
								/ intervalCountX * (x + 1);
						CurvePoint ci = worldToView(new CurvePoint((xi0 + xi1)
								/ 2.0f * xsub, 0));
						ci.y = vy0;

						g.setColor(COLOR_LEGEND);
						g.drawLine((int) ci.x, (int) ci.y, (int) ci.x,
								(int) ci.y + legendLineMin);
					}
				}
			}

			// y
			g.setColor(COLOR_LEGEND_BACKGROUND);
			g.fillRect(0, (int) (vy1 - viewBorderY), (int) vx0,
					(int) (vy0 + viewBorderY));

			// let's first calculate how much space is available
			space = vy0 - vy1;

			// now estimate that we need at least pixel per interval
			int intervalCountY = ((int) space / 30);
			if ((intervalCountY & 1) == 1)
				intervalCountY--;

			// y
			int legendLineMainY = 50;
			for (int y = 0; y < intervalCountY + 1; y++) {
				float yi0 = (worldMaxY - worldMinY) / intervalCountY
						* y + worldMinY;
				CurvePoint c = worldToView(new CurvePoint(0, yi0));

				// legend line
				g.setColor(COLOR_LEGEND);
				g.drawLine((int) c.x - legendLineMainY, (int) c.y, (int) c.x,
						(int) c.y);

				// grid line
				g.setColor(COLOR_LEGEND_GRID);
				g.drawLine((int) c.x, (int) (c.y), (int) (c.x + viewportMaxX),
						(int) (c.y));

				// legend text
				String text = convertFloat(yi0);

				g.setColor(COLOR_LEGEND);
				g.drawString(text, (int) vx0
						- g.getFontMetrics().stringWidth(text) - 2,
						(int) (c.y - 2));
			}

			// x line top (max y)
			g.setColor(COLOR_LEGEND);
			g.drawLine((int) vx0, (int) (vy1), (int) (vx1 + viewBorderX),
					(int) (vy1));

			// left
			g.drawLine((int) vx0, (int) vy0, (int) vx0,
					(int) (vy1 - viewBorderY));

			// right
			g.drawLine((int) vx1, (int) vy0, (int) vx1,
					(int) (vy1 - viewBorderY));
		}

		/**
		 * Convert the list of the given CurvePoint
		 * 
		 * @param curve The list of points to convert
		 * @return The new list of points in Point2D format
		 */
		private ArrayList convertToPoint2DCurve(ArrayList curve) {
			ArrayList bla = new ArrayList();
			for (int j = 0; j < curve.size(); j++) {
				CurvePoint c = (CurvePoint) curve.get(j);
				bla.add(new Vector2f(c.getX(), c.getY()));
			}
			return bla;
		}

		/**
		 * Convert a point 2d curve into a curve point curve
		 * 
		 * @param point2DCurve The list of points to convert
		 * @return The list of points in CurvePoint format
		 */ 
		private ArrayList convertToCurvePointCurve(ArrayList point2DCurve) {
			ArrayList curvePointCurve = new ArrayList();
			for (int j = 0; j < point2DCurve.size(); j++) {
				Vector2f c = (Vector2f) point2DCurve.get(j);
				curvePointCurve.add(new CurvePoint(c.getX(), c.getY()));
			}
			return curvePointCurve;
		}

		/**
		 * Paint the display
		 * 
		 * @param g The graphics context to draw onto
		 * @param vx0 The top-left x coordinate
		 * @param vy0 The top-left y coordinate
		 * @param vx1 The bottom-right x coordinate
		 * @param vy1 The bottom-right y coordinate
		 */
		public void paint(Graphics2D g, float vx0, float vy0, float vx1, float vy1) {
			if (!isActive()) {
				g.drawImage(backgroundImage, 0, (int) vy1
						- backgroundImage.getHeight(), null);
				return;
			}

			viewportMinX = vx0 + viewBorderX;
			viewportMaxX = vx1 - viewBorderX;
			viewportMinY = vy1 - viewBorderY;
			viewportMaxY = vy0 + viewBorderY;

			drawLegend(g, viewportMinX, viewportMinY, viewportMaxX,
					viewportMaxY);

			g.setColor(COLOR_LEGEND_GRID);
			g.drawLine((int) vx0, (int) vy0, (int) (vx1), (int) vy0);

			int[] xPoints = new int[curve.size()];
			int[] yPoints = new int[curve.size()];

			for (int i = 0; i < curve.size(); i++) {
				CurvePoint p = worldToView((CurvePoint) curve.get(i));
				xPoints[i] = (int) (p.x);
				yPoints[i] = (int) (p.y);
			}

			g.setColor(COLOR_LINE);
			g.drawPolyline(xPoints, yPoints, curve.size());

			int width = 6;
			int height = 6;
			for (int i = 0; i < curve.size(); i++) {
				Color color;
				Color colorOutline;
				int off;

				if (((CurvePoint) curve.get(i)).selected) {
					color = COLOR_SELECTED_POINT;
					colorOutline = COLOR_SELECTED_POINT_OUTLINE;
					off = 1;
				} else {
					color = COLOR_POINT;
					colorOutline = COLOR_POINT_OUTLINE;
					off = 0;
				}

				g.setColor(colorOutline);
				g.fillRect(xPoints[i] - width / 2 - off, yPoints[i] - height
						/ 2 - off, width + off * 2, height + off * 2);

				g.setColor(color);
				g.fillRect(xPoints[i] - width / 2 - off + 1, yPoints[i]
						- height / 2 - off + 1, width + off * 2 - 2, height
						+ off * 2 - 2);

				if (value != null && mouseX >= viewportMinX
						&& mouseX <= viewportMaxX && mouseY >= vy0
						&& mouseY <= vy1) {
					value.setCurve(convertToPoint2DCurve(curve));
					value.setMin((int) worldMinY);
					value.setMax((int) worldMaxY);

					// draw little cross where we are
					CurvePoint a = viewToWorld(new CurvePoint(mouseX, 0));
					float b = value.getValue(a.getX());

					CurvePoint crossPoint = new CurvePoint(a.getX(), b);
					CurvePoint p = worldToView(crossPoint);

					// draw cross-line where we are
					g.setColor(COLOR_LEGEND_GRID_CROSS);
					g.drawLine((int) p.getX(), (int) vy0, (int) p.getX(),
							(int) vy1);

					// update mouse position labels
					valueXLabel.setText(TEXT_CURRENT_X
							+ convertFloat(crossPoint.getX()));
					valueYLabel.setText(TEXT_CURRENT_Y
							+ convertFloat(crossPoint.getY()));

				}
			}
		}

		/**
		 * Sort the control points based on their position
		 */
		private void sortPoints() {
			final CurvePoint firstPt = (CurvePoint) curve.get(0);
			final CurvePoint lastPt = (CurvePoint) curve.get(curve.size() - 1);

			Comparator compare = new Comparator() {
				public int compare(Object firstO, Object secondO) {
					CurvePoint first = (CurvePoint) firstO;
					CurvePoint second = (CurvePoint) secondO;

					if (first == firstPt) {
						return -1;
					}
					if (second == lastPt) {
						return -1;
					}

					if (first.x < second.x)
						return -1;
					else if (first.x > second.x)
						return 1;
					else
						return 0;
				}
			};

			Collections.sort(curve, compare);
		}

		/**
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g1d) {
			super.paintComponent(g1d);
			Graphics2D g = (Graphics2D) g1d;

			g.setColor(new Color(0x303040));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			paint(g, 0, 0, this.getWidth(), this.getHeight());
		}

		/**
		 * Check if this graph panel is currently being used
		 * 
		 * @return True if the graph panel is current being used
 		 */
		private boolean isActive() {
			return (curve != null);
		}

		/**
		 * Convert a float to a string format
		 * 
		 * @param f The float to convert 
		 * @return The string formatted from the float
		 */
		private String convertFloat(float f) {
			NumberFormat format = NumberFormat.getInstance();
			format.setMinimumFractionDigits(2);
			format.setMaximumFractionDigits(2);
			format.setMinimumIntegerDigits(1);
			format.setMaximumIntegerDigits(5);
			return format.format(f);
		}

		/**
		 * A point on the curve
		 * 
		 * @author void
		 */
		private class CurvePoint {
			/** The x coordinate of the point */
			public float x;
			/** The y coordinate of the point */
			public float y;
			/** True if this point is selected */
			public boolean selected;

			/**
			 * Create a new curve point
			 * 
			 * @param x The x coordinate of the curve point
			 * @param y The y coordinate of the curve point
			 */
			public CurvePoint(float x, float y) {
				this.x = x;
				this.y = y;
				selected = false;
			}

			/**
			 * Check if this point is selected
			 * 
			 * @return True if the point is selected
			 */
			public boolean isSelected() {
				return selected;
			}

			/**
			 * Get the x coordinate of this point
			 * 
			 * @return The x coordinate of this point
			 */
			public float getX() {
				return x;
			}

			/**
			 * Get the y coordinate of this point
			 * 
			 * @return The y coordinate of this point
			 */
			public float getY() {
				return y;
			}
		}

		/**
		 * Get the world minimum y value
		 * 
		 * @return The world minimum y value
		 */
		public float getWorldMinY() {
			return worldMinY;
		}

		/**
		 * Set the world minimum y value
		 * 
		 * @param worldMinY The world minimum y value
		 */
		public void setWorldMinY(float worldMinY) {
			this.worldMinY = worldMinY;
		}

		/**
		 * Get the world maximum y value
		 * 
		 * @return The world maximum y value
		 */
		public float getWorldMaxY() {
			return worldMaxY;
		}

		/**
		 * Set the world maximum y value
		 * 
		 * @param worldMaxY The world maximum y value
		 */
		public void setWorldMaxY(float worldMaxY) {
			this.worldMaxY = worldMaxY;
		}
	}

	/**
	 * Simple test case for the gradient painter
	 * 
	 * @param argv
	 *            The arguments supplied at the command line
	 */
	public static void main(String[] argv) {
		JFrame frame = new JFrame("Whiskas Gradient Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setBackground(Color.CYAN);
		frame.getContentPane().add(main);

		//
		GraphEditorWindow bottom = new GraphEditorWindow();

		ArrayList curve = new ArrayList();
		curve.add(new Vector2f(0.0f, 0.0f));
		curve.add(new Vector2f(1.0f, 255.0f));
		LinearInterpolator test = new ConfigurableEmitter("bla").new LinearInterpolator(
				curve, 0, 255);
		bottom.registerValue(test, "Test");

		curve = new ArrayList();
		curve.add(new Vector2f(0.0f, 255.0f));
		curve.add(new Vector2f(1.0f, 0.0f));
		test = new ConfigurableEmitter("bla").new LinearInterpolator(curve, 0,
				255);
		bottom.registerValue(test, "Test 2");

		main.add(bottom);

		frame.pack();
		frame.setVisible(true);
		frame.setSize(600, 300);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setVisible(true);
	}
}
