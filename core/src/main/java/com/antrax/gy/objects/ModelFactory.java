package com.antrax.gy.objects;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Array;

public class ModelFactory {

    public static void importBlenderModel(Model model, 
                                          btDiscreteDynamicsWorld physicsWorld, 
                                          Array<ModelInstance> visualInstances, 
                                          Array<btCollisionObject> triggerObjects) {
        
        // 1. Crear la instancia visual principal
        ModelInstance visualInstance = new ModelInstance(model);
        
        // 2. Escanear nodos para configurar visibilidad y físicas
        for (Node node : model.nodes) {
            
            if (node.id.startsWith("collide_")) {
                // GRUPO 1: Colisión rígida estática (Paredes, suelo, obstáculos)
                // Apagamos el nodo visualmente en la instancia de renderizado
                Node visualNode = visualInstance.getNode(node.id);
                if (visualNode != null) visualNode.scale.set(0, 0, 0);
                
                // Generamos la forma física optimizada desde la malla de Blender
                btCollisionShape shape = Bullet.obtainStaticNodeShape(node,false);
                
                // Masa 0 = Objeto estático e inamovible
                btRigidBody.btRigidBodyConstructionInfo info = 
                    new btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero);
                btRigidBody body = new btRigidBody(info);
                
                // Posicionar el cuerpo usando la matriz de transformación de Blender
                body.setWorldTransform(node.globalTransform);
                
                // Guardamos la instancia visual en el userData del cuerpo físico 
                // Esto nos servirá para saber qué objeto seleccionamos con el Raycast
                body.userData=visualInstance;
                
                // Añadir al mundo físico (Grupo 1, colisiona con todo)
                physicsWorld.addRigidBody(body, (short)1, (short)-1);
                info.dispose();
                
            } else if (node.id.startsWith("trigger_")) {
                // GRUPO 2: Volúmenes de lógica (Zonas de recolección, alertas)
                // Apagamos el nodo visualmente
                Node visualNode = visualInstance.getNode(node.id);
                if (visualNode != null) visualNode.scale.set(0, 0, 0);
                
                btCollisionShape shape = Bullet.obtainStaticNodeShape(node,false);
                btCollisionObject trigger = new btCollisionObject();
                trigger.setCollisionShape(shape);
                trigger.setWorldTransform(node.globalTransform);
                
                // Configuración crítica de Trigger: Detecta colisión pero no empuja físicamente
                trigger.setCollisionFlags(trigger.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
                
                // Vinculamos la instancia para que Lua pueda interactuar con ella al activarse
                trigger.userData = visualInstance;
                
                physicsWorld.addCollisionObject(trigger);
                triggerObjects.add(trigger); // Lo guardamos para hacerle contactTest manual
                
            } else {
                // GRUPO 3: Render Puro
                // Si el nodo no tiene prefijos, es parte del decorado visual y se queda intacto
            }
        }
        
        // Añadimos el modelo final procesado a la lista de renderizado
        visualInstances.add(visualInstance);
    }
}