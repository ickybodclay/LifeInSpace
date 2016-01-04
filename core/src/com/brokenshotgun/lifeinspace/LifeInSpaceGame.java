package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class LifeInSpaceGame extends ApplicationAdapter {
    private Stage stage;

    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle buttonStyle;
    private List.ListStyle listStyle;
    private ScrollPane.ScrollPaneStyle scrollStyle;

    private Table mainTable;
    private Label chargeLabel;
    private Label resourceLabel;
    private Table buildTable;

    private AssetManager assetManager;

    private final String spriteAtlasFile = "sprites.atlas";
    private TextureAtlas spriteAtlas;
    private StateManager stateManager;

    private final String btnPressSfxFile = "sfx/button_press.wav";
    private final String btnErrorSfxFile = "sfx/button_error.wav";
    private final String btnBackSfxFile = "sfx/button_back.wav";
    private Sound btnPressSfx;
    private Sound btnErrorSfx;
    private Sound btnBackSfx;

    private Array<StationComponent> componentArray;

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.load(spriteAtlasFile, TextureAtlas.class);
        assetManager.load(btnPressSfxFile, Sound.class);
        assetManager.load(btnErrorSfxFile, Sound.class);
        assetManager.load(btnBackSfxFile, Sound.class);
        assetManager.finishLoading();

        spriteAtlas = assetManager.get(spriteAtlasFile);
        btnPressSfx = assetManager.get(btnPressSfxFile);
        btnErrorSfx = assetManager.get(btnErrorSfxFile);
        btnBackSfx = assetManager.get(btnBackSfxFile);

        stateManager = new StateManager();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        SetupComponents();
        SetupStyles();
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

    private Container[] mainGrid;
    private static final int TOP_LEFT = 0;
    private static final int TOP_MID = 1;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_LEFT = 3;
    private static final int BOTTOM_MID = 4;
    private static final int BOTTOM_RIGHT = 5;

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
        chargeButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stateManager.addCharge();
                btnPressSfx.play();
                return false;
            }
        });

        topMidGroup.add(chargeButton).expandX().fill();
        topMidGroup.row();

        final TextButton buildButton = new TextButton("Build", buttonStyle);
        buildButton.pad(10);
        buildButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainTable.setVisible(false);
                buildTable.setVisible(true);
                btnPressSfx.play();
                return false;
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

        resourceLabel = new Label("Resources = 0", labelStyle);

        TextButton buildConfirmButton = new TextButton("Build", buttonStyle);
        buildConfirmButton.pad(10);
        buildConfirmButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (stateManager.has(buildList.getSelected()) || buildList.getSelected().getResourceCost() > stateManager.getResources()) {
                    btnErrorSfx.play();
                } else {
                    Gdx.app.log("Build", "building " + buildList.getSelected());
                    stateManager.spendResources(buildList.getSelected().getResourceCost());
                    stateManager.add(buildList.getSelected());
                    btnPressSfx.play();
                }
                return false;
            }
        });

        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.pad(10);
        backButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainTable.setVisible(true);
                buildTable.setVisible(false);
                btnBackSfx.play();
                return false;
            }
        });
        HorizontalGroup buildButtonGroup = new HorizontalGroup();
        buildButtonGroup.space(100f);
        buildButtonGroup.padTop(10f);
        buildButtonGroup.padBottom(10f);
        buildButtonGroup.addActor(resourceLabel);
        buildButtonGroup.addActor(buildConfirmButton);
        buildButtonGroup.addActor(backButton);
        buildTable.add(buildButtonGroup);
    }

    private void SetupComponents() {
        componentArray = new Array<StationComponent>();
        componentArray.add(new StationComponent("Rover", 100, 1, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addResourceRate(1);
            }

            @Override
            public boolean isPerpetual() {
                return false;
            }
        }));

        componentArray.add(new StationComponent("Finger Strength Training (2 charge per press)", 200, 0, new Effect() {
            @Override
            public void apply(StateManager stateManager) {
                stateManager.addChargeRate(1);
            }

            @Override
            public boolean isPerpetual() {
                return false;
            }
        }));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateManager.update(Gdx.graphics.getDeltaTime());
        updateUI();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void updateUI() {
        chargeLabel.setText("Charge = " + stateManager.getCharge());
        resourceLabel.setText("Resources = " + stateManager.getResources());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        assetManager.dispose();
    }
}
