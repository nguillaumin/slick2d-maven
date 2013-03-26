package org.newdawn.slick.tools.hiero;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * A dialog to show progress of the distance map generation
 * 
 * @author kevin
 */
public class ProgressDialog extends JDialog implements ProgressListener {
	/** The progress message to display */
	private String message;
	/** The current message to display */
	private int current;
	/** The total message to display */
	private int total;
	
	/**
	 * Create a new dialog
	 * 
	 * @param frame The owner frame that should be blocked
	 */
	public ProgressDialog(JFrame frame) {
		super(frame, "Generating Distance Map", true);
	
		setSize(300,200);
		setResizable(false);
		setLocationRelativeTo(frame);
	}
	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.black);
		g.drawString("Generating Distance Map", 10, 100);
		g.drawString(message + "("+current+"/"+total+")", 20, 150);
	}

	/**
	 * @see org.newdawn.slick.tools.hiero.ProgressListener#reportProgress(java.lang.String, int, int)
	 */
	public void reportProgress(String type, int current, int total) {
		this.message = type;
		this.current = current;
		this.total = total;
		repaint();
	}
}
