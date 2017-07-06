package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface PlaygroundPreview {
    @Nullable
    List<? extends Attribute> getAttributes();

    @Nullable
    Covering getCovering();

    @Nullable
    Integer getHeight();

    long getId();

    @Nullable
    Integer getLength();

    @Nullable
    String getName();

    @Nullable
    Integer getPartLength();

    @Nullable
    Boolean getPartReservationSupportedState();

    @Nullable
    Integer getPartWidth();

    @Nullable
    Photo getPhoto();

    long getSportComplexId();

    @Nullable
    PlaygroundType getType();

    @Nullable
    Integer getWidth();
}
