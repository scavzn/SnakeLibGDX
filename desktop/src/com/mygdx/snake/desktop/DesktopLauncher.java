package com.mygdx.snake.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.snake.Snake;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Snake";
		config.width = 32 * 19;
		config.height = 32 * 17;
		config.resizable = false;
		new LwjglApplication(new Snake(), config);
	}
}
