package com.btc.prosport.player.core.manager.googleApiClient;

import android.location.Location;
import android.support.annotation.NonNull;

import rx.Observable;

import com.btc.prosport.player.core.PlaygroundPlaceInfo;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

public interface GoogleApiManager {
    @NonNull
    Observable<GoogleApiClient> getGoogleApiClient();

    @NonNull
    Observable<Location> getLastLocation(boolean allowAskPermissions);

    @NonNull
    Observable<List<PlaygroundPlaceInfo>> getPlaces(@NonNull final List<String> placesIds);

    boolean isApiMethodEnabled(@NonNull GoogleApiMethod method);
}
