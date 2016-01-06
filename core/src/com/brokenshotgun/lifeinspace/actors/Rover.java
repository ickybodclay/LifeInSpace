package com.brokenshotgun.lifeinspace.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;


public class Rover extends Actor {
    private final Sprite sprite;

    private float speed = 200f;

    public Rover(Sprite sprite) {
        this.sprite = sprite;
        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        sprite.setPosition(getX(), getY());
        sprite.draw(batch);
    }

    private final Vector2 dir = new Vector2();
    private final Vector3 mousePos = new Vector3();
    private final float deadzone = 2f;

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
            //Gdx.app.log("Rover", String.format("M(%.2f, %.2f) | A(%.2f, %.2f)", newPos.x, newPos.y, getX(Align.center), getY(Align.center)));
            dir.x = newPos.x - getX(Align.center);
            dir.y = newPos.y - getY(Align.center);

            if (Math.abs(dir.x) < deadzone && Math.abs(dir.y) < deadzone) {
                dir.x = 0f;
                dir.y = 0f;
            }
            else
                dir.nor();
        }

        setX(getX() + (dir.x * speed * delta));
        setY(getY() + (dir.y * speed * delta));
    }
}
