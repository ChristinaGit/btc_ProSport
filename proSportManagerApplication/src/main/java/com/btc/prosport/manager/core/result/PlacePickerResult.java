package com.btc.prosport.manager.core.result;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.android.gms.maps.model.LatLngBounds;

@Accessors(prefix = "_")
public class PlacePickerResult {
    public PlacePickerResult(
        @NonNull final String placeId,
        @NonNull final String address,
        @NonNull final Double latitude,
        @NonNull final Double longitude,
        @NonNull final LatLngBounds viewPort) {
        Contracts.requireNonNull(placeId, "placeId == null");
        Contracts.requireNonNull(address, "address == null");
        Contracts.requireNonNull(latitude, "latitude == null");
        Contracts.requireNonNull(longitude, "longitude == null");
        Contracts.requireNonNull(viewPort, "viewPort == null");

        _placeId = placeId;
        _address = address;
        _latitude = latitude;
        _longitude = longitude;
        _viewPort = viewPort;
    }

    @Getter
    @NonNull
    private final String _address;

    @Getter
    @NonNull
    private final Double _latitude;

    @Getter
    @NonNull
    private final Double _longitude;

    @Getter
    @NonNull
    private final String _placeId;

    @Getter
    @NonNull
    private final LatLngBounds _viewPort;
}
