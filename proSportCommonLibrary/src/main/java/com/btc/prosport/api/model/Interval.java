package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface Interval {
    @Nullable
    String getId();

    int getIndex();

    @Nullable
    Long getOrderId();

    @Nullable
    Integer getPrice();

    @Nullable
    String getStateCode();

    @Nullable
    String getWeekDay();

    boolean isSale();
}
