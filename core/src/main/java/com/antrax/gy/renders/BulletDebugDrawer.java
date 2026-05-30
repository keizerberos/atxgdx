package com.antrax.gy.renders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

public class BulletDebugDrawer extends btIDebugDraw {
    private ShapeRenderer shapeRenderer;
    private int debugMode = DebugDrawModes.DBG_DrawWireframe;

    public BulletDebugDrawer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void begin(Camera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    }

    public void end() {
        shapeRenderer.end();
    }

    @Override
    public void drawLine(Vector3 from, Vector3 to, Vector3 color) {
        shapeRenderer.setColor(color.x, color.y, color.z, 1f);
        shapeRenderer.line(from, to);
    }

    @Override
    public void setDebugMode(int debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public int getDebugMode() {
        return debugMode;
    }

    // Métodos obligatorios requeridos por la interfaz nativa de Bullet
    @Override public void drawContactPoint(Vector3 PointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {}
    @Override public void reportErrorWarning(String warningString) {}
    @Override public void draw3dText(Vector3 location, String textString) {}

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}