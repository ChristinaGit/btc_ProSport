package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface SportComplexDetail {
    @Nullable
    String getAddress();

    @Nullable
    String getLocalTime();

    @Nullable
    List<? extends Attribute> getAttributes();

    @Nullable
    String getDescription();

    @Nullable
    Boolean getFavoriteState();

    long getId();

    @Nullable
    Boolean getInventoryChargeableState();

    @Nullable
    String getInventoryDescription();

    @Nullable
    String getName();

    @Nullable
    List<String> getPhoneNumbers();

    @Nullable
    List<? extends Photo> getPhotos();

    @Nullable
    List<? extends SubwayStation> getSubwayStations();

    @Nullable
    List<? extends WorkPeriod> getWorkPeriods();
}
