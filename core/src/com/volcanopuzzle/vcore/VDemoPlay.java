package com.volcanopuzzle.vcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.volcanopuzzle.vcore.VMain.GameStates;

public class VDemoPlay {
	
	public VMain	volcano;
	
	boolean active = false;
	
	private Timer.Task nextPuzzleDelayTimer = null;
	
	public VDemoPlay(VMain v){
		volcano = v;
	}
	
	public void stopDemo(){
		if(nextPuzzleDelayTimer != null)nextPuzzleDelayTimer.cancel();
		nextPuzzleDelayTimer = null;
		active = false;
	}
	
	public void startDemo(){
		active = true;
		volcano.mainStage.setSelectorTableVisibility(false);
	}
	
	public void update(){
		if(!active)return;

		if (volcano.gameState == GameStates.PUZZLE && !volcano.mainStage.isLocked()) {
			
			if (volcano.dragPiece != null) {
				if (volcano.dragPiece.isGrabbed) {
					volcano.dragPiece = null;
					return;
				}
				
				//Translate piece towards target position
				
				Vector3 targetPos = volcano.dragPiece.originalPosition.cpy();
				Vector3 currentPos = volcano.dragPiece.getTranslation();
				
				Vector3 direction = targetPos.sub(currentPos);
				
				float dist = direction.len();
				direction.nor();
				
				float dt = Gdx.graphics.getDeltaTime();
				
				float timestep = Math.min(dist, dt * 230.0f);
				
				direction.scl(timestep);
				
				volcano.dragPiece.translate(currentPos.add(direction));
				
			}else{				
				//Start dragging new piece
				for (int i = 0; i < volcano.puzzlePieces.size; i++) {
					VPuzzlePieceRenderable pp = volcano.puzzlePieces.get(i);
					if(!pp.isGrabbed && !pp.isFinished && !pp.setTransferToInitialPosition){
						volcano.dragPiece = pp;
						volcano.dragPiece.onDragStart();
						break;
					}
				}
			}
		}else{			
			//If puzzle is complete, generate new puzzle			
			if(volcano.gameState != GameStates.PUZZLE && !volcano.mainStage.isLocked()){				
				if(nextPuzzleDelayTimer == null){
					nextPuzzleDelayTimer = Timer.schedule(new Timer.Task() {
				        @Override
				        public void run(){
				        	nextPuzzle();
				        }}, 4.0f);
				}
			}
		}
	}
	public void nextPuzzle(){
		volcano.generateNewPuzzle(20, -1);
		volcano.mainStage.setNextGroupVisibility(false);
		volcano.mainStage.puzzleComplete = false;
		
		if(nextPuzzleDelayTimer != null)nextPuzzleDelayTimer.cancel();
		nextPuzzleDelayTimer = null;
	}
	
}
