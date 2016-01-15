package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.brokenshotgun.lifeinspace.screens.MainControlScreen;

public class LifeInSpaceGame extends Game {
    private StateManager stateManager;
    private AssetManager assetManager;

    @Override
    public void create() {
        stateManager = new StateManager();
        assetManager = new AssetManager();
        setScreen(new MainControlScreen(this));
        //setScreen(new GameOverScreen(this));
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
