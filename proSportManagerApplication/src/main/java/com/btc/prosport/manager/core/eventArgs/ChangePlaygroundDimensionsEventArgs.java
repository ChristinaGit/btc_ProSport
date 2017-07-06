package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.extension.eventArgs.IdEventArgs;

@Accessors(prefix = "_")
public class ChangePlaygroundDimensionsEventArgs extends IdEventArgs {
    public ChangePlaygroundDimensionsEventArgs(
        final long playgroundId,
        @Nullable final Integer width,
        @Nullable final Integer height,
        @Nullable final Integer length) {
        super(playgroundId);

        _width = width;
        _height = height;
        _length = length;
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