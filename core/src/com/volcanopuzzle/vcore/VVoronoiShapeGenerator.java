package com.volcanopuzzle.vcore;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class VVoronoiShapeGenerator {
	
	public OrthographicCamera camera;
	public ShapeRenderer shapeRenderer;
	
	class PieceShape{
		public PieceShape(Array<Vector2> s){
			shape = s;
			Vector2 posSum = new Vector2();
			for(int i=0;i<shape.size; i++){
				posSum.add(shape.get(i));
			}
			posSum.scl(1.0f/(float)shape.size);		
			position.set(posSum);
		}
		public Vector2 position = new Vector2();
		public Array<Vector2> shape = null;
	}
	
	public Array<PieceShape> pieceShapes = new Array<PieceShape>();
	Array<Vector2> 			points = new Array<Vector2>();
	
	public VVoronoiShapeGenerator(){
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		shapeRenderer = new ShapeRenderer();
		camera.update();
	}
	
	public void generate(int numPoints, float minDst){
		generatePoints(numPoints,minDst);
		generateRegions();
	}
	
	public void generatePoints(int numPoints, float minDst){
		
		points.clear();
		
		Vector2 size = new Vector2(1,1);
		Random rnd = new Random();

		while(points.size < numPoints){
			Vector2 p = new Vector2((float)rnd.nextFloat() * size.x, (float)rnd.nextFloat() * size.y);
			
			boolean tooClose = false;
			for(int n = 0; n < points.size; n++){
				Vector2 pn = points.get(n);
				if(p.cpy().sub(pn).len() < minDst){
					tooClose = true;
					break;
				}
			}			
			if(!tooClose)points.add(p);
		}
	}
	
	public void generateRegions(){
		
		pieceShapes.clear();
		
		Array<Vector2> initialShape = new Array<Vector2>();
		initialShape.add(new Vector2(0,0));
		initialShape.add(new Vector2(0,1));
		initialShape.add(new Vector2(1,1));
		initialShape.add(new Vector2(1,0));
		
		for(int i=0; i < points.size; i++){
			Array<Vector2> pointsTmp = new Array<Vector2>(points);
			pointsTmp.removeIndex(i);		
			Vector2 p = points.get(i);
			Array<Vector2> shape = new Array<Vector2>(initialShape);
			for(int n = 0; n < pointsTmp.size; n++){				
				Vector2 pn = pointsTmp.get(n);
				Vector2 halfVec = pn.cpy().sub(p).scl(0.5f);

				Vector2 splitDir = halfVec.cpy().rotate90(-1);
				Vector2 splitPos = p.cpy().add(halfVec);
				
				sliceConvexPolygon(shape, splitDir, splitPos);
			}
//			for(int n = 0; n < shape.size; n++){
//				Vector2 p1 = shape.get(n);
//				System.out.println(p1);
//			}
			pieceShapes.add(new PieceShape(shape));
		}	
	}
	
	public void sliceConvexPolygon(Array<Vector2> points, Vector2 dir, Vector2 pos){
		dir.nor();
		Array<Vector2> pointsOut = new Array<Vector2>();
		for(int n = 0; n < points.size; n++){
			int n1 = (n+1)%points.size;
			Vector2 p1 = points.get(n);
			Vector2 p2 = points.get(n1);			
			Vector2 ptop = new Vector2(p1.cpy().sub(pos));			
			float cross = ptop.crs(dir);
			if(cross >= 0)pointsOut.add(p1);
			if((dir.y / dir.x) != ((p2.y - p1.y) / (p2.x - p1.x))){
				float d = ((dir.x * (p2.y - p1.y)) - dir.y * (p2.x - p1.x));
				//System.out.println(p1+" "+d);
				if(d != 0){
		            float r = (float)(((pos.y - p1.y) * (p2.x - p1.x)) - (pos.x - p1.x) * (p2.y - p1.y)) / d;
		            float s = (float)(((pos.y - p1.y) * dir.x) - (pos.x - p1.x) * dir.y) / d;
		            //System.out.println(r+", "+s);
		            if (s >= 0 && s <= 1){
		            	Vector2 vi = new Vector2((float)(pos.x + r * dir.x), (float)(pos.y + r * dir.y));
		            	pointsOut.add(vi);
		            }
				}
			}
		}
		points.clear();
		points.addAll(pointsOut);
	}
	public void render(){
		float scale = 250;
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for(int i = 0; i < pieceShapes.size; i++){
			PieceShape ps = pieceShapes.get(i);
			
			for(int n = 0; n < ps.shape.size; n++){
				Vector2 p1 = ps.shape.get(n).cpy().scl(scale);
				Vector2 p2 = ps.shape.get((n+1)%ps.shape.size).cpy().scl(scale);				
				
				shapeRenderer.setColor(1f, 1f, 0f, 1);
				shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
			}
		}
		shapeRenderer.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for(int i=0; i<points.size; i++){
			Vector2 p = points.get(i).cpy().scl(scale);
			shapeRenderer.setColor(0, 0, 1, 1);
			shapeRenderer.rect(p.x-1, p.y-1, 2, 2);
		}
		shapeRenderer.end();
	}
}
