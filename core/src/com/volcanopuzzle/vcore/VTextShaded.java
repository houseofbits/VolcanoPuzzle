package com.volcanopuzzle.vcore;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class VTextShaded {

	static SpriteBatch spriteBatch;
	
	public VTextShaded(){
		
		
		
	}
	
	public void draw(){
		spriteBatch.begin();	
		VStaticAssets.Fonts.calibri18Font.draw(spriteBatch, "", 0, 0);
		spriteBatch.end();
	}
}
