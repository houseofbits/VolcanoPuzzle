package com.volcanopuzzle.vcore;

import com.badlogic.gdx.math.Vector2;

public class VConfig {
	
	/********************** Configuration for development ***********************************
	
	public boolean 	developmentMode = true; 
	public Vector2 	resolution = new Vector2(1066f, 600f);
	public boolean 	fullScreen = false;
	public float 	userActionActiveTimeout = 50;

	
	/******************** Configuration for production (HD, full screen) ********************/
	
	public boolean 	developmentMode = false; 
	public Vector2 	resolution = new Vector2(1080f, 1920f);
	public boolean 	fullScreen = true;
	public float 	userActionActiveTimeout = 50;	
				
	/****************************************************************************************/	
	
	private static VConfig config = null;
	
	private VConfig(){}
	
	public static void loadConfig(String filename){
		if(config == null){
			config = new VConfig();	
		}		
	}
	public static VConfig get(){
		return config;
	}	
}
