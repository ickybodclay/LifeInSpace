package com.brokenshotgun.lifeinspace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.brokenshotgun.lifeinspace.Effect;
import com.brokenshotgun.lifeinspace.LifeInSpaceGame;
import com.brokenshotgun.lifeinspace.StateManager;
import com.brokenshotgun.lifeinspace.StationComponent;
import com.brokenshotgun.lifeinspace.Widget;

public class MainControlScreen implements Screen {
    private final LifeInSpaceGame game;

    private Stage stage;

    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle buttonStyle;
    private List.ListStyle listStyle;
    private ScrollPane.ScrollPaneStyle scrollStyle;

    private Table mainTable;
    private Container[] mainGrid;
    private static final int TOP_LEFT = 0;
    private static final int TOP_MID = 1;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_LEFT = 3;
    private static final int BOTTOM_MID = 4;
    private static final int BOTTOM_RIGHT = 5;
    private Label chargeLabel;
    private Label buildResourceLabel;

    private Table buildTable;

    private final String spriteAtlasFile = "sprites.atlas";
    private TextureAtlas spriteAtlas;

    private final String btnPressSfxFile = "sfx/button_press.wav";
    private final String btnErrorSfxFile = "sfx/button_error.wav";
    private final String btnBackSfxFile = "sfx/button_back.wav";
    private Sound btnPressSfx;
    private Sound btnErrorSfx;
    private Sound btnBackSfx;

    private Array<StationComponent> componentArray;

    public MainControlScreen(final LifeInSpaceGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.getAssetManager().load(spriteAtlasFile, TextureAtlas.class);
        game.getAssetManager().load(btnPressSfxFile, Sound.class);
        game.getAssetManager().load(btnErrorSfxFile, Sound.class);
        game.getAssetManager().load(btnBackSfxFile, Sound.class);
        game.getAssetManager().finishLoading();

        spriteAtlas = game.getAssetManager().get(spriteAtlasFile);
        btnPressSfx = game.getAssetManager().get(btnPressSfxFile);
        btnErrorSfx = game.getAssetManager().get(btnErrorSfxFile);
        btnBackSfx = game.getAssetManager().get(btnBackSfxFile);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        SetupStyles();
        SetupComponents();
        SetupMainScreen();
        SetupBuildScreen();
    }

