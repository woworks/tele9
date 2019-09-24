package com.woworks.client9.model;

import java.time.LocalDateTime;

public class PriceChange {
    private Price price;
    private LocalDateTime dateTime;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
