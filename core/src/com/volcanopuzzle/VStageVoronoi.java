package com.volcanopuzzle;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class VStageVoronoi extends InputListener {
	
	public Stage mainStage = null;
		
	VVoronoiShapeGenerator shapeGen;
	
	public VStageVoronoi(){

	}
	
	public void create(){
		
		mainStage = new Stage();
		
		shapeGen = new VVoronoiShapeGenerator();
		shapeGen.generate(15, 0.2f);


        Gdx.input.setInputProcessor(mainStage);		
		
		mainStage.setDebugAll(true);
//		mainStage.setDebugUnderMouse(true);
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
			
		shapeGen.render();

	}
	
	public void touchUp (InputEvent e, float x, float y, int pointer, int button) {
		Actor target = e.getListenerActor();

	}
    public boolean touchDown (InputEvent e, float x, float y, int pointer, int button) {
        return true;
    }
}
