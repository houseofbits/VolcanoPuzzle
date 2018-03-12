package com.volcanopuzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class VStageVoronoi extends InputListener {
	
	public Stage mainStage = null;
	 
	
	public VStageVoronoi(){
		
		Array<Vector2> points = new Array<Vector2>();				
		points.add(new Vector2(100,50));
		points.add(new Vector2(150,100));
		points.add(new Vector2(200,50));		
		
		Array<Vector2> initialShape = new Array<Vector2>();
		initialShape.add(new Vector2(0,0));
		initialShape.add(new Vector2(0,1000));
		initialShape.add(new Vector2(1000,1000));
		initialShape.add(new Vector2(1000,0));		
		
//		sliceConvexPolygon(points, new Vector2(1,0), new Vector2(0,80));
//
//		for(int n = 0; n < points.size; n++){
//			Vector2 p = points.get(n);
//			System.out.println(p);
//		}
		
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
			System.out.println("----------------------------------");
			for(int n = 0; n < shape.size; n++){
				Vector2 p1 = shape.get(n);
				System.out.println(p1);
			}			
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
		            float r = (((pos.y - p1.y) * (p2.x - p1.x)) - (pos.x - p1.x) * (p2.y - p1.y)) / d;
		            float s = (((pos.y - p1.y) * dir.x) - (pos.x - p1.x) * dir.y) / d;
		            //System.out.println(r+", "+s);
		            if (s >= 0 && s <= 1){
		            	Vector2 vi = new Vector2(pos.x + r * dir.x, pos.y + r * dir.y);
		            	pointsOut.add(vi);
		            }
				}
			}
		}
		points.clear();
		points.addAll(pointsOut);
	}	
	
	public void create(){
		
		mainStage = new Stage();
		
        Gdx.input.setInputProcessor(mainStage);		
		
		mainStage.setDebugAll(true);
//		mainStage.setDebugUnderMouse(true);
	}
	
	public void render(){
		mainStage.act(Gdx.graphics.getDeltaTime());
		mainStage.draw();
	}
	
	public void touchUp (InputEvent e, float x, float y, int pointer, int button) {
		Actor target = e.getListenerActor();

	}
    public boolean touchDown (InputEvent e, float x, float y, int pointer, int button) {
        return true;
    }
}
