package com.antrax.gx.setup;

import java.util.ArrayList;
import java.util.List;

import com.antrax.gx.Main;
import com.antrax.gx.scenes.SceneController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.LightUtils;
import net.mgsx.gltf.scene3d.utils.MaterialConverter;
import net.mgsx.gltf.scene3d.utils.LightUtils.LightsInfo;

enum ShaderMode {
	GOURAUD, // https://en.wikipedia.org/wiki/Gouraud_shading#Comparison_with_other_shading_techniques
	PHONG, // https://en.wikipedia.org/wiki/Phong_shading
	PBR_MR, 
	PBR_MRSG, 
	CeilShading
}

public class xShaders {

	private static final String TAG = "xShaders";	
	private ShaderMode shaderMode = ShaderMode.PBR_MR;
	boolean shadersValid = false;
	boolean outlineShaderValid = false;
	ShaderProgram outlineShader;
	SceneManager sceneManager;
	SceneController sceneController;
	SceneAsset sceneAsset;
	int maxBones = 0;
	Array<Scene> outlinerObjects;
	
	Array<ModelInstance> miRendereables;
	
	public xShaders(){
		outlinerObjects = new Array<Scene>();
		spriteBatch = new SpriteBatch();
	}
	public void setScene(SceneManager sceneManager, SceneAsset sceneAsset,SceneController sceneController) {
		this.sceneManager = sceneManager;
		this.sceneAsset = sceneAsset;
		this.sceneController = sceneController;
		for (SceneAsset asset : sceneController.getAssets()) {
			if(asset.maxBones>maxBones)
				maxBones = asset.maxBones;
		}
		
	}
	public void create(){
		validateShaders();
	}
	public void setOutline(boolean active){
		outlinesEnabled =active;
	}
	public void validateShaders() {
		if (sceneAsset != null) {
			if (!shadersValid) {
				shadersValid = true;
				System.out.println("shader is valid");
				sceneManager.setShaderProvider(createShaderProvider(shaderMode, maxBones));
				sceneManager.setDepthShaderProvider(PBRShaderProvider.createDefaultDepth(maxBones));

			}
		}
		if (!outlineShaderValid) {
			outlineShaderValid = true;
			if (outlineShader != null)
				outlineShader.dispose();
			if (outlinesEnabled) {// outliner
				String prefix = "";
				if (outlineDistFalloffOption) { // outlineDistFalloffOption
					prefix += "#define DISTANCE_FALLOFF\n";
				}
				outlineShader = new ShaderProgram(Gdx.files.classpath("shaders/outline.vs.glsl").readString(),
						prefix + Gdx.files.classpath("shaders/outline.fs.glsl").readString());
				if (!outlineShader.isCompiled())
					throw new GdxRuntimeException("Outline Shader failed: " + outlineShader.getLog());
				System.out.println("outliner is ok");
			}
		}
	}

	private ShaderProvider createShaderProvider(ShaderMode shaderMode, int maxBones) {

		// fit lights and bones to current scene.
		LightsInfo info = LightUtils.getLightsInfo(new LightsInfo(), sceneManager.environment);
		Gdx.app.log(TAG, "Reset shaders. Lights( dirs: " + info.dirLights + ", points: " + info.pointLights
				+ ", spots: " + info.spotLights + " )");

		switch (shaderMode) {
		default:
		case GOURAUD: {
			Config config = new DefaultShader.Config();
			config.numBones = maxBones;
			config.numDirectionalLights = info.dirLights;
			config.numPointLights = info.pointLights;
			config.numSpotLights = info.spotLights;
			config.defaultCullFace = GL20.GL_FRONT_AND_BACK;

			return new DefaultShaderProvider(config);
		}
//		case PHONG:
//			// TODO phong variant (pixel based lighting)
//		case PBR_MRSG:
//			// TODO SG shader variant
		case PBR_MR: {
			PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
			// config.manualSRGB = false;//ui.shaderSRGB.getSelected();
			// config.manualGammaCorrection = false;//ui.shaderGammaCorrection.isOn();
			config.numBones = maxBones;
			config.numDirectionalLights = info.dirLights;
			config.numPointLights = info.pointLights;
			config.numSpotLights = info.spotLights;
			config.defaultCullFace = 0;
			return PBRShaderProvider.createDefault(config);
		}
		case CeilShading: {
			PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
			config.vertexShader = Gdx.files.classpath("shaders/gltf-ceil-shading.vs.glsl").readString();
			config.fragmentShader = Gdx.files.classpath("shaders/gltf-ceil-shading.fs.glsl").readString();
			// config.manualSRGB = ui.shaderSRGB.getSelected();
			// config.manualGammaCorrection = ui.shaderGammaCorrection.isOn();
			config.numBones = maxBones;
			config.numDirectionalLights = info.dirLights;
			config.numPointLights = info.pointLights;
			config.numSpotLights = info.spotLights;
			return PBRShaderProvider.createDefault(config);
		}
		}
	}
	
