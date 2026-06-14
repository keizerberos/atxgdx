package com.antrax.gy.cameras;

import com.antrax.gy.inputs.FpsInputProcessor;
import com.antrax.gy.inputs.RtsInputProcessor;
import com.antrax.gy.managers.HudManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class CameraManager {
    public PerspectiveCamera rtsCamera;
    public PerspectiveCamera fpsCamera;
    private HudManager hudManager; 
    private boolean isRtsMode = true;
    // Procesadores de entrada asociados
    private RtsInputProcessor rtsInput;
    private FpsInputProcessor fpsInput;
    private InputMultiplexer multiplexer;

    public CameraManager(InputMultiplexer multiplexer, HudManager hudManager) {
        this.multiplexer = multiplexer;
        this.hudManager = hudManager; // ◄ Guardamos la referencia
        // Configuraci�n de C�maras (C�digo previo)
        rtsCamera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        rtsCamera.position.set(0, 20, 15);
        rtsCamera.lookAt(0, 0, 0);
        rtsCamera.near = 0.1f;
        rtsCamera.far = 300f;
        rtsCamera.update();

        fpsCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fpsCamera.position.set(0, 2, 0);
        fpsCamera.near = 0.1f;
        fpsCamera.far = 200f;
        fpsCamera.update();

        // Inicializar procesadores
        rtsInput = new RtsInputProcessor(rtsCamera);
        fpsInput = new FpsInputProcessor(fpsCamera);

        // Registrar el modo inicial en el multiplexor del juego
        multiplexer.addProcessor(rtsInput);
    }

    public void toggleCamera() {
        isRtsMode = !isRtsMode;
        multiplexer.clear(); // Limpiamos los procesadores actuales

        if (isRtsMode) {
            Gdx.input.setCursorCatched(false); // Liberar mouse para clicks RTS
            multiplexer.addProcessor(rtsInput);
            hudManager.setRtsMode(); 
        } else {
            Gdx.input.setCursorCatched(true); // Capturar mouse para mirar en Shooter
            // Sincronizar posici�n inicial FPS en base a donde estaba la c�mara RTS
            fpsCamera.position.set(rtsCamera.position.x, 2f, rtsCamera.position.z);
            fpsCamera.update();
            multiplexer.addProcessor(fpsInput);
            hudManager.setShooterMode(); 
        }
        multiplexer.addProcessor(hudManager.getStage());
    }

    public void updateControllers(float deltaTime) {
        if (isRtsMode) {
            rtsInput.update(deltaTime);
        } else {
            fpsInput.update(deltaTime);
        }
    }

    public PerspectiveCamera getActiveCamera() {
        return isRtsMode ? rtsCamera : fpsCamera;
    }

    public void updateResize(int width, int height) {
        rtsCamera.viewportWidth = width;
        rtsCamera.viewportHeight = height;
        fpsCamera.viewportWidth = width;
        fpsCamera.viewportHeight = height;
    }
}