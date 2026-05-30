package com.antrax.gy;

import com.antrax.gy.screens.SandboxScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class AntraxGame extends Game {
    public AssetManager assetManager; 
    
    @Override
    public void create() {
        assetManager = new AssetManager();
        setScreen(new SandboxScreen(this));
    }
}