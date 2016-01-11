package com.brokenshotgun.lifeinspace;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class StationComponent {
    private String name;
    private int resourceCost;
    private int energyCost;
    private Effect effect;
    private Widget widget;
    private boolean unique;

    public StationComponent(String name, int resourceCost, int energyCost, boolean unique, Effect effect, Widget widget) {
        this.name = name;
        this.resourceCost = resourceCost;
        this.energyCost = energyCost;
        this.unique = unique;
        this.effect = effect;
        this.widget = widget;
    }

    public String getName() {
        return name;
    }

    /**
     * Perpetual energy cost per station cycle.
     * @return
     */
    public int getEnergyCost() {
        return energyCost;
    }

    /**
     * One time 3D printer resource cost.
     * @return
     */
    public int getResourceCost() {
        return resourceCost;
    }

    public Effect getEffect() {
        return effect;
    }

    public boolean hasWidget() {
        return widget != null;
    }

    public Widget getWidget() {
        return widget;
    }

    public boolean isUnique() {
        return unique;
    }

    @Override
    public String toString() {
        return name +
            " resource cost = " + resourceCost +
            ", energy cost = " + energyCost + " charge per use";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationComponent that = (StationComponent) o;

        if (resourceCost != that.resourceCost) return false;
        if (energyCost != that.energyCost) return false;
        if (unique != that.unique) return  false;
        if (!name.equals(that.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + resourceCost;
        result = 31 * result + energyCost;
        result = 31 * result + (unique ? 1 : 0);
        return result;
    }
}
