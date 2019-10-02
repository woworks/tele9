package com.woworks.client9.model;

import java.util.Arrays;

public class Advert {
    private Long id;

    private String title;
    private Price price;
    private String body;
    private Categories categories;
    private Feature[] features;
    private OfferType offerType;
    private State state;

    public Advert(Long id, String title, Price price, String body, Categories categories, Feature[] features, OfferType offerType, State state) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.body = body;
        this.categories = categories;
        this.features = features;
        this.offerType = offerType;
        this.state = state;
    }

    public Advert(Long id, String title, Price price, String body) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.body = body;
    }

    /**
     * Body of advert
     */
    public String getBody() { return body; }

    public Categories getCategories() { return categories; }

    public Feature[] getFeatures() { return features; }

    /**
     * ID of the advert
     */
    public Long getId() { return id; }

    public OfferType getOfferType() { return offerType; }

    public Price getPrice() { return price; }

    /**
     * State of advert
     */
    public State getState() { return state; }

    /**
     * Title of advert
     */
    public String getTitle() { return title; }

    @Override
    public String toString() {
        return "Advert{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", body='" + body + '\'' +
                ", categories=" + categories +
                ", features=" + Arrays.toString(features) +
                ", offerType=" + offerType +
                ", state=" + state +
                '}';
    }
}
