package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface PlaygroundReport {
    @Nullable
    String getColor();

    long getId();

    @Nullable
    String getName();

    @Nullable
    Integer getNotConfirmedOrdersCount();
}
