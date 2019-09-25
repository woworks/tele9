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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price = (Price) o;

        if (Double.compare(price.value, value) != 0) return false;
        return unit != null ? unit.equals(price.unit) : price.unit == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = unit != null ? unit.hashCode() : 0;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Price{" +
                "unit='" + unit + '\'' +
                ", value=" + value +
                '}';
    }
}
