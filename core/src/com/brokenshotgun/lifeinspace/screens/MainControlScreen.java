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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.brokenshotgun.lifeinspace.Effect;
import com.brokenshotgun.lifeinspace.LifeInSpaceGame;
import com.brokenshotgun.lifeinspace.StateListener;
import com.brokenshotgun.lifeinspace.StateManager;
import com.brokenshotgun.lifeinspace.StationComponent;
import com.brokenshotgun.lifeinspace.Widget;

public class MainControlScreen implements Screen, StateListener {
    private final LifeInSpaceGame game;
    private boolean debug = false;

    private Stage stage;

    private Label.LabelStyle chaLabelStyle;
    private Label.LabelStyle resLabelStyle;
    private Label.LabelStyle watLabelStyle;

    private TextButton.TextButtonStyle textButtonStyle;
    private Button.ButtonStyle chargeButtonStyle;
    private List.ListStyle listStyle;
    private ScrollPane.ScrollPaneStyle scrollStyle;
    private Drawable itemEnabledBg;
    private Drawable itemDisabledBg;

    private Table mainTable;
    private Container[] mainGrid;
    private static final int TOP_LEFT = 0;
    private static final int TOP_MID = 1;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_LEFT = 3;
    private static final int BOTTOM_MID = 4;
    private static final int BOTTOM_RIGHT = 5;
    private Label chargeLabel;
    private Label waterLabel;
    private Label buildResourceLabel;

    private List<StationComponent> buildList;

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

        setupUI();

