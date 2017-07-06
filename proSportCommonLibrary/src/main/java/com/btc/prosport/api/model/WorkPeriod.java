package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface WorkPeriod {
    @Nullable
    String getEnd();

    @Nullable
    String getStart();

    @Nullable
    String getWeekDay();
}
