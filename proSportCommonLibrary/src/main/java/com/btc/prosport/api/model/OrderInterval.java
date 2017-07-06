package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface OrderInterval {
    @Nullable
    String getDate();

    int getEndIntervalIndex();

    long getId();

    int getStartIntervalIndex();
}
