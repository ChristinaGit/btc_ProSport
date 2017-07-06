package com.btc.prosport.player.core.manager.googleApiClient;

import android.Manifest;
import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.Collection;
import java.util.Collections;

@Accessors(prefix = "_")
public enum GoogleApiMethod {
    //@formatter:off
    LAST_LOCATION(
        Collections.<Api<? extends Api.ApiOptions.NotRequiredOptions>>singletonList(LocationServices.API),
        Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION)),
    GET_PLACES(
        Collections.<Api<? extends Api.ApiOptions.NotRequiredOptions>>singletonList(Places.GEO_DATA_API),
        Collections.<String>emptyList());
    //@formatter:on

    @Getter
    @NonNull
    private final Collection<Api<? extends Api.ApiOptions.NotRequiredOptions>> _requiredApis;

    @Getter
    @NonNull
    private final Collection<String> _requiredPermissions;

    GoogleApiMethod(
        @NonNull final Collection<Api<? extends Api.ApiOptions.NotRequiredOptions>> requiredApis,
        @NonNull final Collection<String> requiredPermissions) {
        Contracts.requireNonNull(requiredApis, "requiredApis == null");
        Contracts.requireNonNull(requiredPermissions, "requiredPermissions == null");

        _requiredApis = requiredApis;
        _requiredPermissions = requiredPermissions;
    }
}
