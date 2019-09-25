package com.woworks.client9.model;

import java.util.ArrayList;
import java.util.List;

public class AdvertHistory {
    private Advert advert;
    private List<PriceChange> priceHistory = new ArrayList<>();

    public AdvertHistory(Advert advert, List<PriceChange> priceHistory) {
        this.advert = advert;
        this.priceHistory = priceHistory;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public List<PriceChange> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PriceChange> priceHistory) {
        this.priceHistory = priceHistory;
    }

    @Override
    public String toString() {
        return "AdvertHistory{" +
                "advert=" + advert +
                ", priceHistory=" + priceHistory +
                '}';
    }
}
