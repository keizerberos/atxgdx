package com.antrax.gy.screens;

import com.antrax.gy.AntraxGame;
import com.antrax.gy.cameras.CameraManager;
import com.antrax.gy.luas.LuaController;
import com.antrax.gy.managers.EnvironmentManager;
import com.antrax.gy.managers.HudManager;
import com.antrax.gy.managers.InteractionManager;
import com.antrax.gy.objects.ModelFactory;
import com.antrax.gy.renders.RenderSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class SandboxScreen implements Screen {
    private final AntraxGame game;
    
    // Motor F�sico
    private btDefaultCollisionConfiguration collisionConfig;
    private btCollisionDispatcher dispatcher;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver solver;
    private btDiscreteDynamicsWorld physicsWorld;

    // Controladores Base
    private CameraManager cameraManager;
    private EnvironmentManager environmentManager;
    private InteractionManager interactionManager;
    private RenderSystem renderSystem;
    private LuaController luaController;
    
    // Contenedores de Objetos
    private Array<ModelInstance> visualInstances = new Array<>();
    private Array<btCollisionObject> triggerObjects = new Array<>();
    private ModelInstance selectedInstance = null;
    
    // Inputs
    private InputMultiplexer multiplexer;
    
    private HudManager hudManager;
    // Modelo crudo cargado (En producci�n usar�as AssetManager)
    private Model mapModel;

    private float playerHealth = 100f;
    private int teamResources = 150;

    public SandboxScreen(AntraxGame game) {
        this.game = game;
        initPhysics();
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        

        hudManager = new HudManager();
        cameraManager = new CameraManager(multiplexer, hudManager);
        environmentManager = new EnvironmentManager();
        interactionManager = new InteractionManager(physicsWorld);
        renderSystem = new RenderSystem(physicsWorld);
        luaController = new LuaController();
        
        // PROCESAR TU OBJETO DE BLENDER
        // Reemplaza "models/mi_mapa.g3db" por la ruta real de tu archivo
        //mapModel = new GLTFLoader().load(Gdx.files.internal("models/mi_mapa.g3db"));
        SceneAsset assetWorld = new GLTFLoader().load( Gdx.files.internal("models/taller5.gltf"), false);  		
  		Scene sceneWorld = new Scene(assetWorld.scene);
  		mapModel = sceneWorld.modelInstance.model; 
    	//sceneAssets.add(assetWorld);
        
        // Llamamos al importador autom�tico
        ModelFactory.importBlenderModel(mapModel, physicsWorld, visualInstances, triggerObjects);
    }

    private void initPhysics() {
    	Bullet.init();
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        physicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
        physicsWorld.setGravity(new Vector3(0, -9.81f, 0));
    }

    @Override
    public void render(float deltaTime) {
    	 cameraManager.updateControllers(deltaTime);

         // 2. Escuchar Eventos de Teclado Globales / Depuraci�n
         handleDebugAndInteractionInput();

         // 3. Paso de F�sicas
         physicsWorld.stepSimulation(deltaTime, 5, 1f / 60f);
         evaluateManualTriggers();

         hudManager.updateStats(playerHealth, teamResources);
         // 4. Dibujar Pantalla pasando el entorno de luces unificado
         Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
         Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
         
         renderSystem.render(
             cameraManager.getActiveCamera(), 
             visualInstances, 
             selectedInstance, 
             physicsWorld, 
             environmentManager.getEnvironment() // Enlazado con el EnvironmentManager
         );
         hudManager.draw();
    }

    private void handleDebugAndInteractionInput() {
        // Cambio de C�mara
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            cameraManager.toggleCamera();
        }
        
        // Recargar Lua en caliente
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            luaController.reloadScript();
        }

        // CONTROL DE INTERACCIONES Y SELECCI�N (Raycasting)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
        	
            // Evaluamos seg�n el estado actual configurado en el administrador de c�maras
            if (cameraManager.getActiveCamera() == cameraManager.rtsCamera) {
                // Modo RTS: Selecci�n mediante coordenadas del cursor
                Gdx.app.log("Shooter", "[RTS] InteractionManager hit on" );
                selectedInstance = interactionManager.selectObjectFromScreen(
                    cameraManager.rtsCamera, Gdx.input.getX(), Gdx.input.getY()
                );
                Gdx.app.log("Shooter", "[RTS] selectedInstance "+ selectedInstance==null?"nada":"encontrado" );
            } else {
                // Modo Shooter: Disparo/Interacci�n hacia el centro de la ret�cula
                ModelInstance hitObject = interactionManager.shootRayFromCenter(cameraManager.fpsCamera);
                Gdx.app.log("Shooter", "InteractionManager hit on" );
                if (hitObject != null) {
                    Gdx.app.log("Shooter", "�Impacto de hitscan confirmado en un objeto tridimensional!");
                    selectedInstance = hitObject; // Resaltar temporalmente el objeto golpeado
                }
            }
        }
    }

    private void evaluateManualTriggers() {
        for (btCollisionObject trigger : triggerObjects) {
            ContactResultCallback callback = new ContactResultCallback() {
                @Override
                public float addSingleResult(btManifoldPoint cp, 
                                             btCollisionObjectWrapper colObj0Wrap, int partId0, int index0, 
                                             btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
                    ModelInstance triggerInstance = (ModelInstance) trigger.userData;
                    if (triggerInstance != null) {
                        luaController.executeTriggerEvent("onTriggerEnter", triggerInstance);
                    }
                    return 0;
                }
            };
            physicsWorld.contactTest(trigger, callback);
            callback.dispose();
        }
    }


    @Override public void resize(int width, int height) { 
    	cameraManager.updateResize(width, height);
    	hudManager.resize(width, height); // ◄ Crucial para que las tablas no se deformen al estirar la ventana
    	}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        mapModel.dispose();
        renderSystem.dispose();
        physicsWorld.dispose();
        solver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        hudManager.dispose();
    }
}