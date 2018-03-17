package com.volcanopuzzle.vcamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.volcanopuzzle.vcore.VCommon;

public class VCameraPreset {
	
	//Interface for camera callback
	public interface VCameraPresetCallback{
		
		public void onPresetTransitionComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier);
		public void onTransitionAngleXComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier);
		public void onTransitionAngleYComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier);
		public void onTransitionFovComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier);
		public void onTransitionDistanceComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier);
		public void onTransitionPivotComplete(VCameraPresetCollection.PresetsIdentifiers sourceIdentifier, VCameraPresetCollection.PresetsIdentifiers targetIdentifier);		
	}
	//Internal variables
	private Vector2 _momentum = new Vector2(0,0);
	private Vector2 _velocity = new Vector2(0,0);
	
	//Preset transition variables
	private VCameraPresetCallback callback = null;
	private VCameraPresetCollection.PresetsIdentifiers identifier = null;
	private VCameraPresetCollection.PresetsIdentifiers targetIdentifierTransition = null;
	
	//Public camera settings
	public Vector3 	pivotPosition = new Vector3(0,0,0);
	public float	fov = 80.0f;
	public float 	distance = 100.0f;	
	public Vector2 	anglePos = new Vector2(180, 90);
	public Vector2 	friction = new Vector2(0.3f, 0.3f);
	public Vector2 	velocityMax = new Vector2(100,100);
	public float	fovChangeMax = 20;
	public float	distanceChangeMax = 200;
	public float	pivotVelocityMax = 100;
	public float 	targetAngleX = 0;						//[0 : 360]
	public boolean 	transitionAngleXEnabled = false;
	public float 	targetAngleY = 0;						//[-85 : 85]
	public boolean 	transitionAngleYEnabled = false;
	public float	targetFov = 0;
	public boolean 	transitionFovEnabled = false;
	public float	targetDistance = 0;
	public boolean 	transitionDistanceEnabled = false;
	public Vector3 	targetPivot = new Vector3();
	public boolean	transitionPivotEnabled = false;
	public boolean 	gravityEnabled = false;
	public boolean 	wayPointsEnabled = false;	
	public boolean 	cameraPanEnabled = false;
	
