package com.volcanopuzzle.vcore;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.volcanopuzzle.vcamera.VCamera;
import com.volcanopuzzle.vcamera.VCameraPresetCollection.PresetsIdentifiers;
import com.volcanopuzzle.vshaders.VDefaultShaderProvider;
import com.volcanopuzzle.vshaders.VShadowShaderProvider;
import com.volcanopuzzle.vshaders.VTextureRender;
import com.volcanopuzzle.vstage.VStageMain;

public class VMain {
	
	public enum GameStates{
		MAIN_VIEW,	
		PUZZLE,			//Play puzzle view
		FINISHED,		//Puzzle finished view
	}
	public GameStates	gameState = GameStates.PUZZLE;
	public PerspectiveCamera lightView;
	public VCamera	camera;
	public Environment environment = new Environment();
	public VInputProcessor inputProcessor;
	public VStageMain mainStage = new VStageMain(this);	
	public VPieceDistributionBuilder meshBuilder = new VPieceDistributionBuilder();
	public VPuzzleBackgroundRenderable backgroundRenderable = null;	
	public VPuzzleTableRenderable tableRenderable = null;	
	Array<VPuzzlePieceRenderable> puzzlePieces = new Array<VPuzzlePieceRenderable>();
	public int currentImage = 0;
	
	public VDefaultShaderProvider depthShader = null;
	public VShadowShaderProvider puzzlePieceShader = null;
	public VShadowShaderProvider imageBackgroundPieceShader = null;
	public VShadowShaderProvider tableShader = null;	
	
	public VTextureRender	lightDepthTexture;
	
	private VPuzzlePieceRenderable dragPiece = null;
	private Vector3 dragOffset = new Vector3();
	private Vector3 dragIntersection = new Vector3();	
	
	public void create(){
		VStaticAssets.Init();
		inputProcessor = new VInputProcessor(this);
		
		//depthShader = new VDefaultShaderProvider(this);
		depthShader = new VDefaultShaderProvider(this, "shaders/depth.vertex.glsl", "shaders/depth.fragment.glsl");	
		puzzlePieceShader = new VShadowShaderProvider(this, "shaders/piece.vertex.glsl", "shaders/piece.fragment.glsl");
		imageBackgroundPieceShader = new VShadowShaderProvider(this, "shaders/img.vertex.glsl", "shaders/img.fragment.glsl");
		tableShader = new VShadowShaderProvider(this, "shaders/table.vertex.glsl", "shaders/table.fragment.glsl");
		
		lightDepthTexture = new VTextureRender(this);
		
		mainStage.create();
		
		camera = new VCamera(this);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.5f,  -1, -0.8f, 1));		

