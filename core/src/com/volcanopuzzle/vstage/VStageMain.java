package com.volcanopuzzle.vstage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.volcanopuzzle.vcore.VMain;
import com.volcanopuzzle.vcore.VStaticAssets;
import com.volcanopuzzle.vcore.VVoronoiShapeGenerator;

public class VStageMain extends InputListener {
	
	public VMain volcano;
	
	public Stage mainStage = null;		
	public VVoronoiShapeGenerator shapeGen;
    public ButtonGroup<ImageButton>	difficlultyLevelsButtonGroup = new ButtonGroup<ImageButton>();	
    
    ImageButton buttonMain = null;
    ImageButton buttonClose = null;
    float buttonOffset = 10;
    float buttonCloseCenterX = 0;
    float imageIconSize = 115;
    float imageIconMargin = 5;
    float checkboxWidth = 170;    
    float selectorTableWidth = 650;
    float buttonIconSize = 50;
    float titleMarginTop = 50;
    float titleMarginBottom = 50;
    
    Group groupNext = null;    
    public Table	selectorTable = null;
    
    int difficultyLevels[] = {10,25,40};
    int currentDifficultyLevelIndex = 0;
    
    public boolean puzzleComplete = false;
    
	public VStageMain(VMain v){
		volcano = v;
	}
	
	class ImageIcon extends ImageButton{
		public ImageIcon(Drawable a){
			super(a);
		}		
		public ImageIcon(Drawable a, Drawable b){
			super(a,b);
		}
		public int imageIndex = 0;		
	}
	
	public void create(){
		
		mainStage = new Stage();
		
		shapeGen = new VVoronoiShapeGenerator();
		shapeGen.generate(15, 0.2f);
		
//		mainStage.setDebugAll(true);
//		mainStage.setDebugUnderMouse(true);
		
		float swidth = mainStage.getWidth();
		float sheight = mainStage.getHeight();
		
		buttonIconSize = swidth * 0.08f;		
		buttonOffset = swidth * 0.005f;
	    imageIconSize = swidth * 0.11f;
	    imageIconMargin = swidth * 0.005f;
	    checkboxWidth = swidth * 0.17f;
	    selectorTableWidth = swidth * 0.55f;
	    titleMarginTop = swidth * 0.03f;
	    titleMarginBottom = swidth * 0.01f;
		buttonCloseCenterX = (swidth / 2) - (buttonIconSize/2);
	    
        Pixmap labelColor = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        labelColor.setColor(0, 0, 0, 0.8f);
        labelColor.fill();		
		
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = VStaticAssets.Fonts.calibri18Font;		
        
        selectorTable = new Table();     
        selectorTable.background(new Image(new Texture(labelColor)).getDrawable());
        selectorTable.setWidth(selectorTableWidth);
        selectorTable.setX((swidth - selectorTableWidth) / 2);
        selectorTable.setHeight(sheight);
        
        Label headerText= new Label(VStaticAssets.Text.selectorTitle, new Label.LabelStyle(VStaticAssets.Fonts.futuraFont, Color.WHITE));
        headerText.setWrap(true);
        headerText.setAlignment(Align.top);
        
        selectorTable.add(headerText)
        			.width(selectorTableWidth)
        			.height(headerText.getPrefHeight())
        			.colspan(difficultyLevels.length)
        			.padTop(titleMarginTop)
        			.padBottom(titleMarginBottom);
        selectorTable.row();
        
        
        Table imageIconTable = new Table();
        imageIconTable.setWidth(selectorTableWidth);
        		
        addImageIconTable(imageIconTable, 0);              
        addImageIconTable(imageIconTable, 1);              
        addImageIconTable(imageIconTable, 2);              
        addImageIconTable(imageIconTable, 3);                      
        imageIconTable.row().padTop(imageIconMargin);
        addImageIconTable(imageIconTable, 4);              
        addImageIconTable(imageIconTable, 5);              
        addImageIconTable(imageIconTable, 6);              
        addImageIconTable(imageIconTable, 7);                      
        imageIconTable.row().padTop(imageIconMargin);
        addImageIconTable(imageIconTable, 8);              
        addImageIconTable(imageIconTable, 9);              
        addImageIconTable(imageIconTable, 10);              
        addImageIconTable(imageIconTable, 11);                      
        imageIconTable.row().padTop(imageIconMargin);        
        
        selectorTable.add(imageIconTable)
					.width(selectorTableWidth)
					.height(imageIconTable.getPrefHeight())
					.colspan(difficultyLevels.length);        
        selectorTable.row();
        
        for(int n=0; n<difficultyLevels.length; n++){
        	
        	Group g = new Group();
            ImageButton imgb = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable(difficultyLevels[n]+"off"), 
            									VStaticAssets.GUI.buttonsSkin.getDrawable(difficultyLevels[n]+"over"),
            									VStaticAssets.GUI.buttonsSkin.getDrawable(difficultyLevels[n]+"on"));
            float scale = imgb.getWidth() / imgb.getHeight();
            imgb.setSize(checkboxWidth, checkboxWidth / scale);    	
        	g.addActor(imgb);
            g.setName("BUTTON_DIFF"+n);
            g.addListener(this);  
            g.setWidth(imgb.getWidth());
            g.setHeight(imgb.getHeight());            
            selectorTable.add(g).uniform();
            difficlultyLevelsButtonGroup.add(imgb);
            
        }
        difficlultyLevelsButtonGroup.setMaxCheckCount(1);
        
