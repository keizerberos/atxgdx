package com.antrax.gy.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

public class InteractionManager {
    private final btDiscreteDynamicsWorld physicsWorld;
    
    // Vectores de apoyo reutilizables para evitar recolector de basura (GC)
    private final Vector3 rayFrom = new Vector3();
    private final Vector3 rayTo = new Vector3();

    public InteractionManager(btDiscreteDynamicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    /**
     * Lanza un rayo desde la posici�n del mouse (Para selecci�n RTS)
     */
    public ModelInstance selectObjectFromScreen(Camera camera, int screenX, int screenY) {
        // Conseguimos el rayo matem�tico que cruza el lente de la c�mara
        Ray ray = camera.getPickRay(screenX, screenY);
        
        rayFrom.set(ray.origin);
        // Extendemos el rayo hacia adelante en el espacio 3D (ej: 150 metros)
        rayTo.set(ray.direction).scl(150f).add(rayFrom);

        return executeRaycast();
    }

    /**
     * Lanza un rayo desde el centro exacto de la c�mara (Para disparos Shooter / Interacciones)
     */
    public ModelInstance shootRayFromCenter(Camera camera) {
        rayFrom.set(camera.position);
        // El rayo sigue la direcci�n exacta de la mirada de la c�mara
        rayTo.set(camera.direction).scl(100f).add(rayFrom);

        return executeRaycast();
    }

    private ModelInstance executeRaycast() {
        // Callback nativo de Bullet para conseguir �nicamente el primer objeto impactado
        ClosestRayResultCallback callback = new ClosestRayResultCallback(rayFrom, rayTo);
        
        physicsWorld.rayTest(rayFrom, rayTo, callback);

        ModelInstance hitInstance = null;
        
        if (callback.hasHit()) {
            btCollisionObject hitObject = callback.getCollisionObject();
        	System.out.println("hit");
            if (hitObject != null && hitObject.userData != null) {
            	System.out.println("hit");
                // Recuperamos el ModelInstance que guardamos previamente en el ModelFactory
                hitInstance = (ModelInstance) hitObject.userData;
            }
        }

        callback.dispose(); // Es un objeto nativo, liberaci�n obligatoria
        return hitInstance;
    }
}