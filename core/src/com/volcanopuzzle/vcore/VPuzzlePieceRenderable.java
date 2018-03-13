package com.volcanopuzzle.vcore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;

public class VPuzzlePieceRenderable {
	
	protected ModelBatch modelBatch = null;
	protected ModelInstance modelInstance = null;
	
	public Vector3	originalPosition = new Vector3();
	
	public VPuzzlePieceRenderable(Model model){
		
        modelInstance = new ModelInstance(model);
        modelBatch = new ModelBatch();
		
	}
    public void render(PerspectiveCamera cam, Environment env){
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
        }
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
	
	/*

    public BoundingBox boundingBox = new BoundingBox();
    public Matrix4  transform = new Matrix4();
    float[] vertices = null;
    short[] indices = null;
    int   vertexSize = 0;
    
    public void LoadMeshes(Mesh mesh, Matrix4 transform){

        vertexSize = mesh.getVertexSize() / 4;
        vertices = new float[mesh.getNumVertices() * mesh.getVertexSize() / 4];
        mesh.transform(transform.cpy());
        mesh.getVertices(vertices);
        indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);
    }

    public boolean IntersectRay(Ray ray, Vector3 point){

        if(vertices == null || vertexSize == 0)return false;

        Ray r = ray.cpy();
        r.mul(transform.cpy().inv());
        if(Intersector.intersectRayTriangles(r, vertices, indices, vertexSize, point)) {
            point.mul(transform);
            return true;
        }
        return false;
    }	
	*/
	
}
