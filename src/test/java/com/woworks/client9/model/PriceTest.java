package com.woworks.client9.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PriceTest {

    @Test
    void testEquals() {
        Price price0 = new Price("EUR", 100);
        Price price1 = new Price("EUR", 100);
        Price price2 = new Price("EUR", 200);
        Assertions.assertEquals(price0, price1);
        Assertions.assertNotEquals(price1, price2);
    }
}