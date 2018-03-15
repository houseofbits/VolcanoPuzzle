package com.volcanopuzzle.vcore;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class VPieceRenderableBuilder {
	
	public Array<Vector2> randomDistributedPoints = new Array<Vector2>();
	
	public void generateDistributionPoints(int numPoints, Vector2 innerSize, Vector2 outerSize){
		
		randomDistributedPoints.clear();
		
		Vector2 outerPos = outerSize.cpy().scl(0.5f).sub(outerSize);
		Vector2 innerPos = innerSize.cpy().scl(0.5f).sub(innerSize);
		Random rnd = new Random();
		
		float minDst = Math.min(450.0f / numPoints, 30);
		
		while(randomDistributedPoints.size < numPoints){
			Vector2 p = new Vector2(((float)rnd.nextFloat() * outerSize.x) + outerPos.x, ((float)rnd.nextFloat() * outerSize.y) + outerPos.y);
			
			if(isPointInRect(p, innerPos, innerSize))continue;

			boolean tooClose = false;
			for(int n = 0; n < randomDistributedPoints.size; n++){
				Vector2 pn = randomDistributedPoints.get(n);
				if(p.cpy().sub(pn).len() < minDst){
					tooClose = true;
					break;
				}
			}
			
			if(!tooClose)randomDistributedPoints.add(p);
		}
	}
	/*
	public VPuzzlePieceRenderable build(VVoronoiShapeGenerator.PieceShape shape, Vector2 size){
		
		Model model = build(shape.shape, size);
		
		VPuzzlePieceRenderable renderable = new VPuzzlePieceRenderable(model);
		
		Vector2 t = shape.position.cpy().scl(size).sub(size.cpy().scl(0.5f));
		
		renderable.originalPosition.set(t.x, 0, t.y);

		renderable.translate(renderable.originalPosition);
		
		return renderable;
	}	
	
	public Model build(Array<Vector2> shape, Vector2 scale){
		
		MeshBuilder meshBuilder = new MeshBuilder();
				
		//pos, norm, col, uv
		short idx = 0;
		
		float pieceHeight = 2;//(float)(Math.random() * 20.0f);
		
		Vector2 posSum = new Vector2();
		for(int i=0;i<shape.size; i++){
			posSum.add(shape.get(i));
		}
		posSum.scl(1.0f/(float)shape.size);
		
		//Top face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);		
		meshBuilder.part("pieceTop", GL30.GL_TRIANGLE_FAN);		
		for(int i=0;i<shape.size; i++){
			
			Vector2 p = shape.get(i);			
			Vector2 pv = p.cpy().sub(posSum).scl(scale);
			
			idx = meshBuilder.vertex(new Vector3(pv.x, pieceHeight, pv.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);
		}
		Mesh mesh1 = meshBuilder.end();

		//Edge face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);		
		meshBuilder.part("pieceEdge", GL30.GL_TRIANGLE_STRIP);		
		for(int n=0; n<=shape.size; n++){
			
			int i = n%shape.size;
			Vector2 p = shape.get(i);			
			Vector2 pv = p.cpy().sub(posSum).scl(scale);
			
			idx = meshBuilder.vertex(new Vector3(pv.x, pieceHeight, pv.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);			
			idx = meshBuilder.vertex(new Vector3(pv.x, 0, pv.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);			
		}
		Mesh mesh2 = meshBuilder.end();
		
		ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();

	    modelBuilder.part("pieceTop",
	            mesh1,
	            GL30.GL_TRIANGLE_FAN,
	            new Material(ColorAttribute.createDiffuse(Color.WHITE)));
	    		//new Material(ColorAttribute.createDiffuse(new Color((int)(Math.random() * 16777215)))));
	    
	    modelBuilder.part("pieceEdge",
	            mesh2,
	            GL30.GL_TRIANGLE_STRIP,
	            new Material(ColorAttribute.createDiffuse(Color.GRAY)));
	    		//new Material(ColorAttribute.createDiffuse(new Color((int)(Math.random() * 16777215)))));
	    
	    Model model = modelBuilder.end();   
	    return model;
	}
	*/
	public boolean isPointInRect(Vector2 p, Vector2 rectPos, Vector2 rectSize){
		
		Vector2 pp = p.cpy().sub(rectPos);
		
		if(pp.x < 0 || pp.x > rectSize.x)return false;

		if(pp.y < 0 || pp.y > rectSize.y)return false;		
		
		return true;
	}
}
