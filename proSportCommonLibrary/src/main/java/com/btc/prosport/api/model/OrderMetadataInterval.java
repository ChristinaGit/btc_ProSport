package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface OrderMetadataInterval {
    @Nullable
    String getDateEnd();

    @Nullable
    String getDateStart();

    long getId();

    @Nullable
    String getRepeatWeekDays();

    @Nullable
    String getTimeEnd();

    @Nullable
    String getTimeStart();
}
