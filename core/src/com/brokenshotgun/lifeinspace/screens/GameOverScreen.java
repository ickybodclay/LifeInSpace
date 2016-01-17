package com.brokenshotgun.lifeinspace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.brokenshotgun.lifeinspace.LifeInSpaceGame;

public class GameOverScreen implements Screen {
    private final LifeInSpaceGame game;

    private final String spriteAtlasFile = "sprites.atlas";
    private TextureAtlas spriteAtlas;

    private Stage stage;

    public GameOverScreen(LifeInSpaceGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.getAssetManager().load(spriteAtlasFile, TextureAtlas.class);
        game.getAssetManager().finishLoading();

        spriteAtlas = game.getAssetManager().get(spriteAtlasFile);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table ui = new Table();
        ui.setFillParent(true);
        stage.addActor(ui);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.RED;

        Label gameOverLabel = new Label(
                "Game Over\n" +
                        "Mars is full of water and you died of dehydration\n" +
                        "Oops", labelStyle);
        gameOverLabel.setAlignment(Align.center, Align.center);

        ui.add(gameOverLabel).center().expand().fill();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.YELLOW;
        textButtonStyle.downFontColor = Color.DARK_GRAY;
        textButtonStyle.up = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
        textButtonStyle.down = new NinePatchDrawable(spriteAtlas.createPatch("button_pressed"));
        textButtonStyle.disabled = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));

        TextButton restartButton = new TextButton("Restart", textButtonStyle);
        restartButton.pad(10f);
        restartButton.padTop(10f);
        restartButton.padBottom(20f);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getStateManager().clearSave();
                game.getStateManager().reset();
                game.setScreen(new MainControlScreen(game));
            }
        });

        ui.row();
        ui.add(restartButton).pad(10f).bottom();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