        selectorTable.row().padTop(20);
        selectorTable.add()
        			.colspan(difficultyLevels.length)
        			.expandY();
        
        mainStage.addActor(selectorTable);
        
        
        buttonMain = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-main-light"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-main-light-on"));
        buttonMain.setSize(buttonIconSize, buttonIconSize);          
        buttonMain.setName("BUTTON_MAIN");
        buttonMain.setPosition(buttonOffset, buttonOffset);
        buttonMain.addListener(this);          
        mainStage.addActor(buttonMain);
                        
        buttonClose = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-close"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-close-on"));
        buttonClose.setSize(buttonIconSize, buttonIconSize);          
        buttonClose.setName("BUTTON_CLOSE");
        buttonClose.setPosition(buttonOffset, buttonOffset);
        buttonClose.addListener(this);        
        mainStage.addActor(buttonClose);
        
        groupNext = new Group();
        ImageButton buttonNext = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-next"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-next-on"));
        buttonNext.setSize(buttonIconSize, buttonIconSize);          
        buttonNext.setName("BUTTON_NEXT");
        buttonNext.setPosition(swidth - buttonIconSize - buttonOffset, buttonOffset);
        buttonNext.addListener(this);          
        groupNext.addActor(buttonNext);
        
        Label labelButtonNext= new Label(VStaticAssets.Text.continueButtonTitle, new Label.LabelStyle(VStaticAssets.Fonts.futuraFont, Color.WHITE));
        float labely = buttonOffset + ((buttonIconSize - labelButtonNext.getPrefHeight()) / 2);
        float labelx = buttonNext.getX() - labelButtonNext.getPrefWidth();        		
        labelButtonNext.setPosition(labelx, labely);        
        groupNext.addActor(labelButtonNext);
        
        ImageButton buttonZoomIn = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-zoomin"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-zoomin-on"));
        buttonZoomIn.setSize(buttonIconSize, buttonIconSize);          
        buttonZoomIn.setName("BUTTON_ZOOM_IN");
        buttonZoomIn.setPosition(swidth - buttonIconSize - buttonOffset, sheight - buttonIconSize - buttonOffset);
        buttonZoomIn.addListener(this); 
        groupNext.addActor(buttonZoomIn);
        
        ImageButton buttonZoomOut = new ImageButton(VStaticAssets.GUI.buttonsSkin.getDrawable("button-zoomout"), VStaticAssets.GUI.buttonsSkin.getDrawable("button-zoomout-on"));
        buttonZoomOut.setSize(buttonIconSize, buttonIconSize);          
        buttonZoomOut.setName("BUTTON_ZOOM_OUT");
        buttonZoomOut.setPosition(buttonOffset, sheight - buttonIconSize - buttonOffset);
        buttonZoomOut.addListener(this);   
        groupNext.addActor(buttonZoomOut);
        
        //groupNext.setVisible(false);
        setNextGroupVisibility(false);
        
        mainStage.addActor(groupNext);
        
        currentDifficultyLevelIndex = getSelectedDifficulty();
        setSelectorTableVisibility(false);
	}
	
	private void addImageIconTable(Table table, int id){    		
		ImageIcon ib = new ImageIcon(VStaticAssets.GUI.imageIconsSkin.getDrawable("icon"+id),
									VStaticAssets.GUI.imageIconsSkin.getDrawable("iconOn"+id));
        ib.setName("ICON"+id);
        ib.imageIndex = id;
        table.add(ib)
					.width(imageIconSize)
					.height(imageIconSize)
					.pad(imageIconMargin);
        ib.addListener(this);    
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
			
	//	shapeGen.render();

	}
	
	public void setNextGroupVisibility(boolean visible){
		if(!visible){
			groupNext.addAction(Actions.sequence(
						Actions.show(),
						Actions.fadeOut(0.5f),
						Actions.hide()
					));
		}else{
			groupNext.addAction(Actions.sequence(
					Actions.show(),
					Actions.fadeIn(0.5f)
				));
		}
	}
	
	public void onPuzzleComplete(){
//		groupNext.setVisible(true);
		setNextGroupVisibility(true);
		puzzleComplete = true;
	}
	
	public boolean isLocked(){
		return selectorTable.isVisible();
	}
	
	public int getSelectedDifficulty(){		
		int idx = difficlultyLevelsButtonGroup.getCheckedIndex();		
		if(idx >= 0 && idx < difficultyLevels.length){
			return difficultyLevels[idx];
		}		
		return difficultyLevels[0];
	}
	public void setSelectorTableVisibility(boolean visible){
		if(!visible){			
			selectorTable.addAction(Actions.sequence(Actions.touchable(Touchable.disabled),
														Actions.fadeOut(0.4f), 
														Actions.hide()));		
			buttonClose.addAction(Actions.sequence(Actions.touchable(Touchable.disabled),
													Actions.moveTo(buttonOffset, buttonOffset, 0.4f, Interpolation.swingIn),
													Actions.fadeOut(0.3f),
													Actions.hide()
													));
			buttonMain.addAction(Actions.sequence(Actions.delay(0.4f),
					Actions.show(),
					Actions.fadeIn(0.3f),
					Actions.touchable(Touchable.enabled)
					));
			if(puzzleComplete)setNextGroupVisibility(true);//groupNext.setVisible(true);										
		}else{
			selectorTable.addAction(Actions.sequence(Actions.show(), 
														Actions.fadeIn(0.4f), 
														Actions.touchable(Touchable.enabled)));						
			buttonClose.addAction(Actions.sequence(Actions.show(),
													Actions.fadeIn(0.3f),
													Actions.moveTo(buttonCloseCenterX, buttonOffset, 0.4f, Interpolation.swingIn),
													Actions.touchable(Touchable.enabled)
													));
			buttonMain.addAction(Actions.sequence(Actions.touchable(Touchable.disabled),
					Actions.fadeOut(0.3f),
					Actions.hide()			
					));
			//groupNext.setVisible(false);
			setNextGroupVisibility(false);
		}
	}
	public void toggleSelectorTable(){
		setSelectorTableVisibility(!selectorTable.isVisible());
	}
	
	public void touchUp (InputEvent e, float x, float y, int pointer, int button) {
		
		Actor a = e.getListenerActor();
		if(a.getName().compareTo("BUTTON_MAIN") == 0){
			toggleSelectorTable();	
		}
		if(a.getName().compareTo("BUTTON_CLOSE") == 0){
			setSelectorTableVisibility(false);	
		}		
		if(a.getName().compareTo("BUTTON_NEXT") == 0){
			volcano.generateNewPuzzle(getSelectedDifficulty(), -1);
			//groupNext.setVisible(false);
			setNextGroupVisibility(false);
			puzzleComplete = false;
		}	
		if(a.getName().contains("BUTTON_DIFF")){
			int idx = getSelectedDifficulty();
			if(idx != currentDifficultyLevelIndex){
				volcano.generateNewPuzzle(getSelectedDifficulty(), volcano.currentImage);
				//groupNext.setVisible(false);
				setNextGroupVisibility(false);
				currentDifficultyLevelIndex = idx;
				puzzleComplete = false;
			}
		}
		if(a.getName().compareTo("BUTTON_ZOOM_IN") == 0){
			volcano.completeZoomIn();
		}	
		if(a.getName().compareTo("BUTTON_ZOOM_OUT") == 0){
			volcano.completeZoomOut();
		}	
		if(a.getClass() == ImageIcon.class){
			ImageIcon piece = (ImageIcon)a;	
			volcano.generateNewPuzzle(getSelectedDifficulty(), piece.imageIndex);
			setSelectorTableVisibility(false);
			setNextGroupVisibility(false);
			puzzleComplete = false;			
		}			
	}
    public boolean touchDown (InputEvent e, float x, float y, int pointer, int button) {
        return true;
    }
}
