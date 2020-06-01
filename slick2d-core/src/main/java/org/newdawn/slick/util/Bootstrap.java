package org.newdawn.slick.util;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.DisplayMode;
import org.newdawn.slick.Game;

/**
 * Utility class to wrap up starting a game in a single line
 * 
 * @author kevin
 */
public class Bootstrap {
	public static void runAsApplication(Game game, int width, int height, DisplayMode.Opt displayType) {
		try {
			AppGameContainer container = new AppGameContainer(game, width, height, displayType);
			container.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
