package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface SportComplexTitle {
    @Nullable
    Boolean getFavoriteState();

    long getId();

    @Nullable
    String getLocalTime();

    @Nullable
    String getName();

    @Nullable
    List<String> getPhoneNumbers();
}
