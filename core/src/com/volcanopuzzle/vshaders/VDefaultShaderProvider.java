package com.volcanopuzzle.vshaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.volcanopuzzle.vcore.VMain;

public class VDefaultShaderProvider extends DefaultShaderProvider{
	
	protected VMain volcano = null;

	protected 	DefaultShader.Config shaderConfig = null;
	protected 	ShaderProgram	shaderProgram = null;
	
	protected String vsName = "";
	protected String fsName = "";
	protected String vsString = "";
	protected String fsString = "";	
	
	public VDefaultShaderProvider(VMain s, String vname, String fname){
		super();
		volcano = s;
		vsName = vname;
		fsName = fname;
//		volcano.assetsManager.load(vsName, TextAsset.class);		
//		volcano.assetsManager.load(fsName, TextAsset.class);
		shaderConfig = new DefaultShader.Config();
	}		
	public void setDepthFunc(int depth){
		shaderConfig.defaultDepthFunc = depth;		
	}
	public void onLoad(){
//		if(volcano.assetsManager.isLoaded(vsName)
//				&& volcano.assetsManager.isLoaded(fsName)) {			
//			vsString = volcano.assetsManager.get(vsName, TextAsset.class ).getString();
//			fsString = volcano.assetsManager.get(fsName, TextAsset.class ).getString();
//		}
	}
	public Shader getShader (Renderable renderable) {
		Shader shader = renderable.shader;		
		if (shader != null && shader.canRender(renderable)) return shader;
        
        if(shaderProgram == null){
	        String prefix = DefaultShader.createPrefix(renderable, shaderConfig);
	        shaderProgram = new ShaderProgram(prefix + vsString, prefix + fsString);
        }        
        DefaultShader defaultShader = new DefaultShader(renderable, shaderConfig, shaderProgram);
        defaultShader.init();
		
        return defaultShader;
	}	
}


