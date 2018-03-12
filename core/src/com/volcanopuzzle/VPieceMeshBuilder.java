package com.volcanopuzzle;

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

public class VPieceMeshBuilder {
	
	public VPuzzlePieceRenderable build(VVoronoiShapeGenerator.PieceShape shape, Vector2 scale){
		
		Model model = build(shape.shape, scale);
		
		VPuzzlePieceRenderable renderable = new VPuzzlePieceRenderable(model);
		
		Vector2 t = shape.position.cpy().scl(scale);
		
		renderable.translate(new Vector3(t.x, 0, t.y));
		
		return renderable;
	}	
	
	public Model build(Array<Vector2> shape, Vector2 scale){
		
		MeshBuilder meshBuilder = new MeshBuilder();
				
		//pos, norm, col, uv		
		short idx = 0;
		
		float piecey = (float)(Math.random() * 20.0f);
		
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
			idx = meshBuilder.vertex(new Vector3((p.x - posSum.x)* scale.x, piecey, (p.y - posSum.y) * scale.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);
		}
		Mesh mesh1 = meshBuilder.end();

		//Edge face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);		
		meshBuilder.part("pieceTop", GL30.GL_TRIANGLE_STRIP);		
		for(int i=0;i<shape.size; i++){
			Vector2 p = shape.get(i);
//			idx = meshBuilder.vertex(new Vector3((p.x - posSum.x)* scale.x, piecey, (p.y - posSum.y) * scale.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
//			meshBuilder.index(idx);
		}
		Mesh mesh2 = meshBuilder.end();
		
		
		ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();

	    modelBuilder.part("pieceTop",
	            mesh1,
	            GL30.GL_TRIANGLE_FAN,
	            new Material(ColorAttribute.createDiffuse(Color.WHITE)));
	    		//new Material(ColorAttribute.createDiffuse(new Color((int)(Math.random() * 16777215)))));
	    
	    //TODO Build piece edges
	    
	    Model model = modelBuilder.end();   
	    return model;
	}
	
}
