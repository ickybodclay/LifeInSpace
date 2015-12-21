package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

public class LifeInSpaceGame extends ApplicationAdapter {
	private SceneLoader sceneLoader;
	private Viewport viewport;

	@Override
	public void create () {
        viewport = new FitViewport(25f, 18.75f);
		sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MainScene", viewport);

        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());

        Player player = new Player();
        root.getChild("player").addScript(player);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sceneLoader.engine.update(Gdx.graphics.getDeltaTime());
	}
}
