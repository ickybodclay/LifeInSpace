package com.brokenshotgun.lifeinspace.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class Rover extends Actor {
    private final Sprite sprite;

    private float speed = 200f;

    public Rover(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        sprite.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float dirX =
                Gdx.input.isKeyPressed(Input.Keys.LEFT) ? -speed :
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? speed : 0f;

        float dirY =
                Gdx.input.isKeyPressed(Input.Keys.UP) ? speed :
                Gdx.input.isKeyPressed(Input.Keys.DOWN) ? -speed : 0f;

        sprite.translateX(dirX * delta);
        sprite.translateY(dirY * delta);
    }
}
