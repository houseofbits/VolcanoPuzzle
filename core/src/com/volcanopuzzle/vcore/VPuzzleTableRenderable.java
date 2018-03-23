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
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class VPuzzleTableRenderable {
	
	public VMain	volcano;
	
	protected ModelBatch modelBatch = null;
	protected ModelBatch modelBatchInfo = null;
	protected ModelBatch modelDepthBatch = null;
	protected ModelInstance modelTableInst = null;	
	
	protected ModelInstance modelTitleInst = null;	
	protected ModelInstance modelFooterInst = null;	
	
	protected Texture	diffuseTexture;
	
	public Vector3 imageBackgroundSize = new Vector3(1,1,1);
	
	public void setImageBackgroundSize(float x, float y){
		modelTableInst.transform.idt().scale(x, 1, y);
	}
	
	public VPuzzleTableRenderable(VMain v){
		
		volcano = v;
		
        modelBatch = new ModelBatch(volcano.tableShader);
        modelBatchInfo = new ModelBatch();
        modelDepthBatch = new ModelBatch(volcano.depthShader);
        modelTableInst = new ModelInstance(buildRect(-1.2f));

        modelTitleInst = new ModelInstance(buildRect(-1.0f));        
        modelFooterInst = new ModelInstance(buildRect(-1.0f));
        
        diffuseTexture = new Texture(Gdx.files.internal("tableBg2.png"));
        
        diffuseTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        setImageBackgroundSize(330,200);

        setReflectionTexture(null, diffuseTexture);
        
        setLightDepthTexture(null, volcano.lightDepthTexture.get());
        
        modelTitleInst.transform.translate(0, 0, -70);
        modelTitleInst.transform.scale(170, 1, 40);

        modelFooterInst.transform.translate(0, 0, 70);
        modelFooterInst.transform.scale(170, 1, 30);        
        
	}
    public void render(PerspectiveCamera cam, Environment env){
    	
    	modelBatch.begin(cam);
        if(modelTableInst != null){
        	modelBatch.render(modelTableInst, env);
        }
        modelBatch.end();       
//    	
//    	modelBatchInfo.begin(cam);
//    	modelBatchInfo.render(modelTitleInst, env);
//    	modelBatchInfo.render(modelFooterInst, env);
//    	modelBatchInfo.end();
        
    }
    public void renderDepth(PerspectiveCamera cam, Environment env){
        modelDepthBatch.begin(cam);
        if(modelTableInst != null){
        	modelDepthBatch.render(modelTableInst, env);
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
    public void setReflectionTexture(String id, Texture texture){
    	setNodeMaterialAttribute(id, new TextureAttribute(TextureAttribute.Reflection, texture));
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
    	if(id != null && id.length() > 0)return modelTableInst.getNode(id, true);
    	else return modelTableInst.nodes.get(0);
    }     
    //TODO Disposing of renderables
	public void dispose(){
		modelBatch.dispose();
	}
	
	private Model buildRect(float y){
		
		MeshBuilder meshBuilder = new MeshBuilder();
		
		//Top face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);
		meshBuilder.part("piece", GL30.GL_TRIANGLE_STRIP);
		
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
	            new Material(ColorAttribute.createDiffuse(Color.WHITE),
	            			ColorAttribute.createReflection(1, 1, 1, 1)));
		
	    Model model = modelBuilder.end(); 
	    
		return model;
	}
	
}
