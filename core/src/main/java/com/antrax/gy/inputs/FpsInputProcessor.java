package com.antrax.gy.inputs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class FpsInputProcessor extends InputAdapter {
    private final PerspectiveCamera camera;
    private final Vector3 direction = new Vector3();
    private float speed = 10f;
    private float mouseSensitivity = 0.2f;

    private boolean moveForward, moveBackward, moveLeft, moveRight;

    public FpsInputProcessor(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public void update(float deltaTime) {
        direction.set(0, 0, 0);

        // Obtener vectores de dirección adelante y lateral de la cámara
        Vector3 forward = camera.direction.cpy().set(camera.direction.x, 0, camera.direction.z).nor();
        Vector3 right = camera.direction.cpy().crs(camera.up).set(camera.direction.x, 0, camera.direction.z).nor();

        if (moveForward) direction.add(forward);
        if (moveBackward) direction.sub(forward);
        if (moveLeft) direction.sub(right);
        if (moveRight) direction.add(right);

        direction.nor().scl(speed * deltaTime);
        camera.position.add(direction);
        camera.update();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Rotación de la mirada con el movimiento libre del mouse
        float deltaX = -com.badlogic.gdx.Gdx.input.getDeltaX() * mouseSensitivity;
        float deltaY = -com.badlogic.gdx.Gdx.input.getDeltaY() * mouseSensitivity;

        camera.direction.rotate(Vector3.Y, deltaX);
        
        // Evitar que la cámara se voltee completamente boca abajo (clamping)
        Vector3 right = camera.direction.cpy().crs(camera.up).nor();
        camera.direction.rotate(right, deltaY);

        camera.update();
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W) moveForward = true;
        if (keycode == Input.Keys.S) moveBackward = true;
        if (keycode == Input.Keys.A) moveLeft = true;
        if (keycode == Input.Keys.D) moveRight = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W) moveForward = false;
        if (keycode == Input.Keys.S) moveBackward = false;
        if (keycode == Input.Keys.A) moveLeft = false;
        if (keycode == Input.Keys.D) moveRight = false;
        return false;
    }
}