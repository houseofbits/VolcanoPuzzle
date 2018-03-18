package com.volcanopuzzle.vshaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.volcanopuzzle.vcore.VMain;
//
//https://learnopengl.com/Advanced-Lighting/Shadows/Shadow-Mapping
//http://www.microbasic.net/tutorials/shadow-mapping/Full.html
//
public class VShadowShaderProvider extends DefaultShaderProvider{
	
	protected VMain volcano = null;

	protected 	DefaultShader.Config shaderConfig = null;
	protected 	ShaderProgram	shaderProgram = null;
	
	protected String vsString = "";
	protected String fsString = "";	
	protected boolean depthOnly = false;
	
	public VShadowShaderProvider(VMain s){
		this(s, null, null, true);	
	}
	
	public VShadowShaderProvider(VMain s, String vname, String fname, boolean depth){
		super();
		volcano = s;
		if(vname!=null)vsString = Gdx.files.internal(vname).readString();
		if(vname!=null)fsString = Gdx.files.internal(fname).readString();
		shaderConfig = new DefaultShader.Config();
		depthOnly = depth;
	}	
	public VShadowShaderProvider(VMain s, String vname, String fname){
		this(s, vname, fname, false);
	}		
	public void setDepthFunc(int depth){
		shaderConfig.defaultDepthFunc = depth;		
	}
	public Shader getShader (Renderable renderable) {

		Shader shader = renderable.shader;		
		if (shader != null && shader.canRender(renderable)) return shader;
        
        if(shaderProgram == null){
	        String prefix = DefaultShader.createPrefix(renderable, shaderConfig);
	        shaderProgram = new ShaderProgram(prefix + vsString, prefix + fsString);
        }        
        DefaultShader defaultShader;
        
        shaderProgram.begin();
        shaderProgram.pedantic = false;
        shaderProgram.setUniformf("u_lightPosition", volcano.lightView.position);
        shaderProgram.setUniformMatrix("u_lightTrans", volcano.lightView.combined);
        shaderProgram.end();
        
        defaultShader = new DefaultShader(renderable, shaderConfig, shaderProgram);
        defaultShader.init();
		
        return defaultShader;
	}	
}


