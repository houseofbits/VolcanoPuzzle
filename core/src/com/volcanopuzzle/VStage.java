package com.volcanopuzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class VStage extends InputListener {
	
	class PuzzlePiece extends Image{

		public int positionIndexX = 0;
		public int positionIndexY = 0;
		
		public int destinationPosIndexX = 0;
		public int destinationPosIndexY = 0;		
		
//		public float destinationPosX = 0;
//		public float destinationPosY = 0;
//		public float startPosX = 0;
//		public float startPosY = 0;
		
		public PuzzlePiece(TextureRegion tex){
			super(tex);
		}
		
	}
	
	public Stage mainStage = null;
	
	public Group	puzzlePiecesGroup = null;
	
	public VStage(){
		

	}
	
	public void create(){
		
		mainStage = new Stage();
		
		puzzlePiecesGroup = new Group();
		
		Texture texture = new Texture(Gdx.files.internal("img1.png"));		
		
		createPuzzleImage(3,3, texture);
		
//		puzzlePiecesGroup
		
		mainStage.addActor(puzzlePiecesGroup);
		
//		mainStage.setDebugAll(true);
		mainStage.setDebugUnderMouse(true);
	}
	
	public void createPuzzleImage(int widthPieces, int heightPieces, Texture texture){
		
		float maxWidth = mainStage.getWidth() * 0.9f;
		float maxHeight = mainStage.getHeight() * 0.9f;
		
		float texScale = Math.min(maxWidth / texture.getWidth(), maxHeight / texture.getHeight());
		
		float imageStageWidth = texture.getWidth() * texScale;
		float imageStageHeight = texture.getHeight() * texScale;		
		
		puzzlePiecesGroup.setSize(imageStageWidth, imageStageHeight);
		
		float offsetX = (mainStage.getWidth() - imageStageWidth) / 2;
		float offsetY = (mainStage.getHeight() - imageStageHeight) / 2;		
		
		puzzlePiecesGroup.setPosition(offsetX, offsetY);
		
		float pieceTextureWidth = texture.getWidth() / (float)widthPieces;
		float pieceTextureHeight = texture.getHeight() / (float)heightPieces;
		
		float pieceStageWidth = imageStageWidth / (float)widthPieces;
		float pieceStageHeight = imageStageHeight / (float)heightPieces;		
		
		for(int ix = 0; ix < widthPieces; ix++){
			float x1 = (float)ix * pieceStageWidth;
			float x2 = (float)ix * pieceTextureWidth;
			for(int iy = 0; iy < heightPieces; iy++){
				float y1 = (float)iy * pieceStageHeight;
				float y2 = (float)iy * pieceTextureHeight;
				
				TextureRegion reg1 = new TextureRegion(texture, (int)x2,(int)y2, (int)pieceTextureWidth, (int)pieceTextureHeight);
				
				PuzzlePiece piece = new PuzzlePiece(reg1);
				//piece.destinationPosX = x1 + offsetX;
				//piece.destinationPosY = imageStageHeight - pieceStageHeight - y1 + offsetY;				
				//piece.setPosition(piece.destinationPosX, piece.destinationPosY);
				//getActorPosition();
				piece.setSize(pieceStageWidth, pieceStageHeight);
				
				puzzlePiecesGroup.addActor(piece);	
				
				if(ix == (widthPieces - 1) && iy == (heightPieces - 1)){
					piece.setColor(1,1,1, 0.3f);
				}
			}
		}		
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
	}
	
}
