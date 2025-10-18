package com.antrax.gx.objects;

import java.util.UUID;

import com.antrax.gx.scenes.collisions.BulletEntity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

import net.mgsx.gltf.scene3d.scene.Scene;

//this.id = UUID.randomUUID();
public class GameObject {
	public UUID id;
	public String name;
	Node node;
	ModelInstance miInstance;
	//ModelInstance miInstanceNode; //may be usseless
	ModelInstance miBounds;
	Scene scene;
	BoundingBox boundingBox;
	AnimationController controller;
	BulletEntity bulletEntity;
	public boolean canGet = true;

	Vector3 center =new Vector3();
	Vector3 dimensions = new Vector3();
	Vector3 position = new Vector3();
	Vector3 dPosition = new Vector3();
	  
	boolean isRoot = false;
	boolean isScene = false;
	public GameObject(){
		boundingBox = new BoundingBox();
	}
	public void set(Node node , ModelInstance modelInstance){
		this.miInstance = modelInstance;
		//this.miBounds = modelInstance;		
		this.node = node;
		this.calcBounding();
		
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
		this.miInstance.calculateBoundingBox(boundingBox);
		if (boundingBox.getHeight() ==0) {
			this.miBounds = ModelBounding.crear(0.6f,Color.YELLOW);		
			this.miInstance = ModelBounding.crear(0.6f,Color.GREEN);	
		}else {
			this.miBounds = ModelBounding.cargar(miInstance);
		}
		this.miBounds = ModelBounding.cargar(miInstance);	
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
	Matrix4 transform = new Matrix4();
	public Vector3 getPositionBullet(){
		if (this.bulletEntity!=null) {
			this.bulletEntity.body.getWorldTransform(transform);
			transform.getTranslation(this.position);
		}
		
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
		if (this.node!=null) {
			this.node.globalTransform.getTranslation(tGlobal);
			this.node.localTransform.getTranslation(tLocal);
			miInstance.transform.setTranslation(tGlobal);
			miBounds.transform.setTranslation(tGlobal);
			if (helper1!=null) helper1.getModelInstance().transform.setTranslation(tLocal);
			if (helper2!=null) helper2.getBoundingMi().transform.setTranslation(tGlobal);
		}
		//Vector3 tLoc = new Vector3();
		//pos.sub(dPosition);
		/*
		this.miBounds = ModelBounding.cargar(miInstance);		
		this.miInstance.calculateBoundingBox(boundingBox);
		boundingBox.getDimensions(dimensions);
		boundingBox.getCenter(center);
		position.add(center);*/
	}
	GameObject helper1;
	GameObject helper2;
	public void addHelperMi(GameObject go) {		
		helper1 = go;
		if(node!=null){
			node.localTransform.getTranslation(tLocal);
			helper1.getModelInstance().transform.setTranslation(tLocal);
		}
	}
	public void addHelperMiBound(GameObject go) {
		helper2 = go;
		if(node!=null){
			node.localTransform.getTranslation(tGlobal);
			helper2.getBoundingMi().transform.setTranslation(tGlobal);
		}
	}
	Vector3 tLocal = new Vector3();
	Vector3 tGlobal = new Vector3();
	Vector3 tModel = new Vector3();
	Vector3 p1 = new Vector3();
	Matrix4 m1 = new Matrix4();
	int translates= 0;
	public void setTranslate1(Vector3 pos){
		pos.sub(dPosition);
		if(node!=null){
			node.globalTransform.getTranslation(tGlobal);
			node.localTransform.getTranslation(tLocal);
			miInstance.transform.getTranslation(tModel);
			if (helper1!=null) helper1.getModelInstance().transform.setTranslation(tLocal);
			if (helper2!=null) helper2.getBoundingMi().transform.setTranslation(tGlobal);
			//tGlobal.sub(pos);
			//node.localTransform.getTranslation(tLocal);
			if (translates<3) {
				System.out.println(translates + "\tglobal: "+tGlobal.x + "\t"+tGlobal.y + "\t"+tGlobal.z + "\t" );
				System.out.println(translates + "\tlocal: "+tLocal.x + "\t"+tLocal.y + "\t"+tLocal.z + "\t" );
				System.out.println(translates + "\ttModel: "+tModel.x + "\t"+tModel.y + "\t"+tModel.z + "\t" );
				System.out.println(translates + "\tpos: "+pos.x + "\t"+pos.y + "\t"+pos.z + "\t" );
				translates ++;
				
			}

			//miInstance.transform.setToTranslation(pos);

			pos.add( 0,boundingBox.getHeight(),0 );
			//tGlobal.add(pos);
			//node.globalTransform.setTranslation(pos);
	  	 	//node.localTransform.setToTranslation(tGlobal);
	  	 	node.globalTransform.setTranslation(pos);
			
			node.calculateBoundingBox(boundingBox);
			boundingBox.mul(node.globalTransform);
		}
		Vector3 tLoc = new Vector3();
		//pos.sub(dPosition);
		//pos.add( 0,-boundingBox.getHeight(),0 );
		miInstance.transform.setTranslation(pos);
		//miBounds.transform.setTranslation(pos);
		//bulletEntity.motionState = 
		//if (bulletEntity!=null) bulletEntity.transform.setTranslation(pos);
		if (isRoot) {
			miInstance.calculateBoundingBox(boundingBox);
			miInstance.transform.getTranslation(position); 
			boundingBox.mul(miInstance.transform);
			
		}
		if (shape!=null) {
			shape.setCenterOfMassTransform(miInstance.transform);
			//shape.setCenterOfMassTransform(miBounds.transform);
		}
	}
	public void setTranslate1_tmp(Vector3 pos){
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
		//bulletEntity.motionState = 
		if (bulletEntity!=null) bulletEntity.transform.setTranslation(pos);
		if (isRoot) {
			miInstance.calculateBoundingBox(boundingBox);
			miInstance.transform.getTranslation(position); 
			boundingBox.mul(miInstance.transform);
			
		}
		if (shape!=null) {
			shape.setCenterOfMassTransform(miBounds.transform);
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
	
	 class MyMotionState extends btMotionState {
	    Matrix4 transform;
	    Matrix4 transformBound;
	    Vector3 mmPos = new Vector3();
	    @Override
	    public void getWorldTransform (Matrix4 worldTrans) {
	    	
	    	worldTrans.set(miInstance.transform);
	    }
	    @Override
	    public void setWorldTransform (Matrix4 worldTrans) {
//	    	miInstance.transform.set(worldTrans);
	    	worldTrans.getTranslation(mmPos);
	    	
			miInstance.transform.set(worldTrans); 
			node.globalTransform.set(worldTrans);
			miInstance.calculateBoundingBox(boundingBox);
			boundingBox.mul(miInstance.transform);
			//Vector3 tLoc = new Vector3();
			//mmPos.sub(dPosition);
			//miInstance.transform.set(worldTrans);
			//miInstance.transform.setTranslation(mmPos);
			//miInstance.transform.setTranslation(mmPos);

			//node.globalTransform.setTranslation(mmPos);
	  	 	//node.localTransform.setTranslation(mmPos);

			////node.globalTransform.set(worldTrans);
			////node.globalTransform.translate( dPosition );
	    	//miBounds.transform.set(miInstance.transform);
			//node.globalTransform.setTranslation(mmPos);
			
			//node.localTransform.set(worldTrans);

		/*	node.calculateBoundingBox(boundingBox);

			//miInstance.calculateBoundingBox(boundingBox);
			miInstance.transform.getTranslation(mmPos); 
			boundingBox.mul(miInstance.transform);*/
			/*if (isRoot) {
				miInstance.calculateBoundingBox(boundingBox);
				miInstance.transform.getTranslation(position); 
				boundingBox.mul(miInstance.transform);
				
			}*/
			
	    	//System.out.println( mmPos.x + " " +mmPos.y + " " + mmPos.z  );
	    	//miInstance.transform.getTranslation(position);
	    }
	}
		MyMotionState motionState;	
		btRigidBody shape;	
	
	public void addBulletEntity(BulletEntity bulletEntity) {
		this.bulletEntity = bulletEntity;
		motionState = new MyMotionState();
		if (this.node!=null) {
			motionState.transform = node.globalTransform;
			shape = (btRigidBody) bulletEntity.body;
			shape.setMotionState(motionState);
		}else{
			motionState.transform = miInstance.transform;
			shape = (btRigidBody) bulletEntity.body;
			shape.setMotionState(motionState);
		}
		 //shape.activate();
		 /*shape.forceActivationState(ACTIVE_TAG);
		 shape.activate();
		 shape.setDeactivationTime(0);*/
	}
	public void desactive() {
		if (shape==null)return;
		System.out.println("desactive  " + shape.getCollisionFlags());
		shape.setDeactivationTime(10);
		//shape.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);		
	}

	public void active() {
		if (shape==null)return;
		System.out.println("sactive  " + shape.getCollisionFlags());
		shape.activate();
		//shape.setCollisionFlags(btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);		
	}
}
