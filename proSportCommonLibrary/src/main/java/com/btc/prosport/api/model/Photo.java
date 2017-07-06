package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface Photo {
    long getId();

    int getPosition();

    @Nullable
    String getUri();
}
