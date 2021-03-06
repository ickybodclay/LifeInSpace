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
    private Table[] mainGrid;
    private static final int LEFT = 0;
    private static final int MID = 1;
    private static final int RIGHT = 2;
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
        mainGrid = new Table[3];
        for (int i = 0; i < 3; ++i) {
            mainGrid[i] = new Table();
            mainGrid[i].setDebug(debug);
            mainTable.add(mainGrid[i]).minWidth(266f).minHeight(600f);
        }

        setupStyles();
        setupComponents();
        setupChargeWidget();
        setupBuildWidget();
        setupWidgets();
    }

    private void setupWidgets() {
        StationComponent[] components = new StationComponent[componentArray.size];
        for (int i = 0; i < componentArray.size; ++i) {
            components[i] = componentArray.get(i);
        }

        for (int i = 0; i < components.length; ++i) {
            if (game.getStateManager().has(components[i])) {
                restore(components[i]);
            }
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
        chargeLabel = new Label("Charge = " + game.getStateManager().getCharge(), chaLabelStyle);
        chargeLabel.setAlignment(Align.center, Align.center);
        mainGrid[MID].add(chargeLabel).expandX().fill();
        mainGrid[MID].row();

        waterLabel = new Label("Water = " + game.getStateManager().getWater(), watLabelStyle);
        waterLabel.setAlignment(Align.center, Align.center);
        mainGrid[MID].add(waterLabel).expandX().fill();
        mainGrid[MID].row();

        Button chargeButton = new Button(chargeButtonStyle);
        chargeButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.getStateManager().addCharge();
                btnPressSfx.play();
            }
        });

        mainGrid[MID].add(chargeButton).padBottom(15f);
        mainGrid[MID].row();
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
        mainGrid[RIGHT].add(buildTable);

        ScrollPane scrollPane = new ScrollPane(buildList, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        buildTable.add(scrollPane).height(530f).width(266f);

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
            addWidget(component);
        }
        refreshSelectionBackground();
    }

    private void restore(StationComponent component) {
        if(component.isUnique()) {
            componentArray.removeValue(component, false);
            buildList.setItems(componentArray);
        }
        if (component.hasWidget()) {
            addWidget(component);
        }
    }

    private void addWidget(StationComponent component) {
        mainGrid[component.getWidget().position].add(component.getWidget().widget).expandX().fill();
        mainGrid[component.getWidget().position].row();
    }

    private void setupComponents() {
        componentArray = new Array<StationComponent>();

        TextButton roverUseButton = new TextButton("Use Rover (-10C)", textButtonStyle);
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

        Widget roverWidget = new Widget(LEFT, roverUseButton);

        componentArray.add(new StationComponent("Rover", 0, 10, true, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addResourceRate(1);
            }
        }, roverWidget));

        Label.LabelStyle widgetLabelStyle = new Label.LabelStyle();
        widgetLabelStyle.fontColor = Color.PURPLE;
        widgetLabelStyle.font = new BitmapFont();

        Label strengthLabel = new Label("[Finger strength]", widgetLabelStyle);
        strengthLabel.setAlignment(Align.center, Align.center);
        Widget strengthWidget = new Widget(MID, strengthLabel);

        componentArray.add(new StationComponent("Finger strength (+2C per press)", 0, 500, true, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addChargeRate(1);
            }
        }, strengthWidget));

        final Label refiningLabel = new Label("[Improved refining: " + game.getStateManager().getRefiningLevel() + "]", widgetLabelStyle);
        refiningLabel.setAlignment(Align.center, Align.center);
        Widget refiningWidget = new Widget(LEFT, refiningLabel);

        componentArray.add(new StationComponent("Improved refining (+10R)", 100, 200, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.increaseGatherRate(0, 10);
                stateManager.incrementRefiningLevel();
                refiningLabel.setText("[Improved refining: " + game.getStateManager().getRefiningLevel() + "]");
            }
        }, refiningWidget));

        final Label solarPanelLabel = new Label("[Solar panel: " + game.getStateManager().getSolarPanelCount() + "]", widgetLabelStyle);
        solarPanelLabel.setAlignment(Align.center, Align.center);
        Widget solarPanelWidget = new Widget(LEFT, solarPanelLabel);

        componentArray.add(new StationComponent("Solar panel (+10C per second)", 100, 50, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addAutoCharge(10);
                stateManager.incrementSolarPanelCount();
                solarPanelLabel.setText("[Solar panel: " + stateManager.getSolarPanelCount() + "]");
            }
        }, solarPanelWidget));

        final Label solarGridLabel = new Label("[Solar grid: " + game.getStateManager().getSolarGridCount() + "]", widgetLabelStyle);
        solarGridLabel.setAlignment(Align.center, Align.center);
        Widget solarGridWidget = new Widget(LEFT, solarGridLabel);

        componentArray.add(new StationComponent("Solar tracking grid (+100C per second)", 1000, 2000, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addAutoCharge(100);
                stateManager.incrementSolarGridCount();
                solarGridLabel.setText("[Solar grid: " + stateManager.getSolarGridCount() + "]");
            }
        }, solarGridWidget));

        Label detectorLabel = new Label("[Cybernetic Detector]", widgetLabelStyle);
        detectorLabel.setAlignment(Align.center, Align.center);
        Widget detectorWidget = new Widget(LEFT, detectorLabel);

        componentArray.add(new StationComponent("Cybernetic Detector (Find Resource+)", 500, 3000, true, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.increaseResourceSpawnRate();
            }
        }, detectorWidget));

        final Label miningArmLabel = new Label("[Pneumatic mining arm: " + game.getStateManager().getPneumaticArmCount() + "]", widgetLabelStyle);
        miningArmLabel.setAlignment(Align.center, Align.center);
        Widget miningArmWidget = new Widget(LEFT, miningArmLabel);

        componentArray.add(new StationComponent("Pneumatic Mining Arm (+100R)", 900, 15000, false, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.increaseGatherRate(0, 100);
                stateManager.incrementPneumaticArmCount();
                miningArmLabel.setText("[Pneumatic mining arm: " + stateManager.getPneumaticArmCount() + "]");
            }
        }, miningArmWidget));

        componentArray.add(new StationComponent("Spaceship (Return to Earth)", 10000, 1000000, true, new Effect() {
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
