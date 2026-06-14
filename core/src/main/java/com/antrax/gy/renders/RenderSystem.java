package com.antrax.gy.renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;


public class RenderSystem {
	private ModelBatch modelBatch;
    private Environment environment;
    private BulletDebugDrawer debugDrawer;
    private ShaderProgram outlineShaderProgram;

    public RenderSystem(btDiscreteDynamicsWorld physicsWorld) {
        //modelBatch = new ModelBatch();
       // environment = new Environment();
    //    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        //Gdx.files.classpath("shaders/gltf-ceil-shading.vs.glsl").readString();
     /*   outlineBatch = new ModelBatch(
        		Gdx.files.classpath("shaders/outline.vertex.glsl"),//Gdx.files.internal("shaders/outline.vertex.glsl"), 
        		Gdx.files.classpath("shaders/outline.fragment.glsl")//Gdx.files.internal("shaders/outline.fragment.glsl")
        );*/
        outlineShaderProgram = new ShaderProgram(
                Gdx.files.internal("shaders/outline.vertex.glsl"), 
                Gdx.files.internal("shaders/outline.fragment.glsl")
            );

        RenderContext renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1));

        // 3. Crear el proveedor de shaders heredando de DefaultShaderProvider
        DefaultShaderProvider shaderProvider = new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                // Si el objeto tiene nuestro atributo de selección, inyectamos el shader de contorno
                if (renderable.material.has(SelectionAttribute.Type)) {
                    return new com.badlogic.gdx.graphics.g3d.shaders.DefaultShader(
                        renderable, 
                        new com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config(), 
                        outlineShaderProgram
                    );
                }
                // Si no, el proveedor usa el comportamiento base (iluminación estándar para Blender)
                return super.createShader(renderable);
            }
        };

        // 4. 👇 Inicialización definitiva usando tu firma exacta de 3 argumentos
        //modelBatch = new ModelBatch(renderContext, shaderProvider, new DefaultRenderableSorter());
        modelBatch = new ModelBatch(shaderProvider);

        debugDrawer = new BulletDebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);
        physicsWorld.setDebugDrawer(debugDrawer);
        
    }

    private final BoundingBox boundsTmp = new BoundingBox();
    private final Vector3 minTmp = new Vector3();
    private final Vector3 maxTmp = new Vector3();
    public void render(Camera camera, Array<ModelInstance> instances, ModelInstance selectedInstance, btDiscreteDynamicsWorld physicsWorld, Environment environment) {
    	// 1. RENDERIZADO DEL MAPA NORMAL (Sin selección)
        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        // 2. RENDERIZADO DEL CONTORNO (Solo si hay un objeto seleccionado)
        if (selectedInstance != null) {
            // Aplicamos la etiqueta al material
            selectedInstance.materials.first().set(new SelectionAttribute(true));

            // Configuramos OpenGL para la técnica Inverted Hull
            Gdx.gl.glEnable(GL20.GL_CULL_FACE);
            Gdx.gl.glCullFace(GL20.GL_FRONT); // Dibujar caras invertidas

            // El shader requiere parámetros de grosor y color. Los inyectamos en caliente.
            outlineShaderProgram.bind();
            outlineShaderProgram.setUniformf("u_outlineWidth", 0.15f);
            outlineShaderProgram.setUniformf("u_outlineColor", 0.0f, 0.0f, 1.0f, 1.0f); // Amarillo (RGBA)

            // Dibujamos el objeto seleccionado con el mismo batch seguro
            modelBatch.begin(camera);
            modelBatch.render(selectedInstance);
            modelBatch.end();

            // Limpieza inmediata de los estados gráficos
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
            Gdx.gl.glCullFace(GL20.GL_BACK);
            selectedInstance.materials.first().remove(SelectionAttribute.Type);
            
        }

        // 3. RenderBounding de Bullet (L�neas de colisi�n en tiempo real)
        debugDrawer.begin(camera);
        physicsWorld.debugDrawWorld();
        debugDrawer.end();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        modelBatch.dispose();
        outlineShaderProgram.dispose();
        debugDrawer.dispose();
    }
}