package com.antrax.gy.luas;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class LuaController {
    private Globals globals;

    public LuaController() {
        globals = JsePlatform.standardGlobals();
        // Cargar el script que manejará la lógica de pruebas
        reloadScript();
    }

    public void reloadScript() {
        try {
            globals.loadfile("scripts/sandbox_logic.lua").call();
        } catch (Exception e) {
            Gdx.app.log("LuaError", "Error al cargar script: " + e.getMessage());
        }
    }

    // Invocar funciones de Lua pasando objetos Java
    public void executeTriggerEvent(String eventName, ModelInstance objectInstance) {
        LuaValue func = globals.get(eventName);
        if (!func.isnil()) {
            // Pasamos la transformación (Coordenadas) del objeto 3D a Lua para que la altere
            func.call(CoerceJavaToLua.coerce(objectInstance.transform));
        }
    }
}