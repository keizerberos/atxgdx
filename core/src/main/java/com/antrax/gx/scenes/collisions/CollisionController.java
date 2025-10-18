package com.antrax.gx.scenes.collisions;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;


public class CollisionController {
	

	public btGhostPairCallback ghostPairCallback;
	public btPairCachingGhostObject ghostObject;
	public btConvexShape ghostShape;
	public btKinematicCharacterController characterController;
	public Matrix4 characterTransform;
	public Vector3 characterDirection = new Vector3();
	public Vector3 characterDirection2 = new Vector3();
	public Vector3 walkDirection = new Vector3();

	public ModelBuilder modelBuilder = new ModelBuilder();
	public BulletWorld world;
	
	public  btCollisionConfiguration collisionConfiguration;
	public  btCollisionDispatcher dispatcher;
	public  btBroadphaseInterface broadphase;
	public  btConstraintSolver solver;
	public  btCollisionWorld collisionWorld;
	public  PerformanceCounter performanceCounter;
	public  Vector3 gravity;
	

	Array<ModelInstance> colInstances = new Array<ModelInstance>();
	BulletEntity character;
	
	public CollisionController() {
		createWorld();
	}
	
	 BulletWorld createBulletWorld ()  {
		Bullet.init();
		//return new BulletWorld();
		// We create the world using an axis sweep broadphase for this test
		/*btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
		btAxisSweep3 sweep = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		btDiscreteDynamicsWorld collisionWorld = new btDiscreteDynamicsWorld(dispatcher, sweep, solver, collisionConfiguration);
		ghostPairCallback = new btGhostPairCallback();
		sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
		this.collisionConfiguration = collisionConfiguration;
		this.dispatcher = dispatcher;
		this.broadphase = sweep;
		this.solver = solver;
		this.collisionWorld = collisionWorld;
		this.gravity = new Vector3(0, -10, 0);
		if (collisionWorld instanceof btDynamicsWorld) ((btDynamicsWorld)this.collisionWorld).setGravity(this.gravity);
		 */
		btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
		btAxisSweep3 sweep = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		btDiscreteDynamicsWorld collisionWorld = new btDiscreteDynamicsWorld(dispatcher, sweep, solver, collisionConfiguration);
		ghostPairCallback = new btGhostPairCallback();
		sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
		return new BulletWorld (collisionConfiguration, dispatcher, sweep, solver, collisionWorld);

	}
	void createWorld(){
		world = createBulletWorld();
		world.setDebugMode(1);		
	}
	public void addCollisionableSoft(ModelInstance modelInstance) {
		BulletEntity bulletEntity = new BulletEntity(modelInstance , new btCollisionObject());
		world.add2(bulletEntity);		
	}
	private final static Vector3 tmpV = new Vector3();
	private final static Vector3 tmpI = new Vector3();
	public BulletEntity addCollisionableSoft2(ModelInstance modelInstance) {
		//new btRigidBodyConstructionInfo(mass, null, shape, localInertia);

		final BoundingBox boundingBox = new BoundingBox();
		modelInstance.model.calculateBoundingBox(boundingBox);
		//create(model, mass, boundingBox.getWidth(), boundingBox.getHeight(), boundingBox.getDepth());
		tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f);
		tmpI.set(tmpV);
		btRigidBody shape  = new btRigidBody(0.1f, null, new btBoxShape(tmpV),tmpV);
		
		//shape.calculateLocalInertia(1f, tmpV);
		//localInertia = tmpV;
		
		BulletEntity bulletEntity = new BulletEntity(modelInstance , shape);
		world.add2(bulletEntity);
		return bulletEntity;
	}
	public void addCollisionableWorld(ModelInstance modelInstance) {
		BulletEntity bulletEntity = new BulletEntity(modelInstance , new btRigidBody(new BulletConstructor(modelInstance.model , 0f,  new btBvhTriangleMeshShape(modelInstance.model.meshParts ) ).bodyInfo));
		world.add(bulletEntity);
	}
	
	
	
	public void createCharacter(BulletEntity bulletEntity){
		character = bulletEntity;
		characterTransform = character.transform; // Set by reference
		characterTransform.rotate(Vector3.X, 90);
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(characterTransform);
		ghostShape = new btCapsuleShape(2.5f, 2.5f);
		ghostObject.setCollisionShape(ghostShape);
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		characterController = new btKinematicCharacterController ( ghostObject, ghostShape, 0.3f, Vector3.Y);

		characterController.setJumpSpeed(5f);
		characterController.setFallSpeed(55f);
		characterController.setGravity(new Vector3(0f,-98f,0f));
		
		world.collisionWorld.addCollisionObject(ghostObject,
				(short)btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
				(short)(btBroadphaseProxy.CollisionFilterGroups.StaticFilter | btBroadphaseProxy.CollisionFilterGroups.DefaultFilter ));
		((btDiscreteDynamicsWorld)(world.collisionWorld)).addAction(characterController);

		ghostObject.getWorldTransform(characterTransform);
	}
	
	public void add (final BulletEntity entity) {
		if (entity.body != null) {
			if (entity.body instanceof btRigidBody)
				((btDiscreteDynamicsWorld)collisionWorld).addRigidBody((btRigidBody)entity.body);
			else
				collisionWorld.addCollisionObject(entity.body);
			// Store the index of the entity in the collision object.
		//	entity.body.setUserValue(entities.size - 1);
		}
	}
	public void update() {
		world.update();
	}
}
