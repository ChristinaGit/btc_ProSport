package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface PlaygroundTitle {
    long getId();

    @Nullable
    String getName();

    long getSportComplexId();
}
