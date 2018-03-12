package com.volcanopuzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;

public class VCommon {
	
	static ImmediateModeRenderer20 lineRenderer = new ImmediateModeRenderer20(false, true, 0);
	
	static SpriteBatch spriteBatch = new SpriteBatch();
	
	public static float angleCircleDistance(float a, float b){
		float diff = b - a;
        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;
        return diff;
	} 
	
	public static void drawTextLine(String text, float x, float y){
		spriteBatch.begin();	
	//	VStaticAssets.Fonts.calibri18Font.draw(spriteBatch, text, x, y);
		spriteBatch.end();
	}
	
	public static void drawSystemStats(){		
		/*
		float fps = 1.0f/Gdx.graphics.getDeltaTime();		
		spriteBatch.begin();	
		VStaticAssets.Fonts.calibri18Font.draw(spriteBatch, "fps:"+fps, 10, 20);
		spriteBatch.end();		
		*/
	}
	
	public static float lerp(float a, float b, float f){
	    return (a * (1.0f - f)) + (b * f);
	}
	
	// create atomic method for line
	public static void line(float x1, float y1, float z1,
	                        float x2, float y2, float z2,
	                        float r, float g, float b, float a) {
	    lineRenderer.color(r, g, b, a);
	    lineRenderer.vertex(x1, y1, z1);
	    lineRenderer.color(r, g, b, a);
	    lineRenderer.vertex(x2, y2, z2);
	}
	
	// method for whole grid
	public static void grid(int steps, float stepSize) {
		float dimension = steps * stepSize * 0.5f;
	    for (int x = 0; x <= steps; x++) {
	        line((x * stepSize)-dimension, 0, dimension,
	             (x * stepSize)-dimension, 0, -dimension,
	             0.3f, 0.3f, 0.3f, 0);
	    }

	    for (int y = 0; y <= steps; y++) {
	        // draw horizontal
	        line(-dimension, 0, (y * stepSize) - dimension,
	        	dimension, 0, (y * stepSize) - dimension,
	        	0.3f, 0.3f, 0.3f, 0);
	    }
        line(-dimension, 0, 0,
        	dimension, 0, 0,
	        1, 0, 0, 0);
        line(0, 0, 0,
        	stepSize / 2, 0, 0,
	        0, 0, 0, 0);        
        line(0, dimension, 0,
      	     0, -dimension, 0,
      	     0, 1, 0, 0);  
        
        line(0, 0, dimension,
	         0, 0, -dimension,
	         0, 0, 1, 0);
        line(0, 0, 0,
             0, 0, stepSize / 2,
    	     0, 0, 0, 0);                
        line(0, stepSize / 2, 0,
             0, 0, 0,
       	     0, 0, 0, 0);         
      
	}
	public static void drawGrid(PerspectiveCamera cam){
		
		lineRenderer.begin(cam.combined, GL30.GL_LINES);
			grid(50, 50);
		lineRenderer.end();		
	}
	
	
}
