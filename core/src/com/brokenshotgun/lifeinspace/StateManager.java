package com.brokenshotgun.lifeinspace;

import java.util.HashSet;

public class StateManager {
    private HashSet<StationComponent> stationComponents;

    private int charge;
    private int chargeRate;
    private int resources;
    private int resourceRate;

    private float cycleTime = 1f;
    private float updateTime = 0f;

    public StateManager() {
        charge = 0;
        chargeRate = 1;
        resources = 0;
        resourceRate = 0;
        stationComponents = new HashSet<StationComponent>();
    }

    public void addCharge() {
        charge += chargeRate;
    }

    public void addCharge(int amount) {
        charge += amount;
    }

    public boolean spendCharge(int amount) {
        if (amount > charge) return false;
        charge -= amount;
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
    }

    public int getResources() {
        return resources;
    }

    public void setResources(int resources) {
        this.resources = resources;
    }

    public int getResourceRate() {
        return resourceRate;
    }

    public void addResourceRate(int resourceRate) {
        this.resourceRate += resourceRate;
    }

    public void add(StationComponent component) {
        if (has(component)) return;

        stationComponents.add(component);

        if (!component.getEffect().isPerpetual()) {
            component.getEffect().apply(this);
        }
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

        for (StationComponent component : stationComponents) {
            if (component.getEffect().isPerpetual()) {
                component.getEffect().apply(this);
                charge -= component.getEnergyCost();
            }
        }

        updateTime = 0f;
    }
}
