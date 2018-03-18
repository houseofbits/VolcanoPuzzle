package com.volcanopuzzle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.volcanopuzzle.VolcanoPuzzle;

public class VolcanoPuzzleDesktop {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1066;config.height = 600;
		config.width = 1920;config.height = 1080;
		
		config.title = "VolcanoPuzzle";
		config.samples = 6;
		config.depth = 24;
		config.vSyncEnabled = true;
		config.fullscreen = true;
		
		new LwjglApplication(new VolcanoPuzzle(), config);
	}
}
