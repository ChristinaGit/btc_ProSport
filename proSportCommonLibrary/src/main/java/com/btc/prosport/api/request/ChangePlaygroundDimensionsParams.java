package com.btc.prosport.api.request;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangePlaygroundDimensionsParams {

    public ChangePlaygroundDimensionsParams(
        @Nullable final Integer width,
        @Nullable final Integer length,
        @Nullable final Integer height) {
        _width = width;
        _height = height;
        _length = length;
    }

    @Getter
    @Nullable
    @SerializedName("height")
    private final Integer _height;

    @Getter
    @Nullable
    @SerializedName("length")
    private final Integer _length;

    @Getter
    @Nullable
    @SerializedName("width")
    private final Integer _width;
}
