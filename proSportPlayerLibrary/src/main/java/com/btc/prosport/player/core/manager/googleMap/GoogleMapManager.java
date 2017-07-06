package com.btc.prosport.player.core.manager.googleMap;

import android.support.annotation.NonNull;

import rx.Observable;

import com.btc.common.control.manager.permission.CheckPermissionsResult;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public interface GoogleMapManager {
    @NonNull
    CheckPermissionsResult checkPlaygroundsMapPermissions();

    @NonNull
    Observable<GoogleMap> getGoogleMap();

    @NonNull
    List<String> getGoogleMapRequiredPermissions();

    @NonNull
    Observable<GoogleMap> getGoogleMapWithPermissions(boolean allowAskPermissions);
}
