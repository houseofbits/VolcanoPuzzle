package com.volcanopuzzle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.volcanopuzzle.vcore.VMain;

public class VolcanoPuzzle extends ApplicationAdapter {
	
//	public VStage stage = new VStage();
	public VMain main = new VMain();

	
	@Override
	public void create () {
		main.create();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		main.render();
	}
	
	@Override
	public void dispose () {

	}
}
