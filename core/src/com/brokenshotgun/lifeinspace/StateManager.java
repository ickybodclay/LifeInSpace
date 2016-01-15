package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.HashSet;
import java.util.Set;

public class StateManager {
    private HashSet<StationComponent> stationComponents;

    private int charge;
    private int chargeRate;
    private int resources;
    private int resourceRate;
    private int water;

    private boolean autoCharge;
    private boolean autoGather;
    private boolean drainCharge;

    private int autoChargeRate;
    private int autoGatherRate;

    private int waterDrainRate; // # of cycles to drain 1 water
    private int waterDrainCounter;

    private int waterGatherRate;
    private int oreGatherRate;

    private boolean victorious;

    private float cycleTime = 1f;
    private float updateTime = 0f;

    private StateListener stateListener;

    public StateManager() {
        stationComponents = new HashSet<StationComponent>();
        reset();
    }

    public void reset() {
        charge = 20; //= 0;
        chargeRate = 1;
        resources = 0;
        resourceRate = 0;
        water = 10;
        autoCharge = false;
        autoGather = false;
        drainCharge = false;
        autoChargeRate = 0;
        autoGatherRate = 0;
        waterDrainRate = 10;
        waterDrainCounter = 1;
        waterGatherRate = 1;
        oreGatherRate = 5;
        victorious = false;
        stationComponents.clear();
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
        if (drainCharge) spendCharge(1);

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

    public void setDrainCharge(boolean drainCharge) {
        this.drainCharge = drainCharge;
    }

    public void win() {
        victorious = true;
    }

    public boolean isVictorious() {
        return victorious;
    }

    public void save() {
        Preferences prefs = Gdx.app.getPreferences("com.brokenshotgun.marsbasesim");

        prefs.putInteger("charge", charge);
        prefs.putInteger("chargeRate", chargeRate);
        prefs.putInteger("resources", resources);
        prefs.putInteger("resourceRate", resourceRate);
        prefs.putInteger("water", water);
        prefs.putBoolean("autoCharge", autoCharge);
        prefs.putBoolean("autoGather", autoGather);
        prefs.putBoolean("drainCharge", drainCharge);
        prefs.putInteger("autoChargeRate", autoChargeRate);
        prefs.putInteger("autoGatherRate", autoGatherRate);
        prefs.putInteger("waterDrainRate", waterDrainRate);
        prefs.putInteger("waterDrainCounter", waterDrainCounter);
        prefs.putInteger("waterGatherRate", waterGatherRate);
        prefs.putInteger("oreGatherRate", oreGatherRate);
    }

    public void load() {
        Preferences prefs = Gdx.app.getPreferences("com.brokenshotgun.marsbasesim");

        charge = prefs.getInteger("charge");
        chargeRate = prefs.getInteger("chargeRate");
        resources = prefs.getInteger("resources");
        resourceRate = prefs.getInteger("resourceRate");
        water = prefs.getInteger("water");
        autoCharge = prefs.getBoolean("autoCharge");
        autoGather = prefs.getBoolean("autoGather");
        drainCharge = prefs.getBoolean("drainCharge");
        autoChargeRate = prefs.getInteger("autoChargeRate");
        autoGatherRate = prefs.getInteger("autoGatherRate");
        waterDrainRate = prefs.getInteger("waterDrainRate");
        waterDrainCounter = prefs.getInteger("waterDrainCounter");
        waterGatherRate = prefs.getInteger("waterGatherRate");
        oreGatherRate = prefs.getInteger("oreGatherRate");
    }
}
