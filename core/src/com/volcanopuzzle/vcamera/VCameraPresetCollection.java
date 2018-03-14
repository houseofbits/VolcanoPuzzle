package com.volcanopuzzle.vcamera;

import java.util.EnumMap;

public class VCameraPresetCollection {
	
	public enum PresetsIdentifiers{
		//VCameraPresetMain
		PUZZLE_VIEW,
		IMAGE_COMPLETE_VIEW,
	}
	
	public VCameraPreset 		finalPreset = null;
	public EnumMap<PresetsIdentifiers, VCameraPreset> cameraPresets = new EnumMap<PresetsIdentifiers, VCameraPreset>(PresetsIdentifiers.class);
	
	public VCameraPresetCollection(VCameraPreset cam){
		finalPreset = cam;
		
		addPreset(new VCameraPresetMain(PresetsIdentifiers.PUZZLE_VIEW));
		addPreset(new VCameraPresetMain(PresetsIdentifiers.IMAGE_COMPLETE_VIEW));
		
		transitionToPreset(PresetsIdentifiers.PUZZLE_VIEW);
	}
	
	public void addPreset(VCameraPreset preset){
		cameraPresets.put(preset.getPreset(), preset);
	}
	public VCameraPreset getPreset(PresetsIdentifiers identifier){
		return cameraPresets.get(identifier);
	}	
	
	public void transitionToPreset(PresetsIdentifiers targetPresetIdentifier){
		VCameraPreset target = getPreset(targetPresetIdentifier);
		if(target != null){			
			finalPreset.setTransitionFromPreset(target, targetPresetIdentifier);			
		}
	}
	
}
