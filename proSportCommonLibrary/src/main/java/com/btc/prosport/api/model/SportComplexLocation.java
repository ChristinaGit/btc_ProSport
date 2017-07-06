package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

public interface SportComplexLocation {
    @Nullable
    Boolean getFavoriteState();

    long getId();

    @Nullable
    String getLocalTime();

    @Nullable
    Double getLocationLatitude();

    @Nullable
    Double getLocationLongitude();

    @Nullable
    String getPlaceId();
}
