package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface Order {
    @Nullable
    String getCreateDate();

    long getId();

    @Nullable
    List<? extends OrderMetadataInterval> getIntervals();

    int getOriginalPrice();

    @Nullable
    Boolean getPart();

    @Nullable
    User getPlayer();

    @Nullable
    PlaygroundTitle getPlayground();

    int getPrice();

    @Nullable
    SportComplexTitle getSportComplex();

    int getStateCode();
}
