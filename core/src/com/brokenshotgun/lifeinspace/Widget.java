package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class Widget {
    public final int position;
    public final Actor widget;

    public Widget(int position, Actor widget) {
        this.position = position;
        this.widget = widget;
    }
}
