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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class LifeInSpaceGame extends ApplicationAdapter {
    private Stage stage;

    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle buttonStyle;

    private Table mainTable;
    private Label chargeLabel;

    private Table buildTable;

    private AssetManager assetManager;
    private String spriteAtlasFileName = "sprites.atlas";
    private TextureAtlas spriteAtlas;
    private StateManager stateManager;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.load(spriteAtlasFileName, TextureAtlas.class);
        assetManager.finishLoading();

        spriteAtlas = assetManager.get(spriteAtlasFileName);

        stateManager = new StateManager();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        //mainTable.setDebug(true);
        stage.addActor(mainTable);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.GOLD;
        chargeLabel = new Label("Charge = 0", labelStyle);
        mainTable.add(chargeLabel);
        mainTable.row();

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.DARK_GRAY;
        buttonStyle.up = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
        buttonStyle.down = new NinePatchDrawable(spriteAtlas.createPatch("button_pressed"));
        buttonStyle.disabled = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));

        TextButton chargeButton = new TextButton("Charge", buttonStyle);
        chargeButton.pad(10);
        chargeButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stateManager.addCharge(1);
                chargeLabel.setText("Charge = " + stateManager.getCharge());
                return false;
            }
        });

        mainTable.add(chargeButton);
        mainTable.row();

        final TextButton buildButton = new TextButton("Build", buttonStyle);
        buildButton.pad(10);
        buildButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainTable.setVisible(false);
                buildTable.setVisible(true);
                return false;
            }
        });
        mainTable.add(buildButton);

        buildTable = new Table();
        buildTable.setFillParent(true);
        buildTable.setVisible(false);
        stage.addActor(buildTable);

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = new BitmapFont();
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.fontColorSelected = Color.YELLOW;
        listStyle.selection =  new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
        List<String> buildList = new List<String>(listStyle);
        buildList.setFillParent(true);
        buildList.setItems("beep", "boop", "farts");
        ScrollPane scrollPane = new ScrollPane(buildList);
        buildTable.add(scrollPane);

        buildTable.row();

        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.pad(10);
        backButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainTable.setVisible(true);
                buildTable.setVisible(false);
                return false;
            }
        });
        buildTable.add(backButton);
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
        spriteAtlas.dispose();
    }
}
