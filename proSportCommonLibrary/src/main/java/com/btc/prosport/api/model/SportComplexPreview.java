package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface SportComplexPreview {
    @Nullable
    City getCity();

    @Nullable
    Long getDistance();

    @Nullable
    Boolean getFavoriteState();

    long getId();

    @Nullable
    String getLocalTime();

    @Nullable
    Integer getMinimumPrice();

    @Nullable
    String getName();

    @Nullable
    Photo getPhoto();

    @Nullable
    List<? extends SubwayStation> getSubwayStations();
}
