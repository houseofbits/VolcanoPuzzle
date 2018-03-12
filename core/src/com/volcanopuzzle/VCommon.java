package com.volcanopuzzle;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class VCommon {
	
//	static ImmediateModeRenderer20 lineRenderer = new ImmediateModeRenderer20(false, true, 0);
	static public ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	
	//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	//shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	
	// create atomic method for line
	public static void line(float x1, float y1, float z1,
	                        float x2, float y2, float z2,
	                        float r, float g, float b, float a) {
//	    lineRenderer.color(r, g, b, a);
//	    lineRenderer.vertex(x1, y1, z1);
//	    lineRenderer.color(r, g, b, a);
//	    lineRenderer.vertex(x2, y2, z2);
	}
	
	public static void drawGrid(PerspectiveCamera cam){
		
//		lineRenderer.begin(cam.combined, GL30.GL_LINES);
////		shapeRenderer.setColor(0.5f, 0.5f, 1, 1);
////		shapeRenderer.rect(p.x-5, p.y-5, 10, 10);
//		lineRenderer.end();		
	}
	
}
