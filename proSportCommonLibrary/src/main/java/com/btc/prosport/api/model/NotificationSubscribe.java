package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface NotificationSubscribe {
    @Nullable
    String getDateCreated();

    @Nullable
    String getDeviceId();

    long getId();

    @Nullable
    String getName();

    @Nullable
    String getRegistrationId();

    @Nullable
    String getType();

    boolean isActive();
}
