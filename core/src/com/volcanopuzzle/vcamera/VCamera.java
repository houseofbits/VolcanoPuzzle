package com.volcanopuzzle.vcamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.volcanopuzzle.vcamera.VCameraPresetCollection.PresetsIdentifiers;
import com.volcanopuzzle.vcore.VMain;

public class VCamera extends VCameraPreset implements VCameraPreset.VCameraPresetCallback {
	
	private VMain volcano = null;
	
	public PerspectiveCamera cam;

	private VCameraPresetCollection cameraPresetsCollection = new VCameraPresetCollection(this);
	
	public VCamera(VMain o){
		super(null);
		setSceneManager(o);
		setCallback(this);
		cam = new PerspectiveCamera(35, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 1f;
        cam.far = 200;
        cam.update();
	}

	public void setCameraState(VCameraPresetCollection.PresetsIdentifiers state){
		switch(state){
		case PUZZLE_VIEW:
			this.cameraPresetsCollection.transitionToPreset(PresetsIdentifiers.PUZZLE_VIEW);
			break;
		case IMAGE_COMPLETE_VIEW:
			this.cameraPresetsCollection.transitionToPreset(PresetsIdentifiers.IMAGE_COMPLETE_VIEW);
			break;												
		default:
			break;			
		};
	}
	public void update(){
		update(cam);        
	}		
	public PerspectiveCamera get(){
		return cam;
	}
	public void pan(Vector2 mouseDrag){
		mouseDrag.x = -mouseDrag.x;
		//if(cameraState == States.MAIN)addMomentum(mouseDrag);
		if(cameraPanEnabled)addMomentum(mouseDrag);
	}
	//tmp: for testing
	public void onKeyDown(int key){
		//System.out.println(key);
		//spacebar : 62
		//'a' : 29
		if(key == 62){
			System.out.println(anglePos);
		}
		if(key == 29){	//'a'
//			setCameraMode(1);
//			this.cameraPresetsCollection.transitionToPreset(PresetsIdentifiers.MAIN_OVER_STATIC_VIEW_1);			
//			float pos = (float)Math.random() * 360.0f;
//			setTransitionFov(90);
//			setTransitionDistance(300);
			//setTransitionAngleX(pos);
			//setTransitionPivot(new Vector3((float)Math.random() * 360.0f,100,(float)Math.random() * 360.0f));			
			//System.out.println(pos);
		}
	}
	public void onPresetTransitionComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, 
			VCameraPresetCollection.PresetsIdentifiers targetIdentifier){

	}
	public void onTransitionAngleXComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier){
	//	System.out.println("complete(AngleX): "+sourceIdentifier+" -> "+targetIdentifier);
	}
	public void onTransitionAngleYComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier){
	//	System.out.println("complete(AngleY): "+sourceIdentifier+" -> "+targetIdentifier);		
	}
	public void onTransitionFovComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier){
//		System.out.println("complete(Fov): "+sourceIdentifier+" -> "+targetIdentifier);	
	}
	public void onTransitionDistanceComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier){
//		System.out.println("complete(Distance): "+sourceIdentifier+" -> "+targetIdentifier);	
	}
	public void onTransitionPivotComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier){
//		System.out.println("complete(Pivot): "+sourceIdentifier+" -> "+targetIdentifier);	
	}

	public VMain getSceneManager() {
		return volcano;
	}

	public void setSceneManager(VMain volcano) {
		this.volcano = volcano;
	}			
}
