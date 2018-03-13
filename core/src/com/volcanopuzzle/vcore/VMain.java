package com.volcanopuzzle.vcore;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.volcanopuzzle.vstage.VStageMain;

public class VMain {
	
	public PerspectiveCamera camera = null;
	public Environment environment = new Environment();
	public VInputProcessor inputProcessor;
	public VStageMain mainStage = new VStageMain(this);	
	public VPieceMeshBuilder meshBuilder = new VPieceMeshBuilder();
	
	Array<VPuzzlePieceRenderable> puzzlePieces = new Array<VPuzzlePieceRenderable>();
	
	public void create(){
		VStaticAssets.Init();
		inputProcessor = new VInputProcessor(this);
		
		mainStage.create();
		
		camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(new Vector3(100,180,150));
		camera.up.set(0,1,0);   		
		camera.near = 1f;
		camera.far = 3000;
		camera.fieldOfView = 90;
		Quaternion q = new Quaternion();
		q.setEulerAngles(180, 80, 0.0f);
		
		Vector3 nm = new Vector3(0,0,1);
		nm = q.transform(nm).nor();
		camera.direction.set(nm);
		
		camera.update();
		
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.5f,  -1, -0.8f, 1));		
        
        generateNewPuzzle(15);
        
        CameraInputController camController = new CameraInputController(camera);        
//        Gdx.input.setInputProcessor(new InputMultiplexer(mainStage.mainStage, camController, inputProcessor));
        Gdx.input.setInputProcessor(new InputMultiplexer(mainStage.mainStage, inputProcessor.gestureDetector, inputProcessor));        
        
        
	}
	public void render(){
    	
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1.0f);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        
        VCommon.drawGrid(camera);
        
        //puzzlePiece.render(camera, environment);
        for(int i = 0;i<puzzlePieces.size; i++){
        	puzzlePieces.get(i).render(camera, environment);
        }
        
        
		mainStage.render();
	}
	public void generateNewPuzzle(int pieces){
		
		puzzlePieces.clear();
		
		float d = (1.0f / (float)Math.sqrt((float)pieces)) * 0.5f;

		mainStage.shapeGen.generate(pieces,  d);
		
		Random rnd = new Random();
		int r = rnd.nextInt(7) + 1;
		
        Texture texture = new Texture(Gdx.files.internal("img"+r+".png"));
        
        for(int i=0;i<mainStage.shapeGen.pieceShapes.size; i++){
        	VPuzzlePieceRenderable renderable = meshBuilder.build(mainStage.shapeGen.pieceShapes.get(i), new Vector2(200,200));
        	renderable.setDiffuseTexture(null, texture);	
        	puzzlePieces.add(renderable);
        }
	}
	
	public boolean intersectDraggedPieceAtPoint(int x, int y){		
    	Ray r = camera.getPickRay(x, y);
    	if(dragPiece != null && dragPiece.IntersectRay(r, dragIntersection)){
    		return true;
    	}
		return false;
	}
	
	public VPuzzlePieceRenderable getPieceAtPoint(int x, int y, Vector3 p){
    	Ray r = camera.getPickRay(x, y);
    	Vector3 intersectionPoint = new Vector3();
    	Vector3 inter = new Vector3();
    	float dst = 1000000;
    	VPuzzlePieceRenderable found = null;
    	for(int i = 0;i<puzzlePieces.size; i++){
    		if(puzzlePieces.get(i).IntersectRay(r, inter)){
    			float d = camera.position.cpy().sub(inter).len2();
    			if(d < dst){
    				dst = d;
    				intersectionPoint.set(inter);
    				found = puzzlePieces.get(i);
    				p.set(inter);
    			}
    		}
    	}    	
		return found;
	}
	
	private VPuzzlePieceRenderable dragPiece = null;
	private Vector3 dragOffset = new Vector3();
	private Vector3 dragIntersection = new Vector3();
	
	public void onTouchDown(int x, int y){
		VPuzzlePieceRenderable rnd = getPieceAtPoint(x, y, dragIntersection);
		if(rnd != null){
			dragPiece = rnd;
			dragOffset = dragIntersection.cpy().sub(dragPiece.getTranslation());			
			//System.out.println("Grab "+dragIntersection+" offs "+dragOffset);
		}
	}
	public void onTouchUp(int x, int y){
		dragPiece = null;
	}	
	public void onTap(float x, float y, int count, int button){
		

	}
    public void onDrag(int x, int y){
    	if(dragPiece != null){
        	Ray r = camera.getPickRay(x, y);        
        	Intersector.intersectRayPlane(r, dragPiece.surfacePlane, dragIntersection);
    		Vector3 t = dragPiece.getTranslation();    		
    		Vector3 tnew = dragIntersection.sub(dragOffset);
    		tnew.y = t.y;
    		dragPiece.translate(tnew);
    	}
    }	
}
