package com.woworks.client9.model;

import java.io.IOException;

public enum State {
    BLOCKED, BLOCKED_COMMERCIAL, EXPIRED, HIDDEN, NEED_PAY, PUBLIC;

    public String toValue() {
        switch (this) {
            case BLOCKED: return "blocked";
            case BLOCKED_COMMERCIAL: return "blocked_commercial";
            case EXPIRED: return "expired";
            case HIDDEN: return "hidden";
            case NEED_PAY: return "need_pay";
            case PUBLIC: return "public";
        }
        return null;
    }

    public static State forValue(String value) throws IOException {
        if (value.equals("blocked")) return BLOCKED;
        if (value.equals("blocked_commercial")) return BLOCKED_COMMERCIAL;
        if (value.equals("expired")) return EXPIRED;
        if (value.equals("hidden")) return HIDDEN;
        if (value.equals("need_pay")) return NEED_PAY;
        if (value.equals("public")) return PUBLIC;
        throw new IOException("Cannot deserialize State");
    }
}
