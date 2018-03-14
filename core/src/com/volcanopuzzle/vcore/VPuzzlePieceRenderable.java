package com.volcanopuzzle.vcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class VPuzzlePieceRenderable {
	
	protected ModelBatch modelBatch = null;
	protected ModelInstance modelInstance = null;	
	public Vector3	originalPosition = new Vector3();
	public Vector3	startPosition = new Vector3();
    float[] vertices = null;
    short[] indices = null;
    int   vertexSize = 0;
	Vector3 translation = new Vector3();
	public Plane surfacePlane = new Plane();	
	public boolean isFinished = false;
	
	public boolean setTransferToInitialPosition = false;
	float transferVelocity = 0;
	
	public VPuzzlePieceRenderable(Model model){
		
        modelInstance = new ModelInstance(model);
        modelBatch = new ModelBatch();
        loadIntersectionMesh();
        transferVelocity = 0;
	}
    public void render(PerspectiveCamera cam, Environment env){

    	float dt = Gdx.graphics.getDeltaTime();
    	
    	getTranslation();
    	
    	if(setTransferToInitialPosition){
    		
    		Vector3 dst = translation.cpy().sub(startPosition); 
    		float l = dst.len();
    		if(l <= 0.1f){
    			setTransferToInitialPosition = false;
    		}else{
    			
    			if(l > 10){
    				dst.nor().scl(10);
    			}
    			
	    		dst.scl(dt * transferVelocity);
	    		
	    		transferVelocity += 40 * dt;
	    		
	    		translate(translation.sub(dst));
    		}	
    	}else{
	    	Vector3 dst = translation.cpy().sub(originalPosition);    	
	    	float len = dst.len();
	    	float margin = 10;
	    	if(len < margin && len > 0.1f){
	    		float force = 1.0f  - (len / margin);
	    		dst.scl(force * dt * 20);
	    		translate(translation.sub(dst));
	    	}
	    	if(len <= 0.1f){
	    		isFinished = true;
	    		translate(originalPosition);
	    	}
    	}
    	
        modelBatch.begin(cam);
        if(modelInstance != null){
        	modelBatch.render(modelInstance, env);
        }
        modelBatch.end();       
    }
    public void translate(Vector3 pos){
        if(modelInstance != null) {
            modelInstance.transform.idt();
            modelInstance.transform.translate(pos);
            modelInstance.calculateTransforms();
            getTranslation();
            //translation + piece height
            surfacePlane.set(new Vector3(0, translation.y + 5, 0), new Vector3(0,1,0));
        }
    }
    public Vector3 getTranslation(){
    	modelInstance.transform.getTranslation(translation);
    	return translation;
    }
    public void setDiffuseTexture(String id, Texture texture){
    	setNodeMaterialAttribute(id, new TextureAttribute(TextureAttribute.Diffuse, texture));
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

    public void loadIntersectionMesh(){    	
    	MeshPart mpart = modelInstance.model.meshParts.get(0);
    	Mesh mesh = mpart.mesh;
    	
    	vertexSize = mesh.getVertexSize() / 4;
        vertices = new float[mesh.getNumVertices() * mesh.getVertexSize() / 4];
        mesh.transform(modelInstance.transform.cpy());
        mesh.getVertices(vertices);    	
    	
    	if(mpart.primitiveType == GL30.GL_TRIANGLE_FAN){
    		int numFanIndices = mesh.getNumIndices();
        	short[] fanIndices = new short[numFanIndices];
        	mesh.getIndices(fanIndices);
        	
        	indices = new short[(numFanIndices  - 2) * 3];
        	int n=0;
        	for(int i=1; i<numFanIndices - 1; i++){
        		//0, i, i+1
        		indices[n] = fanIndices[0];
        		n++;
        		indices[n] = fanIndices[i];        		
        		n++;
        		indices[n] = fanIndices[i+1]; 
        		n++;
        	}
    	}
    }
	
    public boolean IntersectRay(Ray ray, Vector3 point){
    	
        if(vertices == null || vertexSize == 0)return false;

        Ray r = ray.cpy();
        r.mul(modelInstance.transform.cpy().inv());
        if(Intersector.intersectRayTriangles(r, vertices, indices, vertexSize, point)) {
            point.mul(modelInstance.transform);
            return true;
        }
        return false;
    }	
	
	
}
