package ${package};

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * A game using Slick2d
 */
public class Game extends BasicGame {

    /** Screen width */
    private static final int WIDTH = 800;
    /** Screen height */
    private static final int HEIGHT = 600;
    
    /** A counter... */
    private int counter;

    public Game() {
        super("A Slick2d game");
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
        g.drawString("Hello, " + Integer.toString(counter) + "!", 50, 50);

    }

    @Override
    public void init(GameContainer container) throws SlickException {
        counter = 0;
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        counter++;
    }
    
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game());
        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.setForceExit(false);
        app.start();
    }

}
