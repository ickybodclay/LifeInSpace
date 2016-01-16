package com.brokenshotgun.lifeinspace;

public class StationComponent {
    private String name;
    private int resourceCost;
    private int chargeCost;
    private Effect effect;
    private Widget widget;
    private boolean unique;

    public StationComponent(String name, int resourceCost, int chargeCost, boolean unique, Effect effect, Widget widget) {
        this.name = name;
        this.resourceCost = resourceCost;
        this.chargeCost = chargeCost;
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
    public int getChargeCost() {
        return chargeCost;
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
        StringBuilder builder = new StringBuilder(name);
        if (resourceCost == 0) builder.append("\n[" + chargeCost + "C]");
        else if (chargeCost == 0) builder.append("\n[" + resourceCost + "R]");
        else {
            builder.append("\n[" + resourceCost);
            builder.append("R|" + chargeCost + "C]");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationComponent that = (StationComponent) o;

        if (resourceCost != that.resourceCost) return false;
        if (chargeCost != that.chargeCost) return false;
        if (unique != that.unique) return  false;
        if (!name.equals(that.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + resourceCost;
        result = 31 * result + chargeCost;
        result = 31 * result + (unique ? 1 : 0);
        return result;
    }
}
