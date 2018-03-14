package com.volcanopuzzle.vcamera;

import com.badlogic.gdx.math.Vector2;

public class VCameraPresetMain extends VCameraPreset {
	public VCameraPresetMain(VCameraPresetCollection.PresetsIdentifiers identifier){
		super(identifier);
        
		switch(identifier){
		case PUZZLE_VIEW:

			break;
		case IMAGE_COMPLETE_VIEW:
			distance = 50;
			anglePos.y = 90;
			break;			
		default:
			break;			
		}
	}	
}
