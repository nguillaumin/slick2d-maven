package org.newdawn.slick.tools.peditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.newdawn.slick.Color;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ConfigurableEmitter.ColorRecord;

/**
 * A panel allowing the configuration of the colour and alpha values of the 
 * particles.
 *
 * @author kevin
 */
public class ColorPanel extends ControlPanel {
	/** The editor used to define the change in color */
	private GradientEditor grad;
	/** True if we should ignore update events */
	private boolean blockUpdates = false;

	/** The selection for inherit the rendering settings */
	private JRadioButton inherit;
	/** The selection for using quads */
	private JRadioButton quads;
	/** The selection for using points */
	private JRadioButton points;
	/** The selection for using oriented quads */
	private JCheckBox oriented;
	/** The selection for additive blend mode */
	private JCheckBox additive;
	
	/** The panel displaying the starting alpha value */
	private ValuePanel startAlpha;
	/** The panel displayint the ending alpha value */
	private ValuePanel endAlpha;
	
	/**
	 * Create a new panel to allow particle colour configuration
	 */
	public ColorPanel() {
		grad = new GradientEditor();
		grad.setBorder(BorderFactory.createTitledBorder("Color Change"));
		grad.setBounds(0,0,280,100);
		add(grad);
		
		grad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateColors();
			}
		});
		
		yPos+=70;
		startAlpha= new ValuePanel("Starting Alpha",0,255,255,"The alpha value for particles at their birth",false);
		addValue("startAlpha", startAlpha );
		endAlpha= new ValuePanel("Ending Alpha",0,255,0,"The alpha value for particles at their death",false);
		addValue("endAlpha", endAlpha );

		// rendering panel
		JPanel renderingPrimitivePanel = new DefaultPanel();
		renderingPrimitivePanel.setLayout(new BoxLayout( renderingPrimitivePanel, BoxLayout.X_AXIS ));
		
		inherit = new JRadioButton("Inherit");
		quads = new JRadioButton("Quads");
		points = new JRadioButton("Points");

		ButtonGroup group = new ButtonGroup();
		group.add(inherit);
		group.add(quads);
		group.add(points);
		
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateRender();
			}
		};
		
		inherit.addActionListener(al);
		inherit.setOpaque(false);
		inherit.setSelected(true);
		renderingPrimitivePanel.add(inherit);
		
		quads.addActionListener(al);
		quads.setOpaque(false);
		renderingPrimitivePanel.add(quads);
		
		points.addActionListener(al);
		points.setOpaque(false);
		renderingPrimitivePanel.add(points);
		
		renderingPrimitivePanel.setBounds(0,yPos+15,280,45);
		add(renderingPrimitivePanel);
		yPos+=35;

		// rendering type panel
		JPanel renderingTypePanel = new DefaultPanel();
		renderingTypePanel.setLayout(new BoxLayout( renderingTypePanel, BoxLayout.X_AXIS ));
		
		oriented = new JCheckBox("Oriented Quad");
		oriented.addActionListener(al);
		oriented.setOpaque(false);
		renderingTypePanel.add( oriented );

		additive = new JCheckBox("Additive Blending");
		additive.addActionListener(al);
		additive.setOpaque(false);
		renderingTypePanel.add( additive );
		
		renderingTypePanel.setBounds(0,yPos+15,230,45);
		add(renderingTypePanel);
		yPos+=35;
	}
	
	/**
	 * Update the render setting
	 */
	private void updateRender() {
		if (inherit.isSelected())
		{
			emitter.usePoints = Particle.INHERIT_POINTS;
			oriented.setEnabled( true );
		}
		if (quads.isSelected())
		{
			emitter.usePoints = Particle.USE_QUADS;
			oriented.setEnabled( true );
		}
		if (points.isSelected())
		{
			emitter.usePoints = Particle.USE_POINTS;
			oriented.setEnabled( false );
			oriented.setSelected( false );
		}
		
		// oriented
		if( oriented.isSelected())
			emitter.useOriented= true;
		else
			emitter.useOriented= false;
		
		// additive blending
		if( additive.isSelected())
			emitter.useAdditive= true;
		else
			emitter.useAdditive= false;
	}
	
	/**
	 * Update the state of the emitter based on colours in the editor
	 */
	private void updateColors() {
		if (blockUpdates) {
			return;
		}
		emitter.colors.clear();
		for (int i=0;i<grad.getControlPointCount();i++) {
			float pos = grad.getPointPos(i);
			java.awt.Color col = grad.getColor(i);
			Color slick = new Color(col.getRed() / 255.0f, col.getGreen() / 255.0f, col.getBlue() / 255.0f, 1.0f);
			
			emitter.addColorPoint(pos, slick);
		}
	}
	
	/**
	 * @see org.newdawn.slick.tools.peditor.ControlPanel#linkEmitterToFields(org.newdawn.slick.particles.ConfigurableEmitter)
	 */
	protected void linkEmitterToFields(ConfigurableEmitter emitter) {
		blockUpdates = true;
		link(emitter.startAlpha, "startAlpha");
		link(emitter.endAlpha, "endAlpha");
		
		grad.clearPoints();
		Color start = ((ColorRecord) emitter.colors.get(0)).col;
		Color end = ((ColorRecord) emitter.colors.get(emitter.colors.size()-1)).col;
		
		grad.setStart(new java.awt.Color(start.r,start.g,start.b,1.0f));
		grad.setEnd(new java.awt.Color(end.r,end.g,end.b,1.0f));
		
		for (int i=1;i<emitter.colors.size()-1;i++) {
			float pos = ((ColorRecord) emitter.colors.get(i)).pos;
			Color col = ((ColorRecord) emitter.colors.get(i)).col;
			grad.addPoint(pos, new java.awt.Color(col.r,col.g,col.b,1.0f));
		}
		blockUpdates = false;
		
		if (emitter.usePoints == Particle.INHERIT_POINTS) {
			inherit.setSelected(true);
		}
		if (emitter.usePoints == Particle.USE_POINTS) {
			points.setSelected(true);
		}
		if (emitter.usePoints == Particle.USE_QUADS) {
			quads.setSelected(true);
		}
		oriented.setSelected( emitter.useOriented );
		additive.setSelected( emitter.useAdditive );
	}

	/**
	 * Get the panel controlling the alpha value of particles at the
	 * end of their life
	 * 
	 * @return The panel controlling the alpha value
	 */
	public ValuePanel getEndAlpha() {
		return endAlpha;
	}

	/**
	 * Get the panel controlling the alpha value of particles at the
	 * start of their life
	 * 
	 * @return The panel controlling the alpha value
	 */
	public ValuePanel getStartAlpha() {
		return startAlpha;
	}

}
