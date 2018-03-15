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
    ImageButton buttonMain = null;
    int difficultyLevels[] = {6,12,18,30,60,100};
    
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
		float sheight = mainStage.getHeight();
		float buttonIconSize = swidth * 0.08f;
		
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = VStaticAssets.Fonts.calibri18Font;		
        
        float posx = 100;
        
        for(int n=0; n<difficultyLevels.length; n++){
        	
        	Group g = new Group();
            ImageButton imgb = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-generic-on"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-generic"));
            imgb.setSize(buttonIconSize, buttonIconSize);    	
        	Label l = new Label(difficultyLevels[n]+" gabaliòi", labelStyle);
        	l.setPosition(0, -10);
        	l.setAlignment(Align.center);
        	l.setWidth(buttonIconSize);
        	g.addActor(imgb);
            g.addActor(l);
            g.setName("BUTTON_DIFF"+n);
            g.addListener(this);          
            g.setPosition(posx, 30); 
            buttonsGroup.addActor(g);
            
            posx += buttonIconSize + 30;
            
        }
        
        mainStage.addActor(buttonsGroup);
                
        buttonMain = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-main-on"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-main"));
        buttonMain.setSize(buttonIconSize, buttonIconSize);          
        buttonMain.setName("BUTTON_MAIN");
        buttonMain.setPosition(10, sheight - buttonIconSize - 10);
        buttonMain.addListener(this);  
        
        mainStage.addActor(buttonMain);
        
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
			
	//	shapeGen.render();

	}
	
	public void showInfoWindow(){
    	buttonsGroup.setVisible(true);
	}
	
	public void touchUp (InputEvent e, float x, float y, int pointer, int button) {
		
		Actor a = e.getListenerActor();
		if(a.getName().compareTo("BUTTON_MAIN") == 0){
			showInfoWindow();	
		}
		if(a.getName().contains("BUTTON_DIFF")){
			for(int n=0; n<difficultyLevels.length; n++){
				if(a.getName().compareTo("BUTTON_DIFF"+n) == 0){
		        	volcano.generateNewPuzzle(difficultyLevels[n]);
		        	buttonsGroup.setVisible(false);
		        	break;
				}				
			}
		}		
	}
    public boolean touchDown (InputEvent e, float x, float y, int pointer, int button) {
        return true;
    }
}
