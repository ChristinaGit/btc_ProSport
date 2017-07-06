package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface SportKind {
    @Nullable
    String getIcon();

    long getId();

    @Nullable
    String getName();
}
