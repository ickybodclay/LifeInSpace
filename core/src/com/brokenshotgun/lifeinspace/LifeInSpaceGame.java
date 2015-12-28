package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class LifeInSpaceGame extends ApplicationAdapter {
    private Stage stage;
    private Table table;

    private AssetManager assetManager;
    private String spriteAtlasFileName = "sprites.atlas";
    private TextureAtlas spriteAtlas;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.load(spriteAtlasFileName, TextureAtlas.class);
        assetManager.finishLoading();

        spriteAtlas = assetManager.get(spriteAtlasFileName);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //table.setDebug(true);

        ImageTextButton.ImageTextButtonStyle style = new ImageTextButton.ImageTextButtonStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.BLUE;
        style.downFontColor = Color.DARK_GRAY;
        style.up = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
        style.down = new NinePatchDrawable(spriteAtlas.createPatch("button_pressed"));

        ImageTextButton testButton = new ImageTextButton("Test", style);
        testButton.pad(10);
        testButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("touchDown 1");
                return false;
            }
        });

        table.add(testButton);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
