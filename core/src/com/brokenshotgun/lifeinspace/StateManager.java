package com.brokenshotgun.lifeinspace;

public class StateManager {
    private int charge;

    public StateManager() {
        charge = 0;
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
}
