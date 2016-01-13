package com.brokenshotgun.lifeinspace;

import java.util.HashSet;
import java.util.Set;

public class StateManager {
    private HashSet<StationComponent> stationComponents;

    private int charge;
    private int chargeRate;
    private int resources;
    private int resourceRate;
    private int water;

    private boolean autoCharge = false;
    private boolean autoGather = false;

    private int autoChargeRate = 0;
    private int autoGatherRate = 0;

    private int waterDrainRate = 10; // # of cycles to drain 1 water
    private int waterDrainCounter = 1;

    private int waterGatherRate = 1;
    private int oreGatherRate = 5;

    private float cycleTime = 1f;
    private float updateTime = 0f;

    private StateListener stateListener;

    public StateManager() {
        charge = 20; //= 0;
        chargeRate = 1;
        resources = 0;
        resourceRate = 0;
        water = 10;
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
        if (has(component) && component.isUnique()) return;
        if (!has(component)) stationComponents.add(component);
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

        if (waterDrainCounter % waterDrainRate == 0) {
            water--;
        }

        if (autoGather) resources += autoGatherRate;
        if (autoCharge) charge += autoChargeRate;

        if (stateListener != null) stateListener.onStateChanged(this);

        updateTime = 0f;
        waterDrainCounter++;
    }

    public Set<StationComponent> getStationComponents() {
        return stationComponents;
    }

    public void register(StateListener listener) {
        this.stateListener = listener;
    }

    public void addWater(int amount) {
        water += amount * waterGatherRate;
    }

    public void addOre(int amount) {
        resources += amount * oreGatherRate;
    }

    public int getWater() {
        return water;
    }

    public void doubleGatherRate() {
        waterGatherRate *= 2;
        oreGatherRate *= 2;
    }

    public int getWaterGatherRate() {
        return waterGatherRate;
    }

    public int getOreGatherRate() {
        return oreGatherRate;
    }

    public void addAutoCharge(int amount) {
        autoCharge = true;
        autoChargeRate += amount;
    }
}
