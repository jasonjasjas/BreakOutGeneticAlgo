package com.jason.breakout.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jason.breakout.BreakOut;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "BreakOut Clone";
		config.height = 850;
		config.width = 731+200;
		new LwjglApplication(new BreakOut(), config);
	}
}
