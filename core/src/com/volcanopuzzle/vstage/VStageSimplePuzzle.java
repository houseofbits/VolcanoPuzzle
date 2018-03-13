package com.volcanopuzzle.vstage;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class VStageSimplePuzzle extends InputListener {
	
	class PuzzlePiece extends Image{
		public int positionIndexX = 0;
		public int positionIndexY = 0;		
		public int destinationPosIndexX = 0;
		public int destinationPosIndexY = 0;		
		
		public PuzzlePiece(TextureRegion tex){
			super(tex);
		}		
	}
	
	public Stage mainStage = null;
	
	public Group	puzzlePiecesGroup = null;
	public Image	puzzlePiecesBackground = null;	
	public Vector2	puzzleImageSize = new Vector2();	
	public Vector2	puzzlePieceSize = new Vector2();
	public int		puzzlePiecesX = 0;
	public int		puzzlePiecesY = 0;	
	public int		puzzleEmptyX = 0;
	public int		puzzleEmptyY = 0;	
	public boolean	puzzleComplete = false; 
	
	public VStageSimplePuzzle(){
		

	}
	
	public void create(){
		
		mainStage = new Stage();
		
        Gdx.input.setInputProcessor(mainStage);		
		
		puzzlePiecesGroup = new Group();
		puzzlePiecesBackground = new Image();
		
		puzzlePiecesBackground.setColor(1, 1, 1, 0.2f);
		
		mainStage.addActor(puzzlePiecesBackground);
		mainStage.addActor(puzzlePiecesGroup);

		continueNext();
		
		mainStage.setDebugAll(true);
//		mainStage.setDebugUnderMouse(true);
	}
	
	public void continueNext(){
		Random rnd = new Random();
		int r = rnd.nextInt(7) + 1;
		int xp = rnd.nextInt(2)+2;
		int yp = rnd.nextInt(2)+2;
		if(xp == yp && xp == 2){
			yp = 3;
		}
		Texture texture = new Texture(Gdx.files.internal("img"+r+".png"));	
		createPuzzleImage(xp,yp, texture);
	}
	
	public void createPuzzleImage(int widthPieces, int heightPieces, Texture texture){
		
		puzzlePiecesGroup.clearChildren();
		
		puzzlePiecesX = widthPieces;
		puzzlePiecesY = heightPieces;
		
		puzzlePiecesBackground.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
		
		float maxWidth = mainStage.getWidth() * 0.9f;
		float maxHeight = mainStage.getHeight() * 0.9f;
		
		float texScale = Math.min(maxWidth / texture.getWidth(), maxHeight / texture.getHeight());
		
		puzzleImageSize.set(texture.getWidth() * texScale, texture.getHeight() * texScale);		
		
		puzzlePiecesGroup.setSize(puzzleImageSize.x, puzzleImageSize.y);
		
		float offsetX = (mainStage.getWidth() - puzzleImageSize.x) / 2;
		float offsetY = (mainStage.getHeight() - puzzleImageSize.y) / 2;		
		
		puzzlePiecesGroup.setPosition(offsetX, offsetY);
		
		float pieceTextureWidth = texture.getWidth() / (float)widthPieces;
		float pieceTextureHeight = texture.getHeight() / (float)heightPieces;
		
		puzzlePieceSize.set(puzzleImageSize.x / (float)widthPieces, puzzleImageSize.y / (float)heightPieces);
		
		puzzlePiecesBackground.setSize(puzzleImageSize.x, puzzleImageSize.y);
		puzzlePiecesBackground.setPosition(offsetX, offsetY);
		
		//Generate randomization points
		class Pair{
			Pair(int ix, int iy){x = ix; y = iy;}
			public int x,y;
		}
		Array<Pair> positionIdexes = new Array<Pair>();
		for(int ix = 0; ix < widthPieces; ix++){
			for(int iy = 0; iy < heightPieces; iy++){
				if(ix == (widthPieces - 1) && iy == (heightPieces - 1)){
					continue;
				}				
				positionIdexes.add(new Pair(ix,iy));
			}
		}
		Random ran = new Random();
		for(int ix = 0; ix < widthPieces; ix++){
			float x2 = (float)ix * pieceTextureWidth;
			for(int iy = 0; iy < heightPieces; iy++){
				if(ix == (widthPieces - 1) && iy == (heightPieces - 1)){
					continue;
				}
				float y2 = (float)iy * pieceTextureHeight;
				
				TextureRegion reg1 = new TextureRegion(texture, (int)x2,(int)y2, (int)pieceTextureWidth, (int)pieceTextureHeight);
				
				PuzzlePiece piece = new PuzzlePiece(reg1);
				piece.destinationPosIndexX = ix;
				piece.destinationPosIndexY = iy;
				piece.positionIndexX = ix;
				piece.positionIndexY = iy;				
				piece.setSize(puzzlePieceSize.x, puzzlePieceSize.y);
				piece.addListener(this);				
				puzzlePiecesGroup.addActor(piece);	
								
				//Randomize initial position
				int randP = ran.nextInt(positionIdexes.size);
				Pair p = positionIdexes.get(randP);
				piece.positionIndexX = p.x;
				piece.positionIndexY = p.y;
				positionIdexes.removeIndex(randP);
			}
		}
		
		processPieces();
	}
	private boolean processPieces(){
		
		puzzleComplete = true;		
		SnapshotArray<Actor> pieces = puzzlePiecesGroup.getChildren();
		for(int i=0; i<pieces.size; i++){
			PuzzlePiece piece = (PuzzlePiece)pieces.get(i);
			float x = (float)piece.positionIndexX * puzzlePieceSize.x;
			float y = puzzleImageSize.y - ((float)piece.positionIndexY * puzzlePieceSize.y) - puzzlePieceSize.y;			
			piece.setPosition(x, y);
			if(piece.positionIndexX != piece.destinationPosIndexX  || piece.positionIndexY != piece.destinationPosIndexY){
				puzzleComplete = false;
			}
		}
		for(int ix = 0; ix < puzzlePiecesX; ix++){
			for(int iy = 0; iy < puzzlePiecesY; iy++){
				boolean found = false;
				for(int i=0; i<pieces.size; i++){
					PuzzlePiece piece = (PuzzlePiece)pieces.get(i);
					if(piece.positionIndexX == ix && piece.positionIndexY == iy){
						found = true;
					}
				}
				if(!found){
					puzzleEmptyX = ix;
					puzzleEmptyY = iy;
//					System.out.println(ix+", "+iy);
				}
			}
		}
		return puzzleComplete;
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
	}
	public void swapWithEmpty(PuzzlePiece piece){
		if(Math.abs(piece.positionIndexX - puzzleEmptyX) + Math.abs(piece.positionIndexY - puzzleEmptyY) > 1)return;
		
		piece.positionIndexX = puzzleEmptyX;
		piece.positionIndexY = puzzleEmptyY;

		Runnable r = new Runnable(){
			@Override
		    public void run() {
				processPieces();
				if(puzzleComplete){
					System.out.println("Puzzle complete");
					continueNext();
				}
			}};		
		float x = (float)puzzleEmptyX * puzzlePieceSize.x;
		float y = puzzleImageSize.y - ((float)puzzleEmptyY * puzzlePieceSize.y) - puzzlePieceSize.y;		
		piece.addAction(Actions.sequence(Actions.moveTo(x, y, 0.2f), Actions.run(r)));
	}
	
	public void touchUp (InputEvent e, float x, float y, int pointer, int button) {
		Actor target = e.getListenerActor();
		if(target.getClass() == PuzzlePiece.class){
			PuzzlePiece piece = (PuzzlePiece)target;
			//System.out.println(piece.positionIndexX+", "+piece.positionIndexY);
			swapWithEmpty(piece);
		}
	}
    public boolean touchDown (InputEvent e, float x, float y, int pointer, int button) {
        return true;
    }
}