//        Gdx.input.setInputProcessor(new InputMultiplexer(mainStage.mainStage, camController, inputProcessor));
        Gdx.input.setInputProcessor(new InputMultiplexer(mainStage.mainStage, inputProcessor.gestureDetector, inputProcessor));        
        
        backgroundRenderable = new VPuzzleBackgroundRenderable(this);
        tableRenderable = new VPuzzleTableRenderable(this);
        
		createLight();

		
        generateNewPuzzle(6);        
	}
	public void createLight(){
		
		lightView = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		lightView.near = 1f;
		lightView.far = 200;
	
		Quaternion q = new Quaternion();
		q.setEulerAngles(180, 95, 0.0f);
		
		Vector3 nm = new Vector3(0,0,1);
		nm = q.transform(nm).nor();	
		lightView.direction.set(nm);		
		nm.scl(100);
		
		lightView.position.set(new Vector3(0,0,-30).sub(nm));
		lightView.up.set(0,-1,0);   
		lightView.fieldOfView = 100;
		lightView.update();	
	}
	public void render(){
    	
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.3f,0.3f,0.4f,1.0f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        
        camera.update();
        for(int i = 0;i<puzzlePieces.size; i++){
        	puzzlePieces.get(i).update();
        }
        
        renderLightDepthMap();
        
        //VCommon.drawGrid(camera.get());

        tableRenderable.render(camera.get(), environment);        
        backgroundRenderable.render(camera.get(), environment);  
        for(int i = 0;i<puzzlePieces.size; i++){
        	puzzlePieces.get(i).render(camera.get(), environment);
        }
        
		mainStage.render();
		
		if(checkPuzzleComplete() && gameState == GameStates.PUZZLE){
			gameState = GameStates.FINISHED;
			mainStage.showInfoWindow();
			camera.setCameraState(PresetsIdentifiers.IMAGE_COMPLETE_VIEW);
		}
	}	
	public void renderLightDepthMap(){
        
        lightDepthTexture.beginRender();
        
    	Gdx.gl.glClearColor(1,1,0, 0.0f);
    	Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);

        tableRenderable.renderDepth(lightView, environment);        
        backgroundRenderable.renderDepth(lightView, environment);     
        for(int i = 0;i<puzzlePieces.size; i++){
        	puzzlePieces.get(i).renderDepth(lightView, environment);
        }
        
        lightDepthTexture.endRender();
	}	
	public void generateNewPuzzle(int pieces){
		
		puzzlePieces.clear();
		
		float d = (1.0f / (float)Math.sqrt((float)pieces)) * 0.5f;
		mainStage.shapeGen.generate(pieces,  d);
		
//		Random rnd = new Random();
		int r = (currentImage+1);
		
		currentImage = (currentImage+1)%7;
		
        Texture texture = new Texture(Gdx.files.internal("img"+r+".png"), true);
        
        texture.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Linear);
        
        mainStage.puzzleCurrentImageIndex = r;
        
        float maxWidth=100, maxHeight=100;
        float imageScale = Math.min(maxWidth / texture.getWidth(), maxHeight / texture.getHeight());
        Vector2 size = new Vector2(texture.getWidth() * imageScale, texture.getHeight() * imageScale);
        
        backgroundRenderable.setImageBackgroundSize(size.x, size.y);
        
        backgroundRenderable.setDiffuseTexture(null, texture);
        
        tableRenderable.setDiffuseTexture(null, texture);
        
        meshBuilder.generateDistributionPoints(mainStage.shapeGen.pieceShapes.size, size, new Vector2(230,130));
        
        for(int i=0;i<mainStage.shapeGen.pieceShapes.size; i++){

        	VPuzzlePieceRenderable renderable = new VPuzzlePieceRenderable(this, mainStage.shapeGen.pieceShapes.get(i), size);
        			
        	renderable.setDiffuseTexture(null, texture);	
        	puzzlePieces.add(renderable);
        	
    		Vector2 prr = meshBuilder.randomDistributedPoints.get(i);   		
    		renderable.startPosition.set(prr.x, ((float)Math.random() * 4.0f), prr.y);    		
    		renderable.translate(renderable.originalPosition);    		
    		renderable.setTransferToInitialPosition = true;
        }
        gameState = GameStates.PUZZLE;
        camera.setCameraState(PresetsIdentifiers.PUZZLE_VIEW);
	}
	
	public VPuzzlePieceRenderable getPieceAtPoint(int x, int y, Vector3 p){
    	Ray r = camera.get().getPickRay(x, y);
    	Vector3 intersectionPoint = new Vector3();
    	Vector3 inter = new Vector3();
    	float dst = 1000000;
    	VPuzzlePieceRenderable found = null;
    	for(int i = 0;i<puzzlePieces.size; i++){
        	VPuzzlePieceRenderable pp = puzzlePieces.get(i);
    		if(pp.IntersectRay(r, inter)){
    			float d = camera.get().position.cpy().sub(inter).len2();
    			if(!pp.isFinished && !pp.isGrabbed && d < dst){
    				dst = d;
    				intersectionPoint.set(inter);
    				found = pp;
    				p.set(inter);
    			}
    		}
    	}    	
		return found;
	}
	public void onTouchDown(int x, int y){
		if(gameState == GameStates.PUZZLE){
			VPuzzlePieceRenderable rnd = getPieceAtPoint(x, y, dragIntersection);
			if(rnd != null){
				dragPiece = rnd;
				dragOffset = dragIntersection.cpy().sub(dragPiece.getTranslation());			
			}
		}
	}
	public void onTouchUp(int x, int y){
		dragPiece = null;
	}	
	public void onTap(float x, float y, int count, int button){
		

	}
    public void onDrag(int x, int y){
    	if(gameState == GameStates.PUZZLE){
	    	if(dragPiece != null){
	    		if(dragPiece.isGrabbed){
	    			dragPiece = null;
	    			return;
	    		}
	        	Ray r = camera.get().getPickRay(x, y);        
	        	Intersector.intersectRayPlane(r, dragPiece.surfacePlane, dragIntersection);
	    		Vector3 t = dragPiece.getTranslation();    		
	    		Vector3 tnew = dragIntersection.sub(dragOffset);
	    		tnew.y = t.y;
	    		dragPiece.translate(tnew);
	    	}
    	}
    }	
    
    public boolean checkPuzzleComplete(){
    	for(int i = 0;i<puzzlePieces.size; i++){
    		if(!puzzlePieces.get(i).isFinished)return false;
    	}
    	if(puzzlePieces.size == 0)return false;
    	return true;
    }
    
}
