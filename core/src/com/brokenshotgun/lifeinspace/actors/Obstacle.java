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

public class Obstacle extends Actor implements Pool.Poolable {
    private final Sprite sprite;
    private final World world;
    private Body body;

    public Obstacle(Sprite sprite, World world) {
        this.sprite = sprite;
        this.world = world;

        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());
    }

    public void setup() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(100, 100);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() * 0.4f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

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
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(body != null) {
            sprite.setPosition(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
            sprite.setRotation((float) Math.toDegrees(body.getAngle()));
            sprite.draw(batch);
        }
    }

    @Override public void reset() {}
}
