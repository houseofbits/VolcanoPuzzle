package com.volcanopuzzle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.volcanopuzzle.VolcanoPuzzle;

public class VolcanoPuzzleDesktop {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new VolcanoPuzzle(), config);
	}
}
