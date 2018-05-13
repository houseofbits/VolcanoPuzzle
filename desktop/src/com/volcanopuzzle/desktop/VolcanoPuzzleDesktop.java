package com.volcanopuzzle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.volcanopuzzle.VolcanoPuzzle;
import com.volcanopuzzle.vcore.VConfig;

public class VolcanoPuzzleDesktop {
	public static void main (String[] arg) {
		
		VConfig.loadConfig("config.json");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = (int)VConfig.get().resolution.x;
		config.height = (int)VConfig.get().resolution.y;
		
		config.title = "VolcanoPuzzle";
		config.samples = 6;
		config.depth = 24;
		config.vSyncEnabled = true;
		config.fullscreen = VConfig.get().fullScreen;
		
		new LwjglApplication(new VolcanoPuzzle(), config);
	}
}
