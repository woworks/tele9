package com.woworks.client9.model;

import java.util.Set;

public class AdvertHistory {
    private Advert advert;
    private Set<PriceChange> priceHistory;

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Set<PriceChange> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(Set<PriceChange> priceHistory) {
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
