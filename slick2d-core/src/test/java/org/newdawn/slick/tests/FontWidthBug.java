package org.newdawn.slick.tests;


import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AngelCodeFont.Glyph;

public class FontWidthBug extends BasicGame {

   private Image image;
   private AngelCodeFont font;

   public FontWidthBug() {
      super("FontWidthBug");
   }

   public void render(GameContainer container, Graphics g) throws SlickException {
      image.draw(100,100);
      
      Glyph fg = font.getGlyph('w');
      fg.image.draw(100, 260);
      g.drawRect(100, 260, fg.width+fg.xoffset, fg.height);
      
//      g.drawString("width: "+(fg.width-fg.xoffset), 100, 300);
//      g.drawString("width 2: "+font.getWidth("b"), 100, 320);
   }

   public void init(GameContainer container) throws SlickException {
      font = (AngelCodeFont)container.getDefaultFont();//new AngelCodeFont("testdata/hiero.fnt", "testdata/hiero.png");
      
      String text = "w1a|";
      
      image = new Image(font.getWidth(text), font.getLineHeight());
      Graphics g = image.getGraphics();
      font.drawString(0, 0, text);
      g.setColor(Color.red);
      g.drawRect(0, 0, image.getWidth()-1, image.getHeight()-1);
      g.flush();
      
      
   }

   public void update(GameContainer container, int delta) throws SlickException {}

   public static void main(String[] args) {
      try {
         new AppGameContainer(new FontWidthBug()).start();
      } catch (SlickException e) {
         e.printStackTrace();
      }
   }
}