package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class RotateInUseTest extends BasicGame {
	
	public static void main(String[] args) throws SlickException {
		new AppGameContainer(new RotateInUseTest(), 800, 600, false).start();
	}
	
	public RotateInUseTest() {
		super("Rotate In Use");
	}
	
	private Image sheet2, subImage;
	private SpriteSheet sheet1;
	private float rot1, rot2;
	
	@Override
	public void init(GameContainer container) throws SlickException {
		sheet1 = new SpriteSheet("testdata/dungeontiles.gif", 32, 32);
		
		//lets make this tile rotate around the top left
		sheet1.getSubImage(1, 2).setCenterOfRotation(0, 0);
		//and this one around top middle
		sheet1.getSubImage(3, 2).setCenterOfRotation(16, 0);
		
		sheet2 = new Image("testdata/logo.tga");
		subImage = sheet2.getSubImage(40, 40, 50, 50);
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		sheet1.startUse();
		
		sheet1.renderInUse(50, 50, 0, 0);
		sheet1.renderInUse(100, 50, 1, 2);
		sheet1.renderInUse(150, 50, 64, 64, 1, 2);
		sheet1.renderInUse(250, 50, 64, 64, rot1, 1, 2); //rotates around top left 
		sheet1.renderInUse(350, 50, 64, 64, rot2, 3, 2); //rotates around top middle
		sheet1.renderInUse(450, 50, rot1, 4, 4); //default; rotates around center
		
		sheet1.renderInUse(250, 50, 64, 64, rot1, 1, 2);
		
		sheet1.endUse();
		
		sheet2.startUse();
		subImage.drawEmbedded(100, 200, subImage.getWidth(), subImage.getHeight(), rot1);
		subImage.drawEmbedded(300, 200, subImage.getWidth()*4f, subImage.getHeight()*2f, rot2);
		sheet2.endUse();
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		rot1 += delta * 0.03f;
		rot2 += delta * 0.08f;
	}
	
	
}
