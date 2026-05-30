package com.antrax.gy.scenes;

import com.antrax.gx.scenes.collisions.BaseEntity;
import com.antrax.gx.scenes.collisions.BaseWorld;

public class SceneController {
	private BaseWorld physicsWorld; // O CollisionWorld dependiendo de si es 2D o 3D
	//private EntityManager entityManager; // Maneja player, npc, objetos
	private BaseEntity player;

	public void update(float deltaTime) {
		// Paso fijo para físicas
		stepPhysics(deltaTime);
		// Actualizar posiciones de enemigos e IA
		//entityManager.update(deltaTime);
	}

	private void stepPhysics(float deltaTime) {
		// Lógica de acumulador de tiempo fijo (Fixed Timestep)
	}

	// Getters para que el RenderSystem pueda acceder a lo que necesita dibujar
	//public EntityManager getEntityManager() { return entityManager; }
	public BaseWorld getPhysicsWorld() { return physicsWorld; }
}