	private boolean withFalloff = true;
	private boolean withOutliner = true;
	private SpriteBatch spriteBatch;
	private FrameBuffer depthFbo;
	private boolean outlinesEnabled = false;
/*	private float outlinesWidth = 0f;
	private Color outlineInnerColor = new Color(0,0,0,.3f);
	private Color outlineOuterColor = new Color(0,0,0,.7f);
	private float outlineDepthMin = 0.001f;
	private float outlineDepthMax = 0.9f;
*/
	private float outlinesWidth = 0.2f;
	private Color outlineInnerColor = new Color(0.9f,0.9f,0.9f,0.8f);
	//private Color outlineInnerColor = new Color(0,0f,0,0.8f);
	//private Color outlineInnerColor = new Color(1,1.0f,1,0.8f);
	private Color outlineOuterColor = new Color(0,0,0,.0f);
	//private float outlineDepthMin = 0.55f;
	private float outlineDepthMin = 0.65f;
	private float outlineDepthMax = 0.85f;
	
	private boolean outlineDistFalloffOption = false;
	private float outlineDistFalloff = 0.3f;
	
	protected void captureDepth() {
		depthFbo = ensureFBO(depthFbo, true);
		depthFbo.begin();
		Gdx.gl.glClearColor(1f, 1f, 1f, 0.0f);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		sceneManager.getRenderableProviders().clear();;
		//sceneManager.getRenderableProviders().add(main.sceneRobot);		
		sceneManager.getRenderableProviders().addAll(miRendereables);
		sceneManager.renderDepth();
		depthFbo.end();
	}
	private FrameBuffer ensureFBO(FrameBuffer fbo, boolean hasDepth) {
		int w = Gdx.graphics.getBackBufferWidth();
		int h = Gdx.graphics.getBackBufferHeight();
		if(fbo == null || fbo.getWidth() != w || fbo.getHeight() != h){
			if(fbo != null) fbo.dispose();
			fbo = new FrameBuffer(Format.RGBA8888, w, h, hasDepth);
		}
		return fbo;
	}
	public void preRender(){
		captureDepth();
	}

	public void render(){
		if(outlinesEnabled && outlineShader!=null){
			outlineShader.bind();
			float size = 1-outlinesWidth;
			
			// float depthMin = ui.outlineDepthMin.getValue() * .001f;
			//float depthMin=(float)Math.pow(outlineDepthMin, 14);;//float depthMin = (float)Math.pow(ui.outlineDepthMin.getValue(), 10); // 0.35f
			//float depthMax=(float)Math.pow(outlineDepthMax, 18);;//float depthMax = (float)Math.pow(ui.outlineDepthMax.getValue(), 10); // 0.9f
			float depthMin=(float)Math.pow(outlineDepthMin, 10);//float depthMin = (float)Math.pow(ui.outlineDepthMin.getValue(), 10); // 0.35f
			float depthMax=(float)Math.pow(outlineDepthMax, 10);;//float depthMax = (float)Math.pow(ui.outlineDepthMax.getValue(), 10); // 0.9f
			
			
			outlineShader.setUniformf("u_size", Gdx.graphics.getWidth() * size, Gdx.graphics.getHeight() * size);
			outlineShader.setUniformf("u_depth_min", depthMin);
			outlineShader.setUniformf("u_depth_max", depthMax);
			outlineShader.setUniformf("u_inner_color", outlineInnerColor);
			outlineShader.setUniformf("u_outer_color", outlineOuterColor);
			
			if(outlineDistFalloffOption){
				
				float distanceFalloff = outlineDistFalloff;
				if(distanceFalloff <= 0){
					distanceFalloff = .001f;
				}
				outlineShader.setUniformf("u_depthRange", sceneManager.camera.far / (sceneManager.camera.near * distanceFalloff));
			}
		
			spriteBatch.enableBlending();
			spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
			spriteBatch.setShader(outlineShader);
			spriteBatch.begin();
			spriteBatch.draw(depthFbo.getColorBufferTexture(), 0, 0, 1, 1, 0f, 0f, 1f, 1f);
			spriteBatch.end();
			spriteBatch.setShader(null);
		}		
	}
	
	public void add(Scene sceneObject) {
		outlinerObjects.add(sceneObject);
		//this.main = main;
		// TODO Auto-generated method stub
		
	}
	
	public void setMiRendereables(Array<ModelInstance> miRendereables) {
		this.miRendereables = miRendereables;
	}
}
