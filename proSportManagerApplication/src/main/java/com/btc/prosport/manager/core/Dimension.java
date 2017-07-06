package com.btc.prosport.manager.core;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public class Dimension {

    public Dimension(
        @Nullable final Integer width,
        @Nullable final Integer length,
        @Nullable final Integer height) {
        _width = width;
        _length = length;
        _height = height;
    }

    @Getter
    @Nullable
    private final Integer _height;

    @Getter
    @Nullable
    private final Integer _length;

    @Getter
    @Nullable
    private final Integer _width;
}
