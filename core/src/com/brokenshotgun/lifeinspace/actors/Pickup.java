package com.brokenshotgun.lifeinspace.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;

public class Pickup extends Actor implements Pool.Poolable {
    private Sprite sprite;
    private final World world;
    private Body body;
    private Type type;

    private boolean flaggedForDelete = false;
    private boolean deleted = false;

    public enum Type {
        ORE,
        WATER
    }

    public Pickup(World world) {
        this.world = world;
    }

    public void setup(Sprite typeSprite, Type type) {
        this.sprite = typeSprite;
        this.type = type;

        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(100, 200);

        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

        body.setTransform(x, y, 0f);
    }

    @Override
    public void reset() {
        flaggedForDelete = false;
        deleted = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (sprite != null && body != null) {
            sprite.setPosition(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
            sprite.setRotation((float) Math.toDegrees(body.getAngle()));
            sprite.draw(batch);
        }
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean remove() {
        flaggedForDelete = true;
        return super.remove();
    }

    public boolean isFlaggedForDelete() {
        return flaggedForDelete;
    }

    public void delete() {
        if (deleted || body == null) return;

        world.destroyBody(body);
        body = null;
        deleted = true;
    }
}
