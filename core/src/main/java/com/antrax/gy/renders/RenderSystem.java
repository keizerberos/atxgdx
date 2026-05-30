package com.antrax.gy.renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;


public class RenderSystem {
	private ModelBatch modelBatch;
    private ModelBatch outlineBatch; 
    private Environment environment;
    private BulletDebugDrawer debugDrawer;

    public RenderSystem(btDiscreteDynamicsWorld physicsWorld) {
        modelBatch = new ModelBatch();
       // environment = new Environment();
    //    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        //Gdx.files.classpath("shaders/gltf-ceil-shading.vs.glsl").readString();
        outlineBatch = new ModelBatch(
        		Gdx.files.classpath("shaders/outline.vertex.glsl"),//Gdx.files.internal("shaders/outline.vertex.glsl"), 
        		Gdx.files.classpath("shaders/outline.fragment.glsl")//Gdx.files.internal("shaders/outline.fragment.glsl")
        );
        
        debugDrawer = new BulletDebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);
        physicsWorld.setDebugDrawer(debugDrawer);
    }

    public void render(Camera camera, Array<ModelInstance> instances, ModelInstance selectedInstance, btDiscreteDynamicsWorld physicsWorld, Environment environment) {
        // 1. Renderizado Estándar de todo el mapa y objetos
        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        // 2. Renderizado del Contorno de Selección (Outline)
        if (selectedInstance != null) {
            // Inversión de caras (Cull Face) técnica "Inverted Hull"
            Gdx.gl.glEnable(GL20.GL_CULL_FACE);
            Gdx.gl.glCullFace(GL20.GL_FRONT); // Dibuja solo la parte trasera expandida

            // Dibujamos el objeto seleccionado usando el batch dedicado
            outlineBatch.begin(camera);
            
            // Pasamos los parámetros de color y grosor directamente antes del renderizado si tu shader lo requiere, 
            // aunque el constructor automático de ModelBatch compila los uniforms estándar (u_projViewTrans, u_worldTrans).
            // Nota: Si tu archivo outline.vertex.glsl usa nombres personalizados no integrados de LibGDX,
            // puedes inyectarlos aquí obteniendo el shader activo del lote:
            // outlineBatch.getShaderProvider().getShader(null).program.setUniformf("u_outlineWidth", 0.05f);

            outlineBatch.render(selectedInstance);
            outlineBatch.end();
            
            // Devolvemos el estado gráfico de OpenGL a la normalidad
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        }

        // 3. RenderBounding de Bullet (Líneas de colisión en tiempo real)
        debugDrawer.begin(camera);
        physicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    public void dispose() {
        modelBatch.dispose();
        outlineBatch.dispose(); 
        debugDrawer.dispose();
    }
}