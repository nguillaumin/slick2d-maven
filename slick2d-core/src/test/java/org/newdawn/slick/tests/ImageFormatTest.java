package org.newdawn.slick.tests;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.newdawn.slick.*;
import org.newdawn.slick.util.FileSystemLocation;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.util.ResourceLocation;

/**
 * Created by IntelliJ IDEA. User: Martin Karing Date: 05.03.12 Time: 23:19 To change this template use File | Settings
 * | File Templates.
 */
public class ImageFormatTest extends BasicGame {
  private Image rgbaImage;
  private Image rgbImage;
  private Image grayScaleImage;
  private Image grayScaleAlphaImage;

  private int rgbaImageSize;
  private int rgbImageSize;
  private int grayScaleImageSize;
  private int grayScaleAlphaImageSize;
  
  /**
   * Create a new basic game
   */
  public ImageFormatTest() {
    super("Image Format Test");
  }

  @Override
  public void init(GameContainer container) throws SlickException {
    // The following line is a hack to get this test working in IntelliJ IDEA 11
    try {
      ResourceLoader.addResourceLocation(new FileSystemLocation(new File("./trunk/Slick/")));
    } catch (final Exception ex) {
      // The hack failed... lets leave it at this.
    }

    rgbaImage = new Image("testdata/logo.png");
    rgbImage = new Image("testdata/logo_rgb.png");
    grayScaleImage = new Image("testdata/logo_luminance.png");
    grayScaleAlphaImage = new Image("testdata/logo_luminance_alpha.png");

    rgbaImageSize = rgbaImage.getTexture().getTextureData().length;
    rgbImageSize = rgbImage.getTexture().getTextureData().length;
    grayScaleImageSize = grayScaleImage.getTexture().getTextureData().length;
    grayScaleAlphaImageSize = grayScaleAlphaImage.getTexture().getTextureData().length;
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    // nothing to do
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    final int effectiveWidth = container.getWidth() - rgbaImage.getWidth();
    final int effectiveHeight = container.getHeight() - rgbaImage.getHeight();
    
    final int xStep = effectiveWidth / 4;
    final int yStep = effectiveHeight / 4;
    
    g.clear();
    g.setColor(Color.blue);
    g.fillRect(0, 0, 800, 600);

    g.setColor(Color.orange);
    
    int posX = xStep / 2;
    int posY = yStep / 2;
    g.drawImage(rgbaImage, posX, posY);
    g.drawString(Integer.toString(rgbaImageSize) + " Bytes", posX, posY - 15);
    posX += xStep;
    posY += yStep;

    g.drawImage(rgbImage, posX, posY);
    g.drawString(Integer.toString(rgbImageSize) + " Bytes", posX, posY - 15);
    posX += xStep;
    posY += yStep;

    g.drawImage(grayScaleAlphaImage, posX, posY);
    g.drawString(Integer.toString(grayScaleAlphaImageSize) + " Bytes", posX, posY - 15);
    posX += xStep;
    posY += yStep;

    g.drawImage(grayScaleImage, posX, posY);
    g.drawString(Integer.toString(grayScaleImageSize) + " Bytes", posX, posY - 15);
  }

  /**
   * Entry point to our test
   *
   * @param argv The arguments to pass into the test
   */
  public static void main(String[] argv) {
    boolean sharedContextTest = false;

    try {
      AppGameContainer container = new AppGameContainer(new ImageFormatTest());
      container.setDisplayMode(800,600,false);
      container.start();
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }
}
