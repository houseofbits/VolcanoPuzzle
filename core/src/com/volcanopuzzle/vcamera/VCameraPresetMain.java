package com.volcanopuzzle.vcamera;

import com.badlogic.gdx.math.Vector2;

public class VCameraPresetMain extends VCameraPreset {
	public VCameraPresetMain(VCameraPresetCollection.PresetsIdentifiers identifier){
		super(identifier);
        
		switch(identifier){
		case PUZZLE_VIEW:
			anglePos.y = 85;
			break;
		case IMAGE_COMPLETE_VIEW:
			distance = 60;
			anglePos.y = 89.9f;
			break;			
		default:
			break;			
		}
	}	
}