//	public boolean 	cameraPanAngleXLimitEnabled = false;			
//	public boolean 	cameraPanAngleYLimitEnabled = false;				
//	public Vector2	cameraPanAngleXLimit = new Vector2();
//	public Vector2	cameraPanAngleYLimit = new Vector2();	
	
	//Target goal 	
	public float	transitionAngleXGoal = 8.0f;	//4.0f;
	public float	transitionAngleYGoal = 1.0f;	//4.0f
	public float 	transitionFovGoal = 8.0f;		//2.0f
	public float 	transitionDistanceGoal = 1.0f;	//2.0f
	public float 	transitionPivotGoal = 8.0f;		//2.0f
	
	public class WayPoint{
		public WayPoint(float a, float my){
			anglePos = a;
			minY = my;
		}
		public WayPoint(float a, float my, float maxy){
			anglePos = a;
			minY = my;
			maxY = maxy;
		}
		public WayPoint(WayPoint w){
			anglePos = w.anglePos;
			minY = w.minY;
			maxY = w.maxY;
		}		
		public float anglePos = 0; //[0 : 360]
		public float minY = 5; //[-85 : 85]
		public float maxY = 85; //[-85 : 85]		
	};
	public Array<WayPoint> wayPoints = new Array<WayPoint>();	
	
	public VCameraPreset(VCameraPresetCollection.PresetsIdentifiers idt){
		identifier = idt;
	}
	public void setCallback(VCameraPresetCallback call){
		callback = call;
	}
	public void addWayPoint(WayPoint wp){
		wayPoints.add(wp);		
	}
	public VCameraPresetCollection.PresetsIdentifiers getPreset(){
		return identifier;
	}	
	public VCameraPresetCollection.PresetsIdentifiers getCurrentPreset(){
		if(targetIdentifierTransition != null)return targetIdentifierTransition;
		return identifier;
	}	
	public VCameraPresetCollection.PresetsIdentifiers getTargetPreset(){
		return targetIdentifierTransition;
	}		
	public void update(PerspectiveCamera cam){

		updateMotion();

		Quaternion q = new Quaternion();
		q.setEulerAngles(anglePos.x, anglePos.y, 0.0f);
		
		Vector3 nm = new Vector3(0,0,1);
		nm = q.transform(nm).nor();	
		cam.direction.set(nm);		
		nm.scl(distance);
		
		cam.position.set((pivotPosition.cpy().sub(nm)));
        cam.up.set(0,1,0);   
        cam.fieldOfView = fov;
        cam.update();               
	}	
	private void updateMotion(){
		float dt = Gdx.graphics.getDeltaTime();
		boolean transitionInProgress = false;
		
		_velocity.add(_momentum);
		_momentum.set(0,0);
		
		anglePos.add(_velocity.cpy().scl(dt));
		
		_velocity.x = _velocity.x * (float)Math.pow(friction.x, dt);
		_velocity.y = _velocity.y * (float)Math.pow(friction.y, dt);
		
		if(transitionAngleXEnabled){			
			float diffX = targetAngleX - anglePos.x;
	        while (diffX < -180) diffX += 360;
	        while (diffX > 180) diffX -= 360;
	        _velocity.x = diffX;
	        if(Math.abs(diffX) < transitionAngleXGoal){
	        	transitionAngleXEnabled = false;
	        	if(callback != null)callback.onTransitionAngleXComplete(identifier, targetIdentifierTransition);
	        }else transitionInProgress = true;
		}
		if(transitionAngleYEnabled){
			float diffY = targetAngleY - anglePos.y;
	        while (diffY < -180) diffY += 360;
	        while (diffY > 180) diffY -= 360;
	        _velocity.y = diffY;
	        if(Math.abs(diffY) < transitionAngleYGoal){
	        	transitionAngleYEnabled = false;
	        	if(callback != null)callback.onTransitionAngleYComplete(identifier, targetIdentifierTransition);
	        }else transitionInProgress = true;
		}
		if(transitionFovEnabled){
			float diffF = targetFov - fov;			
			fov += Math.max(-fovChangeMax, Math.min(diffF, fovChangeMax)) * dt;
			if(Math.abs(diffF) < transitionFovGoal){
				transitionFovEnabled = false;
				if(callback != null)callback.onTransitionFovComplete(identifier, targetIdentifierTransition);
			}else transitionInProgress = true;
		}
		if(transitionDistanceEnabled){
			float diffF = targetDistance - distance;			
			distance += Math.max(-distanceChangeMax, Math.min(diffF, distanceChangeMax)) * dt;
			if(Math.abs(diffF) < transitionDistanceGoal){
				transitionDistanceEnabled = false;
				if(callback != null)callback.onTransitionDistanceComplete(identifier, targetIdentifierTransition);
			}else transitionInProgress = true;
		}	
		if(transitionPivotEnabled){
			Vector3 diff = targetPivot.cpy().sub(pivotPosition);
			if(diff.len2() < transitionPivotGoal){
				transitionPivotEnabled = false;	
				if(callback != null)callback.onTransitionPivotComplete(identifier, targetIdentifierTransition);
			}else transitionInProgress = true;
			diff.clamp(-pivotVelocityMax, pivotVelocityMax);
			pivotPosition.add(diff.scl(dt));
		}
		WayPoint wp = getInterpolatedWayPoint();
		//Limit camera movement Y axis
		if(wayPointsEnabled){
			if(wp.minY > anglePos.y)_velocity.y = -(anglePos.y - wp.minY) * 10.0f;
			if(wp.maxY < anglePos.y){
				_velocity.y = -(anglePos.y - wp.maxY) * 10.0f;
			}
		}
		
		//Add small gravity to camera
		if(gravityEnabled){
			float offsetY = 3.0f;
			if(anglePos.y > (wp.minY + offsetY)){
				_velocity.y += ((wp.minY + offsetY) - anglePos.y) * 0.3f * dt;
			}
			_velocity.x += 2 * dt;
		}
		//Clamp to maximum velocity
		_velocity.x = Math.min(Math.max(_velocity.x, -velocityMax.x), velocityMax.x);
		_velocity.y = Math.min(Math.max(_velocity.y, -velocityMax.y), velocityMax.y);		
		
		if(anglePos.x > 360 || anglePos.x < -360)anglePos.x = 0;
		if(anglePos.y > 360 || anglePos.y < -360)anglePos.y = 0;		
		
		if(anglePos.y > 90)anglePos.y = 90;
		if(anglePos.y < -90)anglePos.y = -90;
		
		//Check for preset transition completion		
		if(targetIdentifierTransition!= null && !transitionInProgress){
			VCameraPresetCollection.PresetsIdentifiers id1 = identifier;
			VCameraPresetCollection.PresetsIdentifiers id2 = targetIdentifierTransition;			
			
			identifier = targetIdentifierTransition;
			targetIdentifierTransition = null;

			if(callback != null)callback.onPresetTransitionComplete(id1, id2);
//			System.out.println("trans complete: "+identifier+" -> "+targetIdentifierTransition);
		}
		//System.out.println(anglePos);
	}	
	public void addMomentum(Vector2 v){
		Vector2 mv = v.scl(Gdx.graphics.getDeltaTime());
		mv.scl(new Vector2(10,5));
		_momentum.add(mv);
	}
	public void setTransitionAngleX(float angleX){
		angleX = Math.min(Math.max(angleX, 0), 360);
		transitionAngleXEnabled = true;
		targetAngleX = angleX;
	}
	public void setTransitionAngleY(float angleY){
		angleY = Math.min(Math.max(angleY, -89.9f), 89.9f);
		transitionAngleYEnabled = true;
		targetAngleY = angleY;
	}
	public void setTransitionFov(float fov){
		targetFov = fov;
		transitionFovEnabled = true;
	}
	public void setTransitionDistance(float dist){
		targetDistance = dist;
		transitionDistanceEnabled = true;
	}	
	public void setTransitionPivot(Vector3 p){
		targetPivot.set(p);
		transitionPivotEnabled = true;
	}
	private WayPoint getInterpolatedWayPoint(){
		
		float absAngleX = anglePos.x;
		if(absAngleX < 0)absAngleX = 360.0f - (float)Math.abs(absAngleX);
		
		int inext = 0;
		for(int i=0; i<wayPoints.size; i++){
			inext = (inext + 1) % wayPoints.size;
			WayPoint a = wayPoints.get(i);
			WayPoint b = wayPoints.get(inext);			
			
			float f = 0;
			boolean found = false;
			if(absAngleX >= a.anglePos && absAngleX < b.anglePos){
				f = (absAngleX - a.anglePos) / (b.anglePos - a.anglePos);
				found = true;
			}else if(a.anglePos > b.anglePos && absAngleX >= a.anglePos){
				
//				special case where, for example a=355, b=5
				
//				float fm = 0;
//				float fp = 0;
//				if(a.anglePos < 360)fm = 360 - a.anglePos;
//				if(b.anglePos > 0)fm += b.anglePos;
//				
//				f = (b.anglePos - absAngleX) / fm;				
				
			}else if(absAngleX < a.anglePos){
				f = absAngleX / a.anglePos;
				found = true;
			}
			if(found){				
				//System.out.println(f+" ["+a.anglePos+" - "+b.anglePos+"] "+absAngleX);
				//System.out.println(f+" ["+a.minY+" - "+b.minY+"]");				
				return new WayPoint(absAngleX, VCommon.lerp(a.minY, b.minY, f), VCommon.lerp(a.maxY, b.maxY, f));
			}
		}
		
		return new WayPoint(0,0);
	}
	
	public void setTransitionFromPreset(VCameraPreset target, VCameraPresetCollection.PresetsIdentifiers targetIdentifier){
		
		//System.out.println(this.identifier+" targ:"+target.identifier);
		
		wayPoints.clear();
		if(target.wayPoints.size > 0){
			//System.out.println("this wp "+wayPoints);
			//wayPoints = new Array<WayPoint>(target.wayPoints);
			
			for(int i=0; i<target.wayPoints.size; i++){
				wayPoints.add(new WayPoint(target.wayPoints.get(i)));
			}
			//System.out.println("this wp "+wayPoints);
//			System.out.println(this+" this: "+this.wayPoints.get(0).maxY);
//			System.out.println("target: "+target.wayPoints.get(0).maxY);
		}
		
		//if(targetIdentifier != VCameraPresetCollection.PresetsIdentifiers.MAIN)
		this.setTransitionAngleX(target.anglePos.x);
		this.setTransitionAngleY(target.anglePos.y);
		this.setTransitionDistance(target.distance);
		this.setTransitionFov(target.fov);
		this.setTransitionPivot(target.pivotPosition);

		friction.set(target.friction);
		velocityMax.set(target.velocityMax);
		fovChangeMax = target.fovChangeMax;
		distanceChangeMax = target.distanceChangeMax;		
		pivotVelocityMax = target.pivotVelocityMax;

		//set after transition is finished
		gravityEnabled = target.gravityEnabled;
		wayPointsEnabled = target.wayPointsEnabled;
		
		cameraPanEnabled = target.cameraPanEnabled;
		
//		cameraPanAngleXLimitEnabled = target.cameraPanAngleXLimitEnabled;			
//		cameraPanAngleYLimitEnabled = target.cameraPanAngleYLimitEnabled;			
//		cameraPanAngleXLimit.set(target.cameraPanAngleXLimit);
//		cameraPanAngleYLimit.set(target.cameraPanAngleYLimit);			
		
		targetIdentifierTransition = targetIdentifier;
		
	//	System.out.println("trans to "+targetIdentifier+", "+identifier);

	}
}
