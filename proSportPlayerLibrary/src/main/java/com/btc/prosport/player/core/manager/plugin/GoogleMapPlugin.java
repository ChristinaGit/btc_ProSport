package com.btc.prosport.player.core.manager.plugin;

import android.support.annotation.IdRes;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public final class GoogleMapPlugin {
    public GoogleMapPlugin(@IdRes final int mapViewId) {
        _mapViewId = mapViewId;
    }

    @Getter
    @IdRes
    private final int _mapViewId;
}
