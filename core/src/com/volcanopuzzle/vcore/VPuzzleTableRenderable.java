package com.volcanopuzzle.vcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class VPuzzleTableRenderable {
	
	public VMain	volcano;
	
	protected ModelBatch modelBatch = null;
	protected ModelBatch modelDepthBatch = null;
	protected ModelInstance modelInstance = null;	
	
	public Vector3 imageBackgroundSize = new Vector3(1,1,1);
	
	public void setImageBackgroundSize(float x, float y){
		modelInstance.transform.idt().scale(x, 1, y);
	}
	
	public VPuzzleTableRenderable(VMain v){
		
		volcano = v;
		
        modelBatch = new ModelBatch(volcano.tableShader);
        modelDepthBatch = new ModelBatch(volcano.depthShader);
        modelInstance = new ModelInstance(buildTable());
        
        setImageBackgroundSize(330,200);

        setLightDepthTexture(null, volcano.lightDepthTexture.get());
	}
    public void render(PerspectiveCamera cam, Environment env){
    	
    	modelBatch.begin(cam);
        if(modelInstance != null){
        	modelBatch.render(modelInstance, env);
        }
        modelBatch.end();       
    }
    public void renderDepth(PerspectiveCamera cam, Environment env){
        modelDepthBatch.begin(cam);
        if(modelInstance != null){
        	modelDepthBatch.render(modelInstance, env);
        }
        modelDepthBatch.end();   
    }    
    public void setTransparency(String id, float f){    
    	BlendingAttribute b = new BlendingAttribute(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		b.opacity = f;
    	Node n = getNode(id);
    	if(n!=null){
    		for(int i=0; i<n.parts.size; i++){
    			if(f < 0.001f)n.parts.get(i).enabled = false;
    			else{
    				NodePart np = n.parts.get(i);
    				np.material.set(b);
    				np.enabled = true;
    			}
    		}
    	}		
    } 
    public void setDiffuseTexture(String id, Texture texture){
    	setNodeMaterialAttribute(id, new TextureAttribute(TextureAttribute.Diffuse, texture));
    }
    public void setLightDepthTexture(String id, Texture texture){
    	setNodeMaterialAttribute(id, new TextureAttribute(TextureAttribute.Ambient, texture));
    }    
    public void setNodeMaterialAttribute(String id, Attribute attr){
    	Node n = getNode(id);
    	if(n!=null){
    		for(int i=0; i<n.parts.size; i++){
    			n.parts.get(i).material.set(attr);
    		}
    	}
    }
    public Node getNode(String id){
    	if(id != null && id.length() > 0)return modelInstance.getNode(id, true);
    	else return modelInstance.nodes.get(0);
    }     
    //TODO Disposing of renderables
	public void dispose(){
		modelBatch.dispose();
	}
	
	private Model buildTable(){
		
		MeshBuilder meshBuilder = new MeshBuilder();
		
		//Top face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);		
		meshBuilder.part("piece", GL30.GL_TRIANGLE_STRIP);
			
		float y = -1.2f;
		
		short idx = meshBuilder.vertex(new Vector3(-0.5f, y, -0.5f), new Vector3(0,1,0), new Color(), new Vector2(0, 0));
		meshBuilder.index(idx);		
		idx = meshBuilder.vertex(new Vector3(-0.5f, y, 0.5f), new Vector3(0,1,0), new Color(), new Vector2(0, 1));
		meshBuilder.index(idx);		
		idx = meshBuilder.vertex(new Vector3(0.5f, y, -0.5f), new Vector3(0,1,0), new Color(), new Vector2(1, 0));
		meshBuilder.index(idx);		
		idx = meshBuilder.vertex(new Vector3(0.5f, y, 0.5f), new Vector3(0,1,0), new Color(), new Vector2(1, 1));
		meshBuilder.index(idx);		

		Mesh mesh1 = meshBuilder.end();
		
		ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();

	    modelBuilder.part("piece",
	            mesh1,
	            GL30.GL_TRIANGLE_STRIP,
	            new Material(ColorAttribute.createDiffuse(Color.WHITE)));
	    		//new Material(ColorAttribute.createDiffuse(new Color((int)(Math.random() * 16777215)))));		
		
	    Model model = modelBuilder.end(); 
	    
		return model;
	}
	
}