    private void SetupStyles() {
        labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.GOLD;

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.DARK_GRAY;
        buttonStyle.up = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
        buttonStyle.down = new NinePatchDrawable(spriteAtlas.createPatch("button_pressed"));
        buttonStyle.disabled = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));

        listStyle = new List.ListStyle();
        listStyle.font = new BitmapFont();
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.fontColorSelected = Color.YELLOW;
        listStyle.selection = new NinePatchDrawable(spriteAtlas.createPatch("button_normal")); // new BaseDrawable();
        listStyle.selection.setLeftWidth(10f);
        listStyle.selection.setTopHeight(10f);
        //listStyle.background = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));

        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));
    }

    private void SetupMainScreen() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(true);
        stage.addActor(mainTable);

        // setup grid for placeholder cells for station components
        mainGrid = new Container[6];
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                int gridIndex = (i * 3) + j;
                mainGrid[gridIndex] = new Container();
                mainTable.add(mainGrid[gridIndex]).expand().fill();
            }

            mainTable.row();
        }

        Table topMidGroup = new Table();
        topMidGroup.setDebug(true);
        mainGrid[TOP_MID].setActor(topMidGroup);

        chargeLabel = new Label("Charge = 0", labelStyle);
        chargeLabel.setAlignment(Align.center, Align.center);
        topMidGroup.add(chargeLabel).width(150f);
        topMidGroup.row();

        TextButton chargeButton = new TextButton("Charge", buttonStyle);
        chargeButton.pad(10);
        chargeButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                game.getStateManager().addCharge();
                btnPressSfx.play();
            }
        });

        topMidGroup.add(chargeButton).expandX().fill();
        topMidGroup.row();

        final TextButton buildButton = new TextButton("Build", buttonStyle);
        buildButton.pad(10);
        buildButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                mainTable.setVisible(false);
                buildTable.setVisible(true);
                btnPressSfx.play();
            }
        });
        topMidGroup.add(buildButton).expandX().fill();
        topMidGroup.row();
    }

    private void SetupBuildScreen() {
        buildTable = new Table();
        buildTable.setFillParent(true);
        buildTable.setVisible(false);
        stage.addActor(buildTable);

        final List<StationComponent> buildList = new List<StationComponent>(listStyle);
        buildList.setItems(componentArray);

        ScrollPane scrollPane = new ScrollPane(buildList, scrollStyle);
        buildTable.add(scrollPane).expand().fill().pad(10);

        buildTable.row();

        buildResourceLabel = new Label("Resources = 0", labelStyle);

        TextButton buildConfirmButton = new TextButton("Build", buttonStyle);
        buildConfirmButton.pad(10);
        buildConfirmButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if (game.getStateManager().has(buildList.getSelected()) || buildList.getSelected().getResourceCost() > game.getStateManager().getResources()) {
                    btnErrorSfx.play();
                } else {
                    StationComponent selected = buildList.getSelected();
                    Gdx.app.log("Build", "building " + selected);
                    game.getStateManager().spendResources(selected.getResourceCost());
                    game.getStateManager().add(selected);
                    if(selected.hasWidget()) {
                        mainGrid[selected.getWidget().position].setActor(selected.getWidget().widget);
                    }
                    btnPressSfx.play();
                }
            }
        });

        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.pad(10);
        backButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                mainTable.setVisible(true);
                buildTable.setVisible(false);
                btnBackSfx.play();
            }
        });
        HorizontalGroup buildButtonGroup = new HorizontalGroup();
        buildButtonGroup.space(100f);
        buildButtonGroup.padTop(10f);
        buildButtonGroup.padBottom(10f);
        buildButtonGroup.addActor(buildResourceLabel);
        buildButtonGroup.addActor(buildConfirmButton);
        buildButtonGroup.addActor(backButton);
        buildTable.add(buildButtonGroup);
    }

    private Label roverResourceLabel;

    private void SetupComponents() {
        componentArray = new Array<StationComponent>();

        Table roverTable = new Table();
        roverResourceLabel = new Label("Resources = " + game.getStateManager().getResources(), labelStyle);
        TextButton roverUseButton = new TextButton("Use Rover (costs 10 charge)", buttonStyle);
        roverUseButton.pad(10);
        roverUseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.getStateManager().getCharge() >= 10) {
                    game.setScreen(new RoverScreen(game));
                    game.getStateManager().spendCharge(10);
                    btnPressSfx.play();
                }
                else {
                    btnErrorSfx.play();
                }
            }
        });
        roverTable.add(roverResourceLabel);
        roverTable.row();
        roverTable.add(roverUseButton);


        roverTable.setDebug(true);
        Widget roverWidget = new Widget(BOTTOM_MID, roverTable);

        componentArray.add(new StationComponent("Rover", 100, 1, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addResourceRate(1);
            }
        }, roverWidget));

        componentArray.add(new StationComponent("Finger Strength Training (2 charge per press)", 200, 0, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addChargeRate(1);
            }
        }, null));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getStateManager().update(delta);
        updateUI();

        stage.act(delta);
        stage.draw();
    }

    private void updateUI() {
        if (mainTable.isVisible()) {
            chargeLabel.setText("Charge = " + game.getStateManager().getCharge());
            roverResourceLabel.setText("Resources = " + game.getStateManager().getResources());
        }
        else if (buildTable.isVisible()) {
            buildResourceLabel.setText("Resources = " + game.getStateManager().getResources());
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        game.getAssetManager().dispose();
    }
}