        game.getStateManager().register(this);
    }

    private void setupUI() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(debug);
        stage.addActor(mainTable);

        // setup grid for placeholder cells for station components
        mainGrid = new Container[6];
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                int gridIndex = (i * 3) + j;
                mainGrid[gridIndex] = new Container();
                mainTable.add(mainGrid[gridIndex]).minWidth(266f).minHeight(300f);
            }

            mainTable.row();
        }

        setupStyles();
        setupComponents();
        setupChargeWidget();
        setupBuildWidget();
        setupWidgets();
    }

    private void setupWidgets() {
        for (StationComponent component : game.getStateManager().getStationComponents()) {
            restore(component);
        }
    }

    private void setupStyles() {
        chaLabelStyle = new Label.LabelStyle();
        chaLabelStyle.font = new BitmapFont();
        chaLabelStyle.fontColor = Color.GOLD;

        resLabelStyle = new Label.LabelStyle();
        resLabelStyle.font = new BitmapFont();
        resLabelStyle.fontColor = Color.FIREBRICK;

        watLabelStyle = new Label.LabelStyle();
        watLabelStyle.font = new BitmapFont();
        watLabelStyle.fontColor = Color.CYAN;

        chargeButtonStyle = new Button.ButtonStyle();
        chargeButtonStyle.up = new SpriteDrawable(spriteAtlas.createSprite("charge_button_normal"));
        chargeButtonStyle.down = new SpriteDrawable(spriteAtlas.createSprite("charge_button_pressed"));

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.YELLOW;
        textButtonStyle.downFontColor = Color.DARK_GRAY;
        textButtonStyle.up = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
        textButtonStyle.down = new NinePatchDrawable(spriteAtlas.createPatch("button_pressed"));
        textButtonStyle.disabled = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));

    itemEnabledBg = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));
    itemDisabledBg = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));

    listStyle = new List.ListStyle();
    listStyle.font = new BitmapFont();
    listStyle.fontColorUnselected = Color.WHITE;
    listStyle.fontColorSelected = Color.YELLOW;
    listStyle.selection = itemEnabledBg;
    listStyle.selection.setLeftWidth(LIST_PAD_LEFT);
    listStyle.selection.setBottomHeight(LIST_PAD_BOTTOM);
    //listStyle.background = new NinePatchDrawable(spriteAtlas.createPatch("button_normal"));

    scrollStyle = new ScrollPane.ScrollPaneStyle();
    scrollStyle.vScrollKnob = new NinePatchDrawable(spriteAtlas.createPatch("button_disabled"));
}

    private void setupChargeWidget() {
        Table topMidGroup = new Table();
        topMidGroup.setDebug(debug);
        mainGrid[TOP_MID].setActor(topMidGroup);

        chargeLabel = new Label("Charge = " + game.getStateManager().getCharge(), chaLabelStyle);
        chargeLabel.setAlignment(Align.center, Align.center);
        topMidGroup.add(chargeLabel).width(150f);
        topMidGroup.row();

        waterLabel = new Label("Water = " + game.getStateManager().getWater(), watLabelStyle);
        waterLabel.setAlignment(Align.center, Align.center);
        topMidGroup.add(waterLabel).width(150f);
        topMidGroup.row();

        Button chargeButton = new Button(chargeButtonStyle);
        chargeButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.getStateManager().addCharge();
                btnPressSfx.play();
            }
        });

        topMidGroup.add(chargeButton).expandX().fill().padBottom(15f);
        topMidGroup.row();
    }

    private void setupBuildWidget() {
        buildList = new List<StationComponent>(listStyle);
        buildList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshSelectionBackground();
            }
        });
        buildList.setItems(componentArray);

        Table buildTable = new Table();
        mainGrid[TOP_RIGHT].setActor(buildTable);

        ScrollPane scrollPane = new ScrollPane(buildList, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        buildTable.add(scrollPane).height(230f).width(266f);

        buildResourceLabel = new Label("Resources = " + game.getStateManager().getResources(), resLabelStyle);

        TextButton buildConfirmButton = new TextButton("Build", textButtonStyle);
        buildConfirmButton.pad(10f);
        buildConfirmButton.padBottom(20f);
        buildConfirmButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                StationComponent selected = buildList.getSelected();
                if ((game.getStateManager().has(selected) && selected.isUnique()) ||
                        selected.getResourceCost() > game.getStateManager().getResources() ||
                        selected.getChargeCost() > game.getStateManager().getCharge()) {
                    btnErrorSfx.play();
                } else {
                    Gdx.app.log("Build", "building " + selected);
                    game.getStateManager().spendResources(selected.getResourceCost());
                    game.getStateManager().spendCharge(selected.getChargeCost());
                    build(selected);
                    btnPressSfx.play();
                }
            }
        });

        HorizontalGroup buildButtonGroup = new HorizontalGroup();
        buildButtonGroup.space(100f);
        buildButtonGroup.padTop(10f);
        buildButtonGroup.padBottom(10f);
        buildButtonGroup.addActor(buildResourceLabel);
        buildButtonGroup.addActor(buildConfirmButton);

        buildTable.row();
        buildTable.add(buildButtonGroup);
    }

    private final float LIST_PAD_LEFT = 10f;
    private final float LIST_PAD_BOTTOM = 40f;
    private void refreshSelectionBackground() {
        StationComponent selected = buildList.getSelected();
        if (selected.getResourceCost() > game.getStateManager().getResources() ||
                selected.getChargeCost() > game.getStateManager().getCharge()) {
            listStyle.selection = itemDisabledBg;
            listStyle.selection.setLeftWidth(LIST_PAD_LEFT);
            listStyle.selection.setBottomHeight(LIST_PAD_BOTTOM);
        }
        else {
            listStyle.selection = itemEnabledBg;
            listStyle.selection.setLeftWidth(LIST_PAD_LEFT);
            listStyle.selection.setBottomHeight(LIST_PAD_BOTTOM);
        }
    }

    private void build(StationComponent component) {
        game.getStateManager().add(component);
        if(component.isUnique()) {
            componentArray.removeValue(component, false);
            buildList.setItems(componentArray);
        }
        if (component.hasWidget()) {
            mainGrid[component.getWidget().position].setActor(component.getWidget().widget);
        }
        refreshSelectionBackground();
    }

    private void restore(StationComponent component) {
        if(component.isUnique()) {
            componentArray.removeValue(component, false);
            buildList.setItems(componentArray);
        }
        if (component.hasWidget()) {
            mainGrid[component.getWidget().position].setActor(component.getWidget().widget);
        }
    }

    private void setupComponents() {
        componentArray = new Array<StationComponent>();

        Table roverTable = new Table();
        TextButton roverUseButton = new TextButton("Use Rover (costs 10 charge)", textButtonStyle);
        roverUseButton.pad(10f);
        roverUseButton.padBottom(20f);
        roverUseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.getStateManager().getCharge() >= 10) {
                    game.setScreen(new RoverScreen(game));
                    game.getStateManager().spendCharge(10);
                    btnPressSfx.play();
                } else {
                    btnErrorSfx.play();
                }
            }
        });
        roverTable.add(roverUseButton);

        roverTable.setDebug(debug);
        Widget roverWidget = new Widget(BOTTOM_MID, roverTable);

        // component exponential cost increase 499.075 e^(2.30285 x)

        componentArray.add(new StationComponent("Rover", 0, 10, true, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addResourceRate(1);
            }
        }, roverWidget));

        componentArray.add(new StationComponent("Finger brace (+2C per press)", 50, 100, true, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addChargeRate(1);
            }
        }, null));

        componentArray.add(new StationComponent("Improved refining (x2 gather)", 100, 200, true, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.doubleGatherRate();
            }
        }, null));

        componentArray.add(new StationComponent("Solar panel (+1C per second)", 10, 5, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addAutoCharge(1);
            }
        }, null));

        componentArray.add(new StationComponent("Test Item #1", 1337, 666, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                Gdx.app.log("MainControlScreen", "test");
            }
        }, null));

        componentArray.add(new StationComponent("Test Item #2", 1337, 666, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                Gdx.app.log("MainControlScreen", "test");
            }
        }, null));

        componentArray.add(new StationComponent("Test Item #3", 1337, 666, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                Gdx.app.log("MainControlScreen", "test");
            }
        }, null));

        componentArray.add(new StationComponent("Test Item #4", 1337, 666, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                Gdx.app.log("MainControlScreen", "test");
            }
        }, null));

        componentArray.add(new StationComponent("Test Item #5", 1337, 666, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                Gdx.app.log("MainControlScreen", "test");
            }
        }, null));

        componentArray.add(new StationComponent("Spaceship (Return to Earth)", 10000, 1000000, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                game.getStateManager().win();
            }
        }, null));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getStateManager().update(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void onStateChanged(StateManager stateManager) {
        chargeLabel.setText("Charge = " + game.getStateManager().getCharge());
        waterLabel.setText("Water = " + game.getStateManager().getWater());
        buildResourceLabel.setText("Resources = " + game.getStateManager().getResources());
        refreshSelectionBackground();
        checkForGameOver();
    }

    private void checkForGameOver() {
        if (game.getStateManager().getWater() <= 0) {
            game.setScreen(new GameOverScreen(game));
        }

        if (game.getStateManager().isVictorious()) {
            game.setScreen(new VictoryScreen(game));
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
        game.getStateManager().register(null);
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
