package com.volcanopuzzle.vcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.volcanopuzzle.vcore.VMain.GameStates;

public class VDemoPlay {
	
	public VMain	volcano;
	
	boolean active = true;
	
	public VDemoPlay(VMain v){
		volcano = v;
	}
	
	void update(){
		if(!active)return;
		
		if (volcano.gameState == GameStates.PUZZLE) {
			
			if (volcano.dragPiece != null) {
				if (volcano.dragPiece.isGrabbed) {
					volcano.dragPiece = null;
					return;
				}
				
				//Translate piece towards target position
				
				Vector3 targetPos = volcano.dragPiece.originalPosition.cpy();
				Vector3 currentPos = volcano.dragPiece.getTranslation();
				
				Vector3 direction = targetPos.sub(currentPos).nor();
				
				float dt = Gdx.graphics.getDeltaTime();
				
				direction.scl(dt * 130);
				
				volcano.dragPiece.translate(currentPos.add(direction));
				
			}else{
				
				//Start dragging new piece
				for (int i = 0; i < volcano.puzzlePieces.size; i++) {
					VPuzzlePieceRenderable pp = volcano.puzzlePieces.get(i);
					if(!pp.isGrabbed && !pp.isFinished){
						volcano.dragPiece = pp;
						volcano.dragPiece.onDragStart();
						break;
					}
				}
			}
		}else{
			
			//If puzzle is complete, generate new puzzle
			
			if(volcano.gameState != GameStates.PUZZLE && !volcano.mainStage.isLocked())
				volcano.generateNewPuzzle(100, -1);
			
		}
	}
	
}
