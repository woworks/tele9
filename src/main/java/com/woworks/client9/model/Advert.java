package com.woworks.client9.model;

public class Advert {
    private String id;
    private String title;
    private Price price;
    private String body;
    private Categories categories;
    private Feature[] features;
    private OfferType offerType;
    private State state;


    /**
     * Body of advert
     */
    public String getBody() { return body; }
    public void setBody(String value) { this.body = value; }

    public Categories getCategories() { return categories; }
    public void setCategories(Categories value) { this.categories = value; }

    public Feature[] getFeatures() { return features; }
    public void setFeatures(Feature[] value) { this.features = value; }

    /**
     * ID of the advert
     */
    public String getId() { return id; }
    public void setId(String value) { this.id = value; }

    public OfferType getOfferType() { return offerType; }
    public void setOfferType(OfferType value) { this.offerType = value; }

    public Price getPrice() { return price; }
    public void setPrice(Price value) { this.price = value; }

    /**
     * State of advert
     */
    public State getState() { return state; }
    public void setState(State value) { this.state = value; }

    /**
     * Title of advert
     */
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title = value; }
}
