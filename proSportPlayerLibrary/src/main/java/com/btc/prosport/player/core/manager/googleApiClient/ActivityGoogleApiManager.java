package com.btc.prosport.player.core.manager.googleApiClient;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.permission.PermissionManager;
import com.btc.common.control.manager.permission.RequestPermissionsResult;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.player.core.PlaygroundPlaceInfo;
import com.btc.prosport.player.core.utility.SportComplexUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Accessors(prefix = "_")
public class ActivityGoogleApiManager implements GoogleApiManager {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ActivityGoogleApiManager.class);

    public ActivityGoogleApiManager(
        @NonNull final ObservableActivity observableActivity,
        @NonNull final RxManager rxManager,
        @NonNull final PermissionManager permissionManager,
        @NonNull final Collection<GoogleApiMethod> googleApiMethods) {
        Contracts.requireNonNull(observableActivity, "observableActivity == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(permissionManager, "permissionManager == null");
        Contracts.requireNonNull(googleApiMethods, "googleApiMethods == null");

        _observableActivity = observableActivity;
        _rxManager = rxManager;
        _permissionManager = permissionManager;
        _googleApiMethods = googleApiMethods;

        final val builder = new GoogleApiClient.Builder(getActivity());

        final val apis = new HashSet<Api<? extends Api.ApiOptions.NotRequiredOptions>>();
        for (final val googleApiMethod : getGoogleApiMethods()) {
            apis.addAll(googleApiMethod.getRequiredApis());
        }

        for (final val api : apis) {
            builder.addApi(api);
        }

        _googleApiClient = builder.enableAutoManage(getActivity(), null).build();
    }

    @NonNull
    @Override
    public Observable<GoogleApiClient> getGoogleApiClient() {
        return Observable.create(new ProvideGoogleApiClientOnSubscribe());
    }

    @NonNull
    @Override
    public Observable<Location> getLastLocation(final boolean allowAskPermissions) {
        final Observable<Location> result;

        if (allowAskPermissions) {
            result = getPermissionManager()
                .createPermissionsRequest()
                .addPermissions(GoogleApiMethod.LAST_LOCATION.getRequiredPermissions())
                .show()
                .flatMap(new Func1<RequestPermissionsResult, Observable<Location>>() {
                    @Override
                    public Observable<Location> call(
                        final RequestPermissionsResult permissionsResult) {
                        final Observable<Location> result;

                        if (permissionsResult.isAllGranted()) {
                            result = getLastLocation();
                        } else {
                            result = Observable.just(null);
                        }

                        return result;
                    }
                });
        } else {
            result = getLastLocation();
        }

        return result;
    }

    @NonNull
    @Override
    public Observable<List<PlaygroundPlaceInfo>> getPlaces(@NonNull final List<String> placesIds) {
        Contracts.requireNonNull(placesIds, "placesIds == null");
        final Observable<List<PlaygroundPlaceInfo>> result;

        if (isApiMethodEnabled(GoogleApiMethod.GET_PLACES)) {
            return getGoogleApiClient()
                .observeOn(getRxManager().getIOScheduler())
                .map(new Func1<GoogleApiClient, List<PlaygroundPlaceInfo>>() {
                    @Override
                    public List<PlaygroundPlaceInfo> call(final GoogleApiClient client) {
                        Contracts.requireWorkerThread();

                        final val placeInfos = new ArrayList<PlaygroundPlaceInfo>();

                        PlaceBuffer placeBuffer = null;
                        try {
                            placeBuffer = Places.GeoDataApi
                                .getPlaceById(client,
                                              placesIds.toArray(new String[placesIds.size()]))
                                .await();

                            final int placesCount = placeBuffer.getCount();
                            for (int i = 0; i < placesCount; i++) {
                                final val place = placeBuffer.get(i);
                                final val playgroundPlaceInfo =
                                    SportComplexUtils.getPlaygroundPlaceInfo(place);

                                placeInfos.add(playgroundPlaceInfo);
                            }
                        } finally {
                            if (placeBuffer != null) {
                                placeBuffer.release();
                            }
                        }

                        return placeInfos;
                    }
                });
        } else {
            result = Observable.error(new GoogleApiMethodDisabledException());
        }

        return result;
    }

    @Override
    public boolean isApiMethodEnabled(@NonNull final GoogleApiMethod method) {
        Contracts.requireNonNull(method, "method == null");

        return getGoogleApiMethods().contains(method);
    }

    @NonNull
    protected final AppCompatActivity getActivity() {
        return getObservableActivity().asActivity();
    }

    @NonNull
    protected final Observable<Location> getLastLocation() {
        final Observable<Location> result;

        if (isApiMethodEnabled(GoogleApiMethod.LAST_LOCATION)) {
            result = getGoogleApiClient().map(new Func1<GoogleApiClient, Location>() {
                @Override
                public Location call(final GoogleApiClient googleApiClient) {
                    return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                }
            });
        } else {
            result = Observable.error(new GoogleApiMethodDisabledException());
        }

        return result;
    }

    @NonNull
    private final GoogleApiClient _googleApiClient;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Collection<GoogleApiMethod> _googleApiMethods;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ObservableActivity _observableActivity;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PermissionManager _permissionManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    private final class ProvideGoogleApiClientOnSubscribe
        implements Observable.OnSubscribe<GoogleApiClient>, GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void call(final Subscriber<? super GoogleApiClient> subscriber) {
            _subscriber = subscriber;

            if (_googleApiClient.isConnected()) {
                subscriber.onNext(_googleApiClient);
                subscriber.onCompleted();
            } else {
                _googleApiClient.registerConnectionCallbacks(this);
                _googleApiClient.registerConnectionFailedListener(this);
            }
        }

        @Override
        public void onConnected(@Nullable final Bundle bundle) {
            if (_subscriber != null) {
                _subscriber.onNext(_googleApiClient);
                _subscriber.onCompleted();
            }

            _googleApiClient.unregisterConnectionCallbacks(this);
        }

        @Override
        public void onConnectionSuspended(final int i) {
            if (_subscriber != null) {
                _subscriber.onError(new RuntimeException());
            }

            _googleApiClient.unregisterConnectionCallbacks(this);
        }

        @Override
        public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
            if (_subscriber != null) {
                _subscriber.onError(new RuntimeException());
            }

            _googleApiClient.unregisterConnectionFailedListener(this);
        }

        @Nullable
        private Subscriber<? super GoogleApiClient> _subscriber;
    }
}
