package com.woworks.client9.model;

public class Subcategory {
    private String id;
    private String title;
    private String url;

    /**
     * ID of subcategory
     */
    public String getID() {
        return id;
    }

    public void setID(String value) {
        this.id = value;
    }

    /**
     * Title of category
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * URL of category
     */
    public String getURL() {
        return url;
    }

    public void setURL(String value) {
        this.url = value;
    }
}
