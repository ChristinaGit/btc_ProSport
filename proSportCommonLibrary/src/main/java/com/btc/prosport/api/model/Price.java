package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface Price {
    @Nullable
    String getId();

    int getIntervalIndex();

    @Nullable
    Integer getValue();

    @Nullable
    String getWeekDay();
}
