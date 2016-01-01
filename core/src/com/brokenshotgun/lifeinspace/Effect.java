package com.brokenshotgun.lifeinspace;

public interface Effect {
    void apply(StateManager stateManager);
    boolean isPerpetual();
}
