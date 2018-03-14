package com.volcanopuzzle.vstage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.volcanopuzzle.vcore.VMain;
import com.volcanopuzzle.vcore.VStaticAssets;
import com.volcanopuzzle.vcore.VVoronoiShapeGenerator;

public class VStageMain extends InputListener {
	
	public VMain volcano;
	
	public Stage mainStage = null;
		
	public VVoronoiShapeGenerator shapeGen;
	
    public Group buttonsGroup = new Group();
	
    public int puzzleCurrentImageIndex = 0;
    
	public VStageMain(VMain v){
		volcano = v;
	}
	
	public void create(){
		
		mainStage = new Stage();
		
		shapeGen = new VVoronoiShapeGenerator();
		shapeGen.generate(15, 0.2f);
		
//		mainStage.setDebugAll(true);
//		mainStage.setDebugUnderMouse(true);
		
		float swidth = mainStage.getWidth();
		float buttonIconSize = swidth * 0.08f;
		
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = VStaticAssets.Fonts.calibri18Font;		
	
        
    	Group g = new Group();
        ImageButton imgb = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-generic-on"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-generic"));
        imgb.setSize(buttonIconSize, buttonIconSize);    	
    	Label l = new Label("6 pieces", labelStyle);
    	l.setPosition(0, -10);
    	l.setAlignment(Align.center);
    	l.setWidth(buttonIconSize);
    	g.addActor(imgb);
        g.addActor(l);
        g.setName("BUTTON1");
        g.addListener(this);          
        g.setPosition(10, 10); 
        buttonsGroup.addActor(g);

    	g = new Group();
        imgb = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-generic-on"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-generic"));
        imgb.setSize(buttonIconSize, buttonIconSize);    	
    	l = new Label("20 pieces", labelStyle);
    	l.setPosition(0, -10);
    	l.setAlignment(Align.center);
    	l.setWidth(buttonIconSize);
    	g.addActor(imgb);
        g.addActor(l);
        g.setName("BUTTON2");
        g.addListener(this);          
        g.setPosition(100, 10);       
        buttonsGroup.addActor(g);
        
        mainStage.addActor(buttonsGroup);
        
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
			
		shapeGen.render();

	}
	
	public void showInfoWindow(){
    	buttonsGroup.setVisible(true);		
	}
	
	public void touchUp (InputEvent e, float x, float y, int pointer, int button) {
		
		Actor a = e.getListenerActor();
		
        if(a.getName().compareTo("BUTTON1") == 0){
        	volcano.generateNewPuzzle(6);
        	buttonsGroup.setVisible(false);
        }  
        if(a.getName().compareTo("BUTTON2") == 0){
        	volcano.generateNewPuzzle(20);
        	buttonsGroup.setVisible(false);
        } 		
	}
    public boolean touchDown (InputEvent e, float x, float y, int pointer, int button) {
        return true;
    }
}
