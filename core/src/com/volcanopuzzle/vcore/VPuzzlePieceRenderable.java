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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class VPuzzlePieceRenderable {
	
	public VMain	volcano;
	
	protected ModelBatch modelBatch = null;
	protected ModelBatch modelDepthBatch = null;
	protected ModelInstance pieceModelInstance = null;	
	
	protected ModelInstance shadowModelInstance = null;	
	
	public Vector3	originalPosition = new Vector3();
	public Vector3	startPosition = new Vector3();
    float[] vertices = null;
    short[] indices = null;
    int   vertexSize = 0;
	Vector3 translation = new Vector3();
	public Plane surfacePlane = new Plane();	
	public boolean isFinished = false;
	public boolean isGrabbed = false;
	
	public boolean setTransferToInitialPosition = false;
	float transferVelocity = 0;
	
	float targetY = 0;
	boolean moveToY = false;
	
	public VPuzzlePieceRenderable(VMain v, VVoronoiShapeGenerator.PieceShape shape, Vector2 size){
		
		volcano = v;
		
		Model model = buildPiece(shape, size);

		pieceModelInstance = new ModelInstance(model);
        modelBatch = new ModelBatch(volcano.puzzlePieceShader);
        modelDepthBatch = new ModelBatch(volcano.depthShader);
		
		Vector2 t = shape.position.cpy().scl(size).sub(size.cpy().scl(0.5f));
		
		originalPosition.set(t.x, 0, t.y);

		setLightDepthTexture(null, volcano.lightDepthTexture.get());
		
        loadIntersectionMesh();
        transferVelocity = 0;		
	}
	
	public void update(){

    	float dt = Gdx.graphics.getDeltaTime();
    	
    	getTranslation();
    	
    	if(setTransferToInitialPosition){
    		
    		Vector3 dst = translation.cpy().sub(startPosition); 
    		float l = dst.len();
    		if(l <= 1f){
    			setTransferToInitialPosition = false;
    		}else{
    			
    			if(l > 10){
    				dst.nor().scl(10);
    			}
    			
	    		dst.scl(dt * transferVelocity);
	    		
	    		transferVelocity += l * dt;
	    		
	    		if(transferVelocity > l)transferVelocity = l;
	    		
	    		translate(translation.sub(dst));
    		}	
    	}else{
    		isGrabbed = false;
	    	Vector3 dst = translation.cpy().sub(originalPosition);    	
	    	float len = dst.len();
	    	float margin = 8;
	    	if(len < margin && len > 0.1f){
	    		float force = 1.0f  - (len / margin);
	    		dst.scl(force * dt * 20);
	    		translate(translation.sub(dst));
	    		isGrabbed = true;
	    	}
	    	if(len <= 0.1f){
	    		isFinished = true;
	    		translate(originalPosition);
	    	}	    
    	}
    	if(this.moveToY){
    		float relY = targetY - originalPosition.y;
    		if(Math.abs(relY) < 0.1f){
    			originalPosition.y = targetY;
    			moveToY = false;
    			translate(originalPosition);
    		}else{
    			originalPosition.y += relY * 5 * dt;
    			translate(originalPosition);
    		}
    	}    	
	}	
	public void moveToY(float target){
		moveToY = true;
		targetY = target;
	}
    public void render(PerspectiveCamera cam, Environment env){
        modelBatch.begin(cam);
        if(pieceModelInstance != null){
        	modelBatch.render(pieceModelInstance, env);
        }
        modelBatch.end();
    }
    public void renderDepth(PerspectiveCamera cam, Environment env){
        modelDepthBatch.begin(cam);
        if(pieceModelInstance != null){
        	modelDepthBatch.render(pieceModelInstance, env);
        }
        modelDepthBatch.end();   
    }
    
    public void translate(Vector3 pos){
        if(pieceModelInstance != null) {
            pieceModelInstance.transform.idt();
            pieceModelInstance.transform.translate(pos);
            pieceModelInstance.calculateTransforms();
            getTranslation();
            //translation + piece height
            surfacePlane.set(new Vector3(0, translation.y + 1, 0), new Vector3(0,1,0));
        }
    }
    public Vector3 getTranslation(){
    	pieceModelInstance.transform.getTranslation(translation);
    	return translation;
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
    	if(id != null && id.length() > 0)return pieceModelInstance.getNode(id, true);
    	else return pieceModelInstance.nodes.get(0);
    }     
    //TODO Disposing of renderables
	public void dispose(){
		modelBatch.dispose();
		modelDepthBatch.dispose();
	}

    public void loadIntersectionMesh(){    	
    	MeshPart mpart = pieceModelInstance.model.meshParts.get(0);
    	Mesh mesh = mpart.mesh;
    	
    	vertexSize = mesh.getVertexSize() / 4;
        vertices = new float[mesh.getNumVertices() * mesh.getVertexSize() / 4];
        mesh.transform(pieceModelInstance.transform.cpy());
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
        r.mul(pieceModelInstance.transform.cpy().inv());
        if(Intersector.intersectRayTriangles(r, vertices, indices, vertexSize, point)) {
            point.mul(pieceModelInstance.transform);
            return true;
        }
        return false;
    }	
	
	private Model buildPiece(VVoronoiShapeGenerator.PieceShape shape, Vector2 scale){
		
		MeshBuilder meshBuilder = new MeshBuilder();
				
		short idx = 0;
		
		float pieceHeight = 1;

		//Top face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);		
		meshBuilder.part("pieceTop", GL30.GL_TRIANGLE_FAN);		
		for(int i=0;i<shape.shape.size; i++){
			
			Vector2 p = shape.shape.get(i);			
			Vector2 pv = p.cpy().sub(shape.position).scl(scale);
			idx = meshBuilder.vertex(new Vector3(pv.x, pieceHeight, pv.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);
		}
		Mesh mesh1 = meshBuilder.end();

		//Edge face
		meshBuilder.begin(Usage.Position | Usage.TextureCoordinates);		
		meshBuilder.part("pieceEdge", GL30.GL_TRIANGLE_STRIP);		
		for(int n=0; n<=shape.shape.size; n++){
			
			int i = n%shape.shape.size;
			Vector2 p = shape.shape.get(i);			
			Vector2 pv = p.cpy().sub(shape.position).scl(scale);
			idx = meshBuilder.vertex(new Vector3(pv.x, pieceHeight, pv.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);			
			idx = meshBuilder.vertex(new Vector3(pv.x, 0, pv.y), new Vector3(0,1,0), new Color(), new Vector2(p.x, p.y));
			meshBuilder.index(idx);			
		}
		Mesh mesh2 = meshBuilder.end();
		
		ModelBuilder modelBuilder = new ModelBuilder();
	    modelBuilder.begin();

	    modelBuilder.part("pieceTop",
	            mesh1,
	            GL30.GL_TRIANGLE_FAN,
	            new Material(ColorAttribute.createDiffuse(Color.WHITE)));
	    		//new Material(ColorAttribute.createDiffuse(new Color((int)(Math.random() * 16777215)))));
	    
	    modelBuilder.part("pieceEdge",
	            mesh2,
	            GL30.GL_TRIANGLE_STRIP,
	            new Material(ColorAttribute.createDiffuse(Color.GRAY)));
	    		//new Material(ColorAttribute.createDiffuse(new Color((int)(Math.random() * 16777215)))));
	    
	    Model model = modelBuilder.end();   
	    return model;
	}    
	
}
