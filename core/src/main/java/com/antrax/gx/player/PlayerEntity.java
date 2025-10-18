package com.antrax.gx.player;

import com.antrax.gx.objects.GameObject;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;

public class PlayerEntity {
	Inventary inventary;
	String name;
	GameObject goObjectHand;
	GameObject goObjectFront;
	Vector3 collidePoint = new Vector3();;
	ModelInstance objectHand;
	Node objectHandNode;
	public PlayerEntity(){
		
	}
	public void setObjectHand(GameObject go){
		if (go == null)
			if (this.goObjectHand !=null) {
					this.goObjectHand.canGet = true;					
			}
		
		this.goObjectHand = go;
		if (go != null) 
			this.goObjectHand.canGet = false;
	}
	public void setObjectFront(GameObject go, Vector3 collidePoint){
		this.collidePoint.set(collidePoint);
		this.goObjectFront = go;
	}
	public Vector3 getCollidePoint(){
		return this.collidePoint;
	}
	/*public void setObjectHand(ModelInstance mi){
		this.objectHand = mi;
	}*/
	/*public void setObjectHand(Node node){
		this.objectHandNode = node;
	}*/
	public GameObject getObjectHand(){
		return this.goObjectHand ;
	}
	public GameObject getObjectFront(){
		return this.goObjectFront ;
	}
	public Node getObjectHandNode(){
		return this.objectHandNode ;
	}
	public void clearObjectHand(){
		this.objectHandNode = null;
		this.objectHand = null;
		this.goObjectHand = null;
	}
}
