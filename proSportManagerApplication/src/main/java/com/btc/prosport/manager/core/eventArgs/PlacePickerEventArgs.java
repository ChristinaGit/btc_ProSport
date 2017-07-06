package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;
import com.google.android.gms.maps.model.LatLngBounds;

@Accessors(prefix = "_")
public class PlacePickerEventArgs extends EventArgs {
    public PlacePickerEventArgs(@Nullable final LatLngBounds viewPort) {
        _viewPort = viewPort;
    }

    @Getter
    @Nullable
    private final LatLngBounds _viewPort;
}
