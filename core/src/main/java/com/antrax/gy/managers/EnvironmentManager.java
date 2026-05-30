package com.antrax.gy.managers;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class EnvironmentManager {
    private final Environment environment;
    private DirectionalLight sunLight;

    public EnvironmentManager() {
        this.environment = new Environment();
        
        // 1. Luz Ambiental: Evita que las zonas en sombra queden completamente negras (RGB, Alpha)
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        
        // 2. Luz Direccional (Simula el Sol): Afecta a todo el mapa por igual
        sunLight = new DirectionalLight();
        sunLight.set(0.8f, 0.8f, 0.7f, -0.5f, -1.0f, -0.3f); // Color sutilmente cálido y dirección hacia abajo
        environment.add(sunLight);
    }

    /**
     * Permite agregar luces dinámicas en tiempo de ejecución (ej. explosiones, disparos)
     */
    public void addPointLight(float r, float g, float b, Vector3 position, float intensity) {
        PointLight pointLight = new PointLight();
        pointLight.set(r, g, b, position, intensity);
        environment.add(pointLight);
    }

    public Environment getEnvironment() {
        return environment;
    }
    
    public void updateSunDirection(float x, float y, float z) {
        sunLight.direction.set(x, y, z).nor();
    }
}