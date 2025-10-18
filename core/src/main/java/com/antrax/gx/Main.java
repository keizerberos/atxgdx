package com.antrax.gx;

import com.antrax.gx.player.PlayerEntity;
import com.antrax.gx.scenes.SceneController;
import com.antrax.gx.setup.xShaders;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import net.mgsx.gltf.scene3d.utils.LightUtils;
import net.mgsx.gltf.scene3d.utils.MaterialConverter;
import net.mgsx.gltf.scene3d.utils.LightUtils.LightsInfo;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ModelBatch modelbatchBound;
    private Texture image;
    /*public SceneAsset assetRobot;
    public SceneAsset assetWorld;
    public Scene sceneRobot;
    public Scene sceneWorld;*/
    private SceneController sceneController;
    private SceneManager sceneManager;
    private PerspectiveCamera camera;
	private DirectionalLightEx light;
	private Cubemap diffuseCubemap;
	private Cubemap environmentCubemap;
	private Cubemap specularCubemap;
	private Texture brdfLUT;
	private PlayerEntity playerEntity;
	
	private xShaders xshaders;

	Texture img;
    @Override
    public void create() {
    	batch = new SpriteBatch();
		img = new Texture("cross.png");

		playerEntity = new PlayerEntity();
    	modelbatchBound = new ModelBatch();
    	sceneManager = new SceneManager();
    	createSceneManager(sceneManager);
    	//sceneManager = new SceneManager(new DefaultShaderProvider(), new DepthShaderProvider());
		configCamera(sceneManager);
    	sceneController = new SceneController(camera);
    	sceneController.enableCollision();
    	sceneController.setMainPlayer(playerEntity);
    	sceneController.loadAssets(sceneManager);    	
    	xshaders = new xShaders();
		configLights(sceneManager);
		configCubeMaps();
		configEnvironment(sceneManager,sceneController.box);
		xshaders.setScene (sceneManager,sceneController.assetWorld,sceneController);
		xshaders.setOutline(true);
		//xshaders.add(sceneController.sceneRobot);
		xshaders.setMiRendereables(sceneController.getMiRendereables());
		xshaders.create();
		convertMaterials(sceneController.getRendereables());
		sceneManager.setCamera(camera);
    }

    @Override
    public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        camera.update();
        sceneController.update(deltaTime);
		Gdx.gl.glClearColor(0.13f, 0.16f, 0.13f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		sceneManager.update(deltaTime);
		//sceneRobot.modelInstance.transform.rotateRad(0,1f, 0,0.01f);
		xshaders.preRender();
		sceneManager.getRenderableProviders().clear();
		sceneManager.getRenderableProviders().addAll(sceneController.getRendereables());		
		
		sceneManager.render();
		xshaders.render();
		
		renderBounding();
		batch.begin();
		batch.draw(img, Gdx.graphics.getWidth()/2-24, Gdx.graphics.getHeight()/2-24);
		batch.end();	
    }
    public void createSceneManager(SceneManager sceneManager) {
    	DirectionalLightEx defaultLight = new DirectionalLightEx();
    	defaultLight.direction.set(-.5f,-.5f,-.7f).nor();
		defaultLight.color.set(Color.WHITE);
		if(defaultLight instanceof DirectionalLightEx){
			DirectionalLightEx light = (DirectionalLightEx)defaultLight;
			light.intensity = 0.00001f;
			light.updateColor();
		}
		sceneManager.environment.add(defaultLight);
    	
    }
    public void renderBounding() {

		modelbatchBound.begin(camera);			
		//modelbatch_main.render(modelInstance_person,env_main);
		//modelbatch_main.render(modelInstance_line,env_main);
		//modelbatch_main.render(modelInstance_person);
		//escena3D.renderBounding(modelbatch_bound);
		sceneController.renderBounding(modelbatchBound);
		modelbatchBound.end();
    }
    public void configEnvironment(SceneManager sceneManager,Scene sceneWorld) {
    	sceneManager.setAmbientLight(0.0001f); //0.4f
		sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
		sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
		sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
		sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f,0.4f ));
		sceneManager.environment.set(new FogAttribute(FogAttribute.FogEquation));

		sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0.1f));
		FogAttribute fogEquation = sceneManager.environment.get(FogAttribute.class, FogAttribute.FogEquation);
		if(fogEquation != null){
			fogEquation.value.set(
				MathUtils.lerp(sceneManager.camera.near, sceneManager.camera.far, (10 + 1f) / 2f),
				MathUtils.lerp(sceneManager.camera.near, sceneManager.camera.far, (10 + 1f) / 2f),
				10f * (10 + 1f) / 2f
				);
		}
		SceneSkybox skybox = new SceneSkybox(environmentCubemap);
		BoundingBox box = new BoundingBox();

		BoundingBox sceneBox = new BoundingBox();
		sceneWorld.modelInstance.calculateBoundingBox(box);
		if(box.isValid()){
			sceneBox.set(box);
		}

		//sceneManager.setSkyBox(skybox);
		DirectionalLight oldLight = sceneManager.getFirstDirectionalLight();

		sceneManager.environment.add(oldLight);
		if(oldLight != null && !(oldLight instanceof DirectionalShadowLight)){
			//DirectionalLight newLight = new DirectionalShadowLight(4096*2,4096*2).setBounds(sceneBox).set(oldLight);
			//DirectionalLight newLight = new DirectionalShadowLight(4096/16,4096/16).setBounds(sceneBox).set(oldLight);
			DirectionalLight newLight = new DirectionalShadowLight().setBounds(sceneBox).set(oldLight);
			//net.mgsx.gltf.scene3d.lights.
			sceneManager.environment.remove(oldLight);
			sceneManager.environment.add(newLight);
			//if(oldLight == defaultLight) defaultLight = newLight;
		}
    }
    private void convertMaterials(Array<Scene> rendereables) {
		for (Scene scene :  rendereables) {
			MaterialConverter.makeCompatible(scene);			
		}
    }
	private ShaderProvider createShaderProvider(int maxBones){
		LightsInfo info = LightUtils.getLightsInfo(new LightsInfo(), sceneManager.environment);
		Config config = new DefaultShader.Config();
		
		config.numBones = maxBones;
		config.numDirectionalLights = info.dirLights;
		config.numPointLights = info.pointLights;
		config.numSpotLights = info.spotLights;		
		return new DefaultShaderProvider(config);
	}
    public void configCubeMaps() {
		environmentCubemap = EnvironmentUtil.createCubemap (new InternalFileHandleResolver(), "textures/environment/environment_", ".png", EnvironmentUtil.FACE_NAMES_NEG_POS);
		diffuseCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), "textures/diffuse/diffuse_", "_0.jpg", EnvironmentUtil.FACE_NAMES_NEG_POS);
		specularCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(), "textures/specular/specular_", "_", ".jpg", 10, EnvironmentUtil.FACE_NAMES_NEG_POS);
		brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
    }
    public void configLights(SceneManager sceneManager) {
    	light = new DirectionalLightEx();
		light.direction.set(2, -2, 1).nor();
		light.color.set(Color.WHITE);
		sceneManager.environment.add(light);
    }
    public void configCamera(SceneManager sceneManager) {
    	camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float d = 0.2f;
		camera.near = d ;
		camera.far = 350;
		camera.position.set(-30f/3f, 45f/3f, 30f/3f);
		camera.direction.set(0.1f, -0.2f, -0.1f);	
		camera.rotate(10, 1f, 0f, 0f);
		sceneManager.setCamera(camera);
    }
    @Override
    public void dispose() {
    }
}
