package com.antrax.gx.scenes;

import java.util.HashMap;
import java.util.UUID;

import com.antrax.gx.objects.GameObject;
import com.antrax.gx.objects.ModelBounding;
import com.antrax.gx.player.PlayerController;
import com.antrax.gx.player.PlayerEntity;
import com.antrax.gx.scenes.collisions.BulletEntity;
import com.antrax.gx.scenes.collisions.CollisionController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class SceneController {

	public SceneAsset assetPlayer;
	public SceneAsset assetRobot;
    public SceneAsset assetWorld;
    public SceneAsset assetWorld_cb;
    public Scene scenePlayer;
    public Scene sceneWorld;
    public Scene sceneWorld_cb;
    public Scene sceneRobot;
    public Scene box;
    public CollisionController collisionController;
    public PlayerController playerController;
	Array<Scene> renderObjects = new Array<Scene>();
	Array<SceneAsset> sceneAssets = new Array<SceneAsset>();
	Array<ModelInstance> outlinerMiObjects = new Array<ModelInstance>();
	private Array<ModelInstance> modelInstanceBounding = new Array<ModelInstance>();
	HashMap<String, ModelInstance> boundingObjects = new HashMap<String, ModelInstance>();
	HashMap<String, ModelInstance> boundingObjectsMi = new HashMap<String, ModelInstance>();
	HashMap<String, Node> boundingObjectsNode = new HashMap<String, Node>();

	HashMap<UUID, GameObject> gameObjects = new HashMap<UUID, GameObject>();
	HashMap<UUID, GameObject> gameObjectsSelectable = new HashMap<UUID, GameObject>();
	HashMap<String, GameObject> helpersGameObjects = new HashMap<String, GameObject>();
	
	private static final Vector3 rayFrom = new Vector3();
	private static final Vector3 rayTo = new Vector3();
	//private static final ClosestRayResultCallback callback = new ClosestRayResultCallback(rayFrom, rayTo);

	private Vector3 collidePoint = new Vector3();
	private Vector3 collidePointTest = new Vector3();
	Vector3 center =new Vector3();
	  Vector3 dimensions = new Vector3();
	float distance;
	//public btCollisionObject rayTest(btCollisionWorld collisionWorld, Ray ray) {
	
	/*helper*/
	public Array<SceneAsset> getAssets(){
		return sceneAssets;
	}
	public HashMap<String, ModelInstance> getBoundingObjcets(){
		return boundingObjects;
	}
	public ModelInstance rayTest(btCollisionWorld collisionWorld, Ray ray) {	    rayFrom.set(ray.origin);
	    // 50 meters max from the origin
	    rayTo.set(ray.direction).scl(50f).add(rayFrom);

	    // we reuse the ClosestRayResultCallback, thus we need to reset its
	    // values
	    /*callback.setCollisionObject(null);
	    callback.setClosestHitFraction(1f);
	    callback.getRayFromWorld(rayFrom);
	    callback.getRayToWorld(rayTo);

	    collisionWorld.rayTest(rayFrom, rayTo, callback);

	    if (callback.hasHit()) {
	        return callback.getCollisionObject();
	    }
*/
	    int result = -1;

		distance = -1;
	    for (int i = 0; i < getRendereables().size; ++i) {
			final ModelInstance instance = getRendereables().get(i).modelInstance;
			instance.transform.getTranslation(collidePoint);
			final  BoundingBox bounds = new BoundingBox();
			instance.calculateBoundingBox(bounds);
			bounds.getDimensions(dimensions);
			 float  radius = dimensions.len() / 2f;
			
			bounds.getCenter(center);
			collidePoint.add(center);
			float dist2 = ray.origin.dst2(collidePoint);
			if (distance >= 0f && dist2 > distance) continue;
			if (Intersector.intersectRaySphere(ray, collidePoint, radius, null)) {
				result = i;
				distance = dist2;
			}
		}
	    if (result>=0)
	    	return getRendereables().get(result).modelInstance;
	    return null;
	}
	public Node rayTestBounding(Ray ray, float MaxDistance) {
	    rayFrom.set(ray.origin);
	    // 50 meters max from the origin
	    rayTo.set(ray.direction).scl(50f).add(rayFrom);

	    //int result = -1;
	    String result = "";
		distance = -1;

		for (String key : boundingObjects.keySet()) { 
			final ModelInstance instance = boundingObjects.get(key) ;
			instance.transform.getTranslation(collidePoint);
			final  BoundingBox bounds = new BoundingBox();
			instance.calculateBoundingBox(bounds);
			bounds.getDimensions(dimensions);
			float  radius = dimensions.len() / 2f;
			
			bounds.getCenter(center);
			collidePoint.add(center);
			float dist2 = ray.origin.dst2(collidePoint);
			if(dist2 >MaxDistance) continue;
			if (distance >= 0f && dist2 > distance ) continue;
			if (Intersector.intersectRayBounds(ray, bounds, collidePoint)) {
			//if (Intersector.intersectRaySphere(ray, position, radius, null)) {
				result = key;
				distance = dist2;
			}
		}
	    if (result!="")
	    	return boundingObjectsNode.get(result);
	    	//return boundingObjects.get(result);
	    
	    return null;
	}
	public GameObject rayTestGO(Ray ray, float MaxDistance) {
	    rayFrom.set(ray.origin);
	    rayTo.set(ray.direction).scl(150f).add(rayFrom);

	    UUID result = null;
		distance = -1;

		for (UUID key : gameObjectsSelectable.keySet()) { 
			final GameObject instance = gameObjects.get(key) ;
			if (!instance.canGet) continue;
			float dist2 = ray.origin.dst2(instance.getPosition());
			//if(dist2 > MaxDistance) continue;
			if (distance >= 0f && dist2 > distance ) continue;
			if (Intersector.intersectRayBounds(ray, instance.getBounding(), collidePointTest)) {
				collidePoint.set(collidePointTest);
				result = key;
				distance = dist2;
			}
		}
	    if (result!=null)
	    	return gameObjects.get(result);
	    	//return boundingObjects.get(result);
	    
	    return null;
	}
	Camera camera; 
	public SceneController(Camera camera) {
		this.camera = camera;
	}
	
	public void enableCollision() {
		collisionController = new CollisionController();
	}
    
    public void loadAssets(SceneManager sceneManager){
    	assetRobot = new GLTFLoader().load( Gdx.files.internal("models/rebox2.gltf"), false);        
    	sceneRobot = new Scene(assetRobot.scene);
    	sceneAssets.add(assetRobot);
    	
  	//	sceneRobot.modelInstance.transform.scale(400, 400, 400);
  		assetWorld = new GLTFLoader().load( Gdx.files.internal("models/scene_a.gltf"), false);  		
  		sceneWorld = new Scene(assetWorld.scene);
    	sceneAssets.add(assetWorld);
    	
  		assetPlayer = new GLTFLoader().load( Gdx.files.internal("models/player.gltf"), false);
  		scenePlayer= new Scene(assetPlayer.scene);
    	sceneAssets.add(assetPlayer);

  		assetWorld_cb = new GLTFLoader().load( Gdx.files.internal("models/scene_a.gltf"), false);
  		sceneWorld_cb = new Scene(assetWorld_cb.scene);
    	sceneAssets.add(assetWorld_cb);
  		
		scenePlayer.modelInstance. transform.setTranslation(0f, 10f, 0f);
  		
		box = sceneWorld;
    	renderObjects.add(sceneWorld);
    	renderObjects.add(sceneRobot);
    	
    	addToGameObject(sceneWorld);
    	
    	addBounding(sceneRobot,true);
    	addBounding(sceneWorld,false);
    	addBoxes();
    	setPlayerController(scenePlayer);
    	
    	if(collisionController!=null){
    		//collisionController.addCollisionableSoft2(sceneRobot.modelInstance);
    		//collisionController.addCollisionableSoft(scenePlayer.modelInstance);
    		collisionController.addCollisionableWorld(sceneWorld_cb.modelInstance);
    	}

    	loadRendereables(sceneManager, getRendereables());
		System.out.println("[SceneController] modelInstanceBounding.size():" + modelInstanceBounding.size );    	
    }

    public void loadRendereables(SceneManager sceneManager,Array<Scene> rendereables) {
    	for(Scene scene: rendereables) {
    		sceneManager.addScene(scene,true);    		
    	}
    	System.out.println("sceneManager.getTotalLightsCount():" + sceneManager.getTotalLightsCount());		
    }
    void setPlayerController(Scene scene){
    	if (collisionController==null) return;    
    	if (playerController ==null) playerController = new PlayerController(camera);
    	playerController.setWorldCollision(collisionController);
		playerController.setPlayerEntity(mainPlayerEntity);
		playerController.setHelperHandHold(helpersGameObjects.get("cameraHand"));
    	collisionController.createCharacter(new BulletEntity(scene.modelInstance , null));
    }
    void addBoxes() {
		ModelInstance box = ModelBounding.crear(0.2f);	
		GameObject gameObject = new GameObject();
		//gameObject.setModelInstance(mi);
		gameObject.setModelInstanceBound(box);
		gameObject.translate(0, 2f, 0);
		UUID key = UUID.randomUUID();
		gameObject.id = key;
		gameObjects.put(key, gameObject );
		helpersGameObjects.put("boxPointer", gameObject );

		box = ModelBounding.crear(0.5f);	
		gameObject = new GameObject();
		//gameObject.setModelInstance(mi);
		gameObject.setModelInstanceBound(box);
		gameObject.translate(0, 2f, 0);
		key = UUID.randomUUID();
		gameObject.id = key;
		gameObjects.put(key, gameObject );
		helpersGameObjects.put("cameraHand", gameObject );
    }
    GameObject createBox(String idKey,float size,Color color ) {
		ModelInstance box = ModelBounding.crear(size,color);	
		GameObject gameObject = new GameObject();
		//gameObject.setModelInstance(mi);
		gameObject.setModelInstanceBound(box);
		gameObject.translate(0, 2f, 0);
		UUID key = UUID.randomUUID();
		gameObject.id = key;
		gameObjects.put(key, gameObject );
		helpersGameObjects.put(idKey, gameObject );
		return gameObject;
    }
    BaseLight blight;
    Node lightNode;
	Vector3 lightTranslate = new Vector3();
	void addToGameObject(Scene scene) {
		//scene.lights
		for(Entry<Node, BaseLight> entry :  scene.lights){
			Node node = scene.modelInstance.getNode(entry.key.id, true);
			if(node != null){
				//lights.put(node, createLight(entry.value));
				ModelInstance mi = new ModelInstance(scene.modelInstance.model ,node.id);
				BoundingBox bb = new BoundingBox();
				//scene.modelInstance.calculateBoundingBox(bb);
				mi.calculateBoundingBox(bb);
				System.out.println("-- bb Depth:"+ bb.getDepth()+ " Height:"+ bb.getHeight() + " Width:" + bb.getWidth()); 
				GameObject gameObject = new GameObject();
				//gameObject.set(node, scene.modelInstance);
				gameObject.set(node, mi);
				UUID key = UUID.randomUUID();
				gameObject.id = key;
				gameObjects.put(key, gameObject );
				//gameObjects.put(key, gameObject );
				//gameObjectsSelectable.put(key, gameObject);
				gameObject.addHelperMi(createBox("h1"+key,0.2f,Color.CHARTREUSE));
				gameObject.addHelperMiBound(createBox("h2"+key,0.1f,Color.MAGENTA));
			}
		}
	}
    void addBounding(Scene scene,boolean firstValid) {
    	ModelInstance modelInstance = scene.modelInstance;
    	//BaseLight light = scene.getLight("Point.000");
    	for (Node key : scene.lights.keys() ) {
    		//System.out.println("scene.light.name:  " + key.id );
    		lightNode = key;
    		blight = scene.lights.get(key);    		
    		key.globalTransform.getTranslation(lightTranslate);
    		System.out.println(" transform:  " + lightTranslate.x + " " + lightTranslate.y + " " + lightTranslate.z);
    		System.out.println(" blight.color:  " + blight.color);
    	}

    	
    	/*if (light!=null)
    		System.out.println("scene.light.color: " + light.color);*/
    	if (firstValid) {
			GameObject gameObjectParent = new GameObject();
			UUID key1 = UUID.randomUUID();
			gameObjectParent.id = key1;
			
			Node nodex = modelInstance.nodes.get(0);
			gameObjectParent.name = nodex.id ;
			System.out.println("- modelInstance.nodes.size:" + modelInstance.nodes.size);
			ModelInstance mi = new ModelInstance(modelInstance.model ,nodex.id);
			/*nodex.detach();				
			nodex.inheritTransform = false;*/
			gameObjectParent.setNode(nodex);
			gameObjectParent.setModelInstanceMain(mi);
			gameObjects.put(key1, gameObjectParent );
			gameObjectsSelectable.put(key1, gameObjectParent);
			gameObjectParent.addHelperMi(createBox("h1"+key1,0.2f,Color.CHARTREUSE));
			gameObjectParent.addHelperMiBound(createBox("h2"+key1,0.1f,Color.MAGENTA));
			
			BulletEntity bulletEntity ;
			if(collisionController!=null) {
	    		bulletEntity = collisionController.addCollisionableSoft2(scene.modelInstance);
	    		gameObjectParent.addBulletEntity(bulletEntity);
			}
			/*for ( Node nodec : nodex.getChildren()) {
				addBounding(nodec,  modelInstance);
			}*/
    	}
    	else {
    		GameObject gameObjectParent = new GameObject();
			UUID key1 = UUID.randomUUID();
			gameObjectParent.id = key1;
			Node nodex = modelInstance.nodes.get(0);
			gameObjectParent.name = nodex.id ;
			System.out.println("- modelInstance.nodes.size:" + modelInstance.nodes.size);
			gameObjectParent.setNode(nodex);
			gameObjectParent.setModelInstance(scene);
			gameObjects.put(key1, gameObjectParent );
			//gameObjectsSelectable.put(key1, gameObjectParent);
			gameObjectParent.addHelperMi(createBox("h1"+key1,0.2f,Color.CHARTREUSE));
			gameObjectParent.addHelperMiBound(createBox("h2"+key1,0.1f,Color.MAGENTA));
    	}
		System.out.println("firstValid"+"\t"+firstValid+"\t"+modelInstance.nodes.size);
		for ( int i = firstValid?1:0; i< modelInstance.nodes.size;i++) {
			Node node = modelInstance.nodes.get(i);
		  	node.detach();
		  	node.inheritTransform=false;
			String id = node.id;
			String id_data[] = id.split("_");	
			System.out.println("-- node.id:" + node.id);
			ModelInstance mi = new ModelInstance(modelInstance.model ,node.id);
		
			GameObject gameObject = new GameObject();

			gameObject.name = node.id ;
			//gameObject.setModelInstance(mi);
			gameObject.setNode(node);
			gameObject.setModelInstance(mi);
			UUID key = UUID.randomUUID();
			gameObject.id = key;
			gameObjects.put(key, gameObject );
			gameObjectsSelectable.put(key, gameObject);
			gameObject.addHelperMi(createBox("h1"+key,0.2f,Color.CHARTREUSE));
			gameObject.addHelperMiBound(createBox("h2"+key,0.1f,Color.MAGENTA));

			System.out.println("node.getChildCount():" + node.getChildCount()  );
			for ( Node nodec : node.getChildren()) {
				addBounding(nodec,  modelInstance);
			}
		}
		System.out.println("- ended:" + modelInstance.nodes.size);
    } 
    void addBounding(ModelInstance modelInstance) {
    	
    	for ( int i = 0; i< modelInstance.nodes.size;i++) {
			Node node = modelInstance.nodes.get(i);
		  	node.detach();
		  	node.inheritTransform=false;
			String id = node.id;
			String id_data[] = id.split("_");	
			System.out.println("---- node.id:" + node.id);
			node.detach();				
			node.inheritTransform = false;
			ModelInstance mi = new ModelInstance(modelInstance.model,node.id);
		
			GameObject gameObject = new GameObject();
			
			//gameObject.setModelInstance(mi);
			gameObject.setNode(node);
			gameObject.setModelInstance(mi);
			UUID key = UUID.randomUUID();
			gameObject.id = key;
			gameObjects.put(key, gameObject );
			gameObjectsSelectable.put(key, gameObject);
			gameObject.addHelperMi(createBox("h1"+key,0.2f,Color.CHARTREUSE));
			gameObject.addHelperMiBound(createBox("h2"+key,0.1f,Color.MAGENTA));

			System.out.println("node.getChildCount():" + node.getChildCount());
			/*for ( Node nodec : node.getChildren()) {
				addBounding(nodec,mi);
			}*/
		}
    }

    void addBounding(Node node,ModelInstance modelInstance) {
    	
		  	//node.detach();
		  	//node.inheritTransform=false;
			String id = node.id;
			System.out.println("sub -- node.id:" + node.id);
			//String id_data[] = id.split("_");	
			//node.detach();
			//node.inheritTransform = false;
			
			ModelInstance mi = new ModelInstance(modelInstance.model ,node.id);

			BoundingBox boundingBox = new BoundingBox();
			Vector3 v3 =new Vector3();
			node.calculateBoundingBox(boundingBox);
			
			boundingBox.getDimensions(v3);
			System.out.println("sub -- model :" + v3.x + " " + v3.y + " " + v3.z );
			GameObject gameObject = new GameObject();
			
			gameObject.setNode(node);
			gameObject.setModelInstance(mi);
			UUID key = UUID.randomUUID();
			gameObject.id = key;
			gameObjects.put(key, gameObject );
			gameObjectsSelectable.put(key, gameObject);
			gameObject.addHelperMi(createBox("h1"+key,0.09f,Color.CHARTREUSE));
			gameObject.addHelperMiBound(createBox("h2"+key,0.04f,Color.MAGENTA));

			System.out.println("sub -- node.getChildCount():" + node.getChildCount());
		/*	for ( Node nodec : node.getChildren()) {
				
				addBounding(nodec,mi);
			}*/
    }

    public Array<Scene> getRendereables() {
    	return renderObjects;
    }
    public Array<ModelInstance> getMiRendereables() {
    	return outlinerMiObjects;
    }

    public void detectObjectsGO(){
    	Ray pickRay = camera.getPickRay(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
    	GameObject gameObject= rayTestGO( pickRay , 5f);
        if (gameObject != null) {
        	//outlinerObjects.add(body)
        	//System.out.println("gameObjects.uuid:"+gameObject.id);
        	//System.out.println("gameObjects.position.x:"+ gameObject.getPosition().x );
        	//System.out.println("gameObjects.position.y:"+ gameObject.getPosition().y );
        	
        	outlinerMiObjects.clear();
        	outlinerMiObjects.add(gameObject.getModelInstance());
        	//if(!playerController.isHolding()) {
        		mainPlayerEntity.setObjectFront(gameObject,collidePoint);
        		//mainPlayerEntity.setObjectHand(gameObject);
        	//}
	    	helpersGameObjects.get("boxPointer").setTranslate(collidePoint);
        }else{


    		mainPlayerEntity.setObjectFront(null,collidePoint);
        	outlinerMiObjects.clear();
        	if(!playerController.isHolding()) 
        		mainPlayerEntity.clearObjectHand();
        }
    }
    
    public void update(float delta) {
    	if (playerController!=null) playerController.update();
    	detectObjectsGO();
		if (collisionController!=null) collisionController.update();
		updateObjects(delta);
		//scenePlayer.modelInstance.transform.rotateRad(0,1f, 0,0.01f);

		//lightNode.globalTransform.getTranslation(lightTranslate);
		//System.out.println(" transform:  " + lightTranslate.x + " " + lightTranslate.y + " " + lightTranslate.z);
    }
    int i = 0;
    
    private void updateObjects(float delta) {
		for (UUID key : gameObjects.keySet()) 
			gameObjects.get(key).update(delta);
    }
	public void renderBounding0(ModelBatch modelBatch) {
		//for (i= 0; i< modelInstanceBounding.size; i++)
		for (String key : boundingObjects.keySet()) 
			modelBatch.render(boundingObjects.get(key) );
			//modelBatch.render(modelInstanceBounding.get(i));
	}
	public void renderBounding(ModelBatch modelBatch) {
		//for (i= 0; i< modelInstanceBounding.size; i++)
		for (UUID key : gameObjects.keySet()) 
			modelBatch.render(gameObjects.get(key).getBoundingMi() );
			//modelBatch.render(modelInstanceBounding.get(i));
	}
	PlayerEntity mainPlayerEntity;
	public void setMainPlayer(PlayerEntity playerEntity) {
		mainPlayerEntity = playerEntity;		
	};
}
