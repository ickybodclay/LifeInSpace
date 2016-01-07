package com.brokenshotgun.lifeinspace.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.brokenshotgun.lifeinspace.LifeInSpaceGame;
import com.brokenshotgun.lifeinspace.actors.Obstacle;
import com.brokenshotgun.lifeinspace.actors.Pickup;
import com.brokenshotgun.lifeinspace.actors.Rover;

public class RoverScreen implements Screen, ContactListener {
    private final LifeInSpaceGame game;

    private final Color martianRed = Color.valueOf("ac3232");
    private final String spriteAtlasFile = "sprites.atlas";
    private TextureAtlas spriteAtlas;
    private Sprite rockSprite;
    private Sprite oreSprite;
    private Sprite waterSprite;
    private Sprite roverSprite;

    private Stage stage;
    private World world;

    private Box2DDebugRenderer debugRenderer;

    private Table ui;
    private Rover rover;
    private Obstacle rock;
    private Pickup water;

    public RoverScreen(final LifeInSpaceGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.getAssetManager().load(spriteAtlasFile, TextureAtlas.class);
        game.getAssetManager().finishLoading();

        spriteAtlas = game.getAssetManager().get(spriteAtlasFile);

        rockSprite = spriteAtlas.createSprite("rock");
        oreSprite = spriteAtlas.createSprite("ore");
        waterSprite = spriteAtlas.createSprite("water");
        roverSprite = spriteAtlas.createSprite("rover");

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(this);

        rover = new Rover(roverSprite, world);
        stage.addActor(rover);

        rock = new Obstacle(rockSprite, world);
        stage.addActor(rock);

        water = new Pickup(waterSprite, world, Pickup.Type.WATER);
        stage.addActor(water);

        ui = new Table();
        ui.setFillParent(true);
        stage.addActor(ui);

        rover.toFront();

        debugRenderer = new Box2DDebugRenderer();
        /*
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;
        ui.add(new Label("Rover says : Hello world", labelStyle));
        */
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(martianRed.r, martianRed.g, martianRed.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1f / 60f, 6, 2);
        cleanupWorld();
        stage.act(delta);
        stage.draw();

        debugRenderer.render(world, stage.getBatch().getProjectionMatrix());

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.getStateManager().addResources(25);
            game.setScreen(new MainControlScreen(game));
        }
    }

    private void cleanupWorld() {
        // TODO change to iterate through list of active pickups
        if (water.isFlaggedForDelete()) {
            water.delete();
        }
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
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        world.dispose();
    }

    @Override
    public void beginContact(Contact contact) {
        Rover r =
            (contact.getFixtureA().getUserData() instanceof Rover) ? (Rover)contact.getFixtureA().getUserData() :
            (contact.getFixtureB().getUserData() instanceof Rover) ? (Rover)contact.getFixtureB().getUserData() : null;

        Pickup p =
            (contact.getFixtureA().getUserData() instanceof Pickup) ? (Pickup)contact.getFixtureA().getUserData() :
            (contact.getFixtureB().getUserData() instanceof Pickup) ? (Pickup)contact.getFixtureB().getUserData() : null;

        if (r != null && p != null) {
            Gdx.app.log("RoverScreen", "pickup acquired!");
            // add pickup resource type
            // remove pickup from world
            p.remove();
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
