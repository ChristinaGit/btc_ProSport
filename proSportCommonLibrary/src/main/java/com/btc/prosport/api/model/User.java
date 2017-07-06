package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface User {
    @Nullable
    String getAvatarUri();

    @Nullable
    City getCity();

    @Nullable
    String getFirstName();

    long getId();

    @Nullable
    String getLastName();

    @Nullable
    String getPhoneNumber();

    @Nullable
    String getEmail();
}
