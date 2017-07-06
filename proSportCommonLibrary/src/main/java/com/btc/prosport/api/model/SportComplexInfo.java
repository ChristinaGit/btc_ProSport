package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface SportComplexInfo {
    @Nullable
    String getAddress();

    @Nullable
    Boolean getFavoriteState();

    long getId();

    @Nullable
    String getLocalTime();

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
