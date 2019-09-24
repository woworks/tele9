package com.woworks.client9.model;

public class Price {
    private String unit;
    private double value;

    public Price(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() { return unit; }
    public void setUnit(String value) { this.unit = value; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    @Override
    public String toString() {
        return "Price{" +
                "unit='" + unit + '\'' +
                ", value=" + value +
                '}';
    }
}
