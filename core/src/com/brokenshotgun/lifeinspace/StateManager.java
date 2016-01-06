package com.brokenshotgun.lifeinspace;

import java.util.HashSet;
import java.util.Set;

public class StateManager {
    private HashSet<StationComponent> stationComponents;

    private int charge;
    private int chargeRate;
    private int resources;
    private int resourceRate;

    private float cycleTime = 1f;
    private float updateTime = 0f;

    private StateListener stateListener;

    public StateManager() {
        charge = 10;
        chargeRate = 1;
        resources = 100;
        resourceRate = 0;
        stationComponents = new HashSet<StationComponent>();
    }

    public void addCharge() {
        addCharge(chargeRate);
    }

    public void addCharge(int amount) {
        charge += amount;

        if (stateListener != null) stateListener.onStateChanged(this);
    }

    public void addResources(int amount) {
        resources += amount;

        if (stateListener != null) stateListener.onStateChanged(this);
    }

    public boolean spendCharge(int amount) {
        if (amount > charge) return false;
        charge -= amount;
        if (stateListener != null) stateListener.onStateChanged(this);
        return true;
    }

    public boolean spendResources(int amount) {
        if (amount > resources) return false;
        resources -= amount;
        if (stateListener != null) stateListener.onStateChanged(this);
        return true;
    }

    public int getCharge() {
        return charge;
    }

    public int getChargeRate() {
        return chargeRate;
    }

    public void addChargeRate(int chargeRate) {
        this.chargeRate += chargeRate;

        if (stateListener != null) stateListener.onStateChanged(this);
    }

    public int getResources() {
        return resources;
    }

    public int getResourceRate() {
        return resourceRate;
    }

    public void addResourceRate(int resourceRate) {
        this.resourceRate += resourceRate;

        if (stateListener != null) stateListener.onStateChanged(this);
    }

    public void add(StationComponent component) {
        if (has(component)) return;
        stationComponents.add(component);
        component.getEffect().apply(this);
    }

    public void remove(StationComponent component) {
        stationComponents.remove(component);
    }

    public boolean has(StationComponent component) {
        return stationComponents.contains(component);
    }

    public void update(float delta) {
        updateTime += delta;

        if (updateTime < cycleTime) return;

        resources += resourceRate;
        charge += chargeRate;

        if (stateListener != null) stateListener.onStateChanged(this);

        updateTime = 0f;
    }

    public Set<StationComponent> getStationComponents() {
        return stationComponents;
    }

    public void register(StateListener listener) {
        this.stateListener = listener;
    }
}
