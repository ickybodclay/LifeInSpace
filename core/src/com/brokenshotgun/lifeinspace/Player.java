package com.brokenshotgun.lifeinspace;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

/**
 * Created by Jason on 12/21/2015.
 */
public class Player implements IScript {
    private Entity entity;
    private TransformComponent transformComponent;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        transformComponent = entity.getComponent(TransformComponent.class);
    }

    @Override
    public void act(float delta) {
        transformComponent.x +=
                Gdx.input.isKeyPressed(Input.Keys.LEFT) ? -1 :
                Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? 1 : 0;

        transformComponent.y +=
                Gdx.input.isKeyPressed(Input.Keys.UP) ? 1 :
                Gdx.input.isKeyPressed(Input.Keys.DOWN) ? -1 : 0;
    }

    @Override
    public void dispose() {

    }
}
