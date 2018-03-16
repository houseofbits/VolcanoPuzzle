package com.volcanopuzzle.vshaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.volcanopuzzle.vcore.VMain;

public class VDefaultShaderProvider extends DefaultShaderProvider{
	
	protected VMain volcano = null;

	protected 	DefaultShader.Config shaderConfig = null;
	protected 	ShaderProgram	shaderProgram = null;
	
	protected String vsString = "";
	protected String fsString = "";	
	protected boolean depthOnly = false;
	
	public VDefaultShaderProvider(VMain s){
		this(s, null, null, true);	
	}
	
	public VDefaultShaderProvider(VMain s, String vname, String fname, boolean depth){
		super();
		volcano = s;
		if(vname!=null)vsString = Gdx.files.internal(vname).readString();
		if(vname!=null)fsString = Gdx.files.internal(fname).readString();
		shaderConfig = new DefaultShader.Config();
		depthOnly = depth;
	}	
	public VDefaultShaderProvider(VMain s, String vname, String fname){
		this(s, vname, fname, false);
	}		
	public void setDepthFunc(int depth){
		shaderConfig.defaultDepthFunc = depth;		
	}
	public Shader getShader (Renderable renderable) {
		
		if(depthOnly){
			return new DepthShader(renderable);
		}
		
		Shader shader = renderable.shader;		
		if (shader != null && shader.canRender(renderable)) return shader;
        
        if(shaderProgram == null){
	        String prefix = DefaultShader.createPrefix(renderable, shaderConfig);
	        shaderProgram = new ShaderProgram(prefix + vsString, prefix + fsString);
        }        
        DefaultShader defaultShader;
        
        defaultShader = new DefaultShader(renderable, shaderConfig, shaderProgram);
        defaultShader.init();
		
        return defaultShader;
	}	
}


