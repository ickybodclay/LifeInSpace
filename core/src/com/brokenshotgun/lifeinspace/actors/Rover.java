package com.brokenshotgun.lifeinspace.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;


public class Rover extends Actor {
    private final Sprite sprite;
    private final Body body;
    private final World world;

    private float speed = 400000f;

    private final Vector2 dir = new Vector2();
    private final Vector3 mousePos = new Vector3();
    private final float deadzone = 2f;

    public Rover(Sprite sprite, World world) {
        this.sprite = sprite;
        this.world = world;

        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(400, 300);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);
        body.setLinearDamping(3f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        sprite.setPosition(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));

        sprite.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        dir.x = 0f;
        dir.y = 0f;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mousePos.x = Gdx.input.getX();
            mousePos.y = Gdx.input.getY();
            mousePos.z = 0f;
            Vector3 newPos = getStage().getCamera().unproject(mousePos);
            dir.x = newPos.x - body.getPosition().x;
            dir.y = newPos.y - body.getPosition().y;

            if (Math.abs(dir.x) < deadzone && Math.abs(dir.y) < deadzone) {
                dir.x = 0f;
                dir.y = 0f;
            }
            else
                dir.nor();
        }
        
        body.applyForceToCenter(dir.x * speed, dir.y * speed, true);
    }
}
