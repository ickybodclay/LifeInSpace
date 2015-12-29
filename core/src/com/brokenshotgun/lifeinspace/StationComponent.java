package com.brokenshotgun.lifeinspace;

/**
 * Created by Chunk on 12/29/2015.
 */
public class StationComponent {
    private String name;
    private int resourceCost;
    private int energyCost;

    public StationComponent(String name, int resourceCost, int energyCost) {
        this.name = name;
        this.resourceCost = resourceCost;
        this.energyCost = energyCost;
    }

    public String getName() {
        return name;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getResourceCost() {
        return resourceCost;
    }
}
