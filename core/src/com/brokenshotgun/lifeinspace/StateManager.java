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
    private int solarGridCount;

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

    public void increaseGatherRate(int multiplier) {
        waterGatherRate *= multiplier;
        oreGatherRate *= multiplier;
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

    private int solarPanelCount = 0;
    public void incrementSolarPanelCount() {
        solarPanelCount++;
    }

    private int pneumaticArmCount = 0;
    public void incrementPneumaticArmCount() {
        pneumaticArmCount++;
    }

    public void increaseResourceSpawnRate() {
        resourceSpawnRate *= 3;
    }

    private int resourceSpawnRate = 10;
    public int getResourceSpawnRate() {
        return resourceSpawnRate;
    }

    public void save() {
        Preferences prefs = Gdx.app.getPreferences("com.brokenshotgun.marsbasesim");

        Gdx.app.log("StateManager", "Saving...");

        int tmpCharge = charge;
        int tmpResources = resources;

        // hack to get around station components not being serializable;
        // when game state is restored, player will have to re-buy components
        for (StationComponent component : stationComponents) {
            tmpCharge += component.getChargeCost();
            tmpResources += component.getResourceCost();
        }

        if (solarPanelCount > 1) {
            tmpCharge += 5 * (solarPanelCount - 1);
            tmpResources += 10 * (solarPanelCount - 1);
        }

        if (pneumaticArmCount > 1) {
            tmpCharge += 5000 * (pneumaticArmCount - 1);
            tmpResources += 10000 * (pneumaticArmCount - 1);
        }

        prefs.putInteger("charge", tmpCharge);
        prefs.putInteger("resources", tmpResources);
        prefs.putInteger("water", water);
        prefs.flush();

        Gdx.app.log("StateManager", "Saved!");
    }

    public void load() {
        Preferences prefs = Gdx.app.getPreferences("com.brokenshotgun.marsbasesim");

        charge = prefs.getInteger("charge", 0);
        resources = prefs.getInteger("resources", 0);
        water = prefs.getInteger("water", 10);
    }

    public void clearSave() {
        Preferences prefs = Gdx.app.getPreferences("com.brokenshotgun.marsbasesim");
        prefs.clear();
        prefs.flush();
    }

    public int getSolarPanelCount() {
        return solarPanelCount;
    }

    public int getSolarGridCount() {
        return solarGridCount;
    }

    public void incrementSolarGridCount() {
        solarGridCount++;
    }

    public int getPneumaticArmCount() {
        return pneumaticArmCount;
    }
}
