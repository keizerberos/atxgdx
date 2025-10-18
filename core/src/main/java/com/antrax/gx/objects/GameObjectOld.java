package com.antrax.gx.objects;

import java.util.UUID;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.gltf.scene3d.scene.Scene;

//this.id = UUID.randomUUID();
public class GameObjectOld {
	public UUID id;
	Node node;
	ModelInstance miInstance;
	//ModelInstance miInstanceNode; //may be usseless
	ModelInstance miBounds;
	Scene scene;
	BoundingBox boundingBox;
	AnimationController controller;
	public boolean canGet = true;

	Vector3 center =new Vector3();
	Vector3 dimensions = new Vector3();
	Vector3 position = new Vector3();
	Vector3 dPosition = new Vector3();
	  
	boolean isRoot = false;
	boolean isScene = false;
	public GameObjectOld(){
		boundingBox = new BoundingBox();
	}
	public void setModelInstance(ModelInstance modelInstance){
		this.miInstance = modelInstance;
	    this.controller = new AnimationController(modelInstance);
	    if (controller!=null && modelInstance.animations.size>0) {
	    	System.out.println("modelInstance.animations.items[0].id: " + modelInstance.animations.get(0).id );
	    	//controller.setAnimation(modelInstance.animations.get(0).id);
			this.controller.animate(modelInstance.animations.get(0).id, -1, 1f, null, 0.01f);			
	    }
		//head_move
		this.calcBounding();
	}
	public void setModelInstance(Scene scene){
		this.scene = scene;
		this.miInstance = scene.modelInstance;
	    this.controller = scene.animationController;
	    if (controller!=null ) {
	    	System.out.println("scene.animations.items[0].id: " + this.miInstance.animations.get(0).id );
	    	//controller.setAnimation(modelInstance.animations.get(0).id);
	    	scene.animationController.animate(this.miInstance.animations.get(0).id, -1, 1f, null, 0.1f);
			//scene.animationController.setAnimation(null);
	    	//scene.animations.playAll(true);
	    }
	    isScene = true;
		//head_move
		this.calcBounding();
	}
	public void setModelInstanceMain(ModelInstance modelInstance){
		this.miInstance = modelInstance;
	    this.controller = new AnimationController(modelInstance);
	    if (controller!=null && modelInstance.animations.size>0) {
	    	System.out.println("modelInstance.animations.items[0].id: " + modelInstance.animations.get(0).id );
	    	//controller.setAnimation(modelInstance.animations.get(0).id);
			this.controller.animate(modelInstance.animations.get(0).id, -1, 1f, null, 0.01f);
	    }
		//head_move
		this.calcBounding();
		isRoot = true;
	}
	public void setModelInstanceBound(ModelInstance modelInstance){
		this.miInstance = modelInstance;
		this.calcBoundingBound();
	}
	public void setNode(Node node){
		this.node = node;
		this.node.globalTransform.getTranslation(dPosition);		
	}
	public Node getNode(){
		return this.node;
	}
	/*void setModelInstanceNode(ModelInstance modelInstance){
		this.miInstanceNode = modelInstance;
		this.calcBounding();
	}	*/
	public void calcBoundingBound(){
		this.miBounds = miInstance;		
		this.miInstance.calculateBoundingBox(boundingBox);
		boundingBox.getDimensions(dimensions);
		boundingBox.getCenter(center);
		position.add(center);
	}
	public void calcBounding(){
		this.miBounds = ModelBounding.cargar(miInstance);		
		this.miInstance.calculateBoundingBox(boundingBox);
		boundingBox.getDimensions(dimensions);
		boundingBox.getCenter(center);
		position.add(center);
	}
	public void reCalcBounding(){	
		this.miBounds.calculateBoundingBox(boundingBox);
		boundingBox.getDimensions(dimensions);
		boundingBox.getCenter(center);
		this.miBounds.transform.getTranslation(position);
		position.add(center);
	}
	public ModelInstance getModelInstance(){
		return miInstance;
	}
	public Vector3 getPosition(){
		//reCalcBounding();
		return position;
	}
	public ModelInstance getBoundingMi(){
		return miBounds;
	}
	public BoundingBox getBounding(){
		return boundingBox;
	}
	public void update(float delta) {
		if (controller!=null) {
			if (isScene) {
				scene.animationController.update(delta);
			}
			else
				controller.update(delta);
		}		
	}
	GameObjectOld helper1;
	GameObjectOld helper2;
	public void addHelperMi(GameObjectOld go) {		
		helper1 = go;
		if(node!=null){
			node.localTransform.getTranslation(tLocal);
			helper1.getModelInstance().transform.setTranslation(tLocal);
		}
	}
	public void addHelperMiBound(GameObjectOld go) {
		helper2 = go;
		if(node!=null){
			node.localTransform.getTranslation(tGlobal);
			helper2.getBoundingMi().transform.setTranslation(tGlobal);
		}
	}
	Vector3 tLocal = new Vector3();
	Vector3 tGlobal = new Vector3();
	Vector3 p1 = new Vector3();
	Matrix4 m1 = new Matrix4();
	public void setTranslate1(Vector3 pos){
		if(node!=null){
			node.globalTransform.getTranslation(tGlobal);
			node.localTransform.getTranslation(tLocal);
			//tGlobal.add(pos);
			node.globalTransform.setTranslation(pos);
	  	 	node.localTransform.setTranslation(pos);
			if (helper1!=null) helper1.getModelInstance().transform.setTranslation(tLocal);
			if (helper2!=null) helper2.getBoundingMi().transform.setTranslation(tGlobal);

			node.calculateBoundingBox(boundingBox);
		}
		Vector3 tLoc = new Vector3();
		pos.sub(dPosition);
		miInstance.transform.setTranslation(pos);
		miBounds.transform.setTranslation(pos);
		if (isRoot) {
			miInstance.calculateBoundingBox(boundingBox);
			miInstance.transform.getTranslation(position); 
			boundingBox.mul(miInstance.transform);
		}
		//boundingBox.mul(node.localTransform);
	}
	public void setTranslate(Vector3 pos){
		miInstance.transform.setTranslation(pos);
		miBounds.transform.setTranslation(pos);
		miBounds.calculateBoundingBox(boundingBox);
		boundingBox.mul(miBounds.transform);
		
		Vector3 tLocal = new Vector3();
		Vector3 tGlobal = new Vector3();
		if(node!=null){
			node.localTransform.getTranslation(tLocal);
			node.globalTransform.getTranslation(tGlobal);
			//tGlobal.add(x,y,z);
	  	  	node.localTransform.setTranslation(tLocal);
	  	  	node.globalTransform.setTranslation(tGlobal);
		}
	}
	public void translate(float x, float y, float z){
		//position.add (x,y,z);
		
		//miInstance.transform.setToTranslation(position);
		miInstance.transform.translate(x,y,z);
		//miBounds.transform.translate(x,y,z);
		miBounds.transform.translate(x,y,z);
  	 // Vector3 pos = new Vector3();
  	  //mi.transform.getTranslation(pos);
  	  	//node.globalTransform.translate(x, y, z);
		miBounds.calculateBoundingBox(boundingBox);
		boundingBox.mul(miBounds.transform);
		
		Vector3 tLocal = new Vector3();
		Vector3 tGlobal = new Vector3();
		if(node!=null){
			node.localTransform.getTranslation(tLocal);
			node.globalTransform.getTranslation(tGlobal);
			tGlobal.add(x,y,z);
			tLocal.add(x,y,z);
	  	  	node.localTransform.setTranslation(tLocal);
	  	  	node.globalTransform.setTranslation(tGlobal);
			helper1.getModelInstance().transform.setTranslation(tLocal);
			helper2.getBoundingMi().transform.setTranslation(tGlobal);
			node.localTransform.getTranslation(tLocal);
			node.globalTransform.getTranslation(tGlobal);
			System.out.println("Local:" + tLocal.x + " " + tLocal.y + " " + tLocal.z);
			System.out.println("Global:" + tGlobal.x + " " + tGlobal.y + " " + tGlobal.z);
		}
		miInstance.transform.getTranslation(tLocal);
		miBounds.transform.getTranslation(tGlobal);
		//System.out.println("miInstance:" + tLocal.x + " " + tLocal.y + " " + tLocal.z);
		//System.out.println("miBounds:" + tGlobal.x + " " + tGlobal.y + " " + tGlobal.z);
	}
}
