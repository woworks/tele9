package com.woworks.client9.model;

public class Feature {
    private String id;
    private String unit;
    private String value;

    /**
     * Advert feature id
     */
    public String getID() { return id; }
    public void setID(String value) { this.id = value; }

    /**
     * Unit of a feature value (required only for features with `textbox_numeric_measurement`
     * type).
     */
    public String getUnit() { return unit; }
    public void setUnit(String value) { this.unit = value; }

    /**
     * Advert feature value
     */
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
