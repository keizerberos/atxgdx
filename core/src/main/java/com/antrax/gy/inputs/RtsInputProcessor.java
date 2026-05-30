package com.antrax.gy.inputs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class RtsInputProcessor extends InputAdapter {
    private final PerspectiveCamera camera;
    private final Vector3 moveDirection = new Vector3();
    private float speed = 20f;
    private float rotationSpeed = 0.5f;

    // Estados de las teclas
    private boolean moveForward, moveBackward, moveLeft, moveRight;

    public RtsInputProcessor(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public void update(float deltaTime) {
        moveDirection.set(0, 0, 0);

        // Movimiento relativo a la orientación horizontal de la cámara
        if (moveForward) moveDirection.z -= 1;
        if (moveBackward) moveDirection.z += 1;
        if (moveLeft) moveDirection.x -= 1;
        if (moveRight) moveDirection.x += 1;

        moveDirection.nor().scl(speed * deltaTime);
        
        // Aplicar movimiento manteniendo la altura (Y) fija estilo RTS
        camera.position.add(moveDirection.x, 0, moveDirection.z);
        camera.update();
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

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom con la rueda del mouse
        camera.position.add(0, amountY * 2f, 0);
        camera.update();
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Rotar cámara aérea arrastrando con el botón derecho
        if (com.badlogic.gdx.Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float deltaX = -com.badlogic.gdx.Gdx.input.getDeltaX() * rotationSpeed;
            camera.rotateAround(camera.position, Vector3.Y, deltaX);
            camera.update();
            return true;
        }
        return false;
    }
}