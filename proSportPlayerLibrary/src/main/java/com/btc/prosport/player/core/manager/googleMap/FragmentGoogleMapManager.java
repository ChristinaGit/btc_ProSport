package com.btc.prosport.player.core.manager.googleMap;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.permission.CheckPermissionsResult;
import com.btc.common.control.manager.permission.PermissionManager;
import com.btc.common.control.manager.permission.RequestPermissionsResult;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.common.extension.fragment.ObservableFragment;
import com.btc.prosport.player.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Collections;
import java.util.List;

@Accessors(prefix = "_")
public class FragmentGoogleMapManager implements GoogleMapManager {
    public FragmentGoogleMapManager(
        @IdRes final int mapViewId,
        @NonNull final ObservableFragment observableFragment,
        @NonNull final PermissionManager permissionManager) {
        Contracts.requireNonNull(observableFragment, "observableFragment == null");
        Contracts.requireNonNull(permissionManager, "permissionManager == null");

        _mapViewId = mapViewId;
        _observableFragment = observableFragment;
        _permissionManager = permissionManager;
    }

    @Override
    @NonNull
    public final CheckPermissionsResult checkPlaygroundsMapPermissions() {
        final val permissions = Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION);
        return getPermissionManager().checkPermissions(permissions);
    }

    @NonNull
    @Override
    public Observable<GoogleMap> getGoogleMap() {
        return Observable.create(new Observable.OnSubscribe<GoogleMap>() {
            @Override
            public void call(final Subscriber<? super GoogleMap> subscriber) {
                Contracts.requireMainThread();

                try {
                    final val mapView = getMapView();
                    if (mapView != null) {
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(final GoogleMap googleMap) {
                                subscriber.onNext(googleMap);
                                subscriber.onCompleted();
                            }
                        });
                    } else {
                        subscriber.onError(new IllegalStateException("MapView is not found."));
                    }
                } catch (final Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @NonNull
    @Override
    public List<String> getGoogleMapRequiredPermissions() {
        return Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    @NonNull
    public final Observable<GoogleMap> getGoogleMapWithPermissions(
        final boolean allowAskPermissions) {
        if (allowAskPermissions) {
            return requestPlaygroundsMapPermissions().flatMap(new Func1<RequestPermissionsResult,
                Observable<GoogleMap>>() {
                @Override
                public Observable<GoogleMap> call(final RequestPermissionsResult result) {
                    if (result.isAllGranted()) {
                        return getGoogleMap();
                    } else {
                        final val error =
                            new InsufficientPermissionException(result.getDeniedPermissions());
                        return Observable.error(error);
                    }
                }
            });
        } else {
            final val checkResult = checkPlaygroundsMapPermissions();
            if (checkResult.isAllGranted()) {
                return getGoogleMap();
            } else {
                final val error =
                    new InsufficientPermissionException(checkResult.getDeniedPermissions());
                return Observable.error(error);
            }
        }
    }

    @NonNull
    protected final Activity getActivity() {
        return getFragment().getActivity();
    }

    @NonNull
    protected final Fragment getFragment() {
        return getObservableFragment().asFragment();
    }

    @Nullable
    protected final MapView getMapView() {
        final MapView mapView;

        final val fragmentView = getFragment().getView();
        if (fragmentView != null) {
            mapView = (MapView) fragmentView.findViewById(getMapViewId());
        } else {
            mapView = null;
        }

        return mapView;
    }

    @NonNull
    protected final Observable<RequestPermissionsResult> requestPlaygroundsMapPermissions() {
        final val context = getActivity();
        final val accessFineLocationExplanation =
            context.getString(R.string
                                  .sport_complexes_map_permission_access_fine_location_explanation);
        return getPermissionManager()
            .createPermissionsRequest()
            .addPermission(Manifest.permission.ACCESS_FINE_LOCATION, accessFineLocationExplanation)
            .show();
    }

    @Getter(AccessLevel.PROTECTED)
    @IdRes
    private final int _mapViewId;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ObservableFragment _observableFragment;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PermissionManager _permissionManager;
}
