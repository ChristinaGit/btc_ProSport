package com.btc.prosport.player.presenter;

import android.location.Location;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.extension.exception.InsufficientPermissionException;
import com.btc.common.utility.tuple.Tuple2;
import com.btc.common.utility.tuple.Tuples;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.SportComplexLocation;
import com.btc.prosport.api.model.entity.PageEntity;
import com.btc.prosport.api.model.entity.SportComplexLocationEntity;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;
import com.btc.prosport.player.core.manager.googleApiClient.GoogleApiManager;
import com.btc.prosport.player.core.manager.googleMap.GoogleMapManager;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.core.manager.render.SportComplexInfoRenderer;
import com.btc.prosport.player.screen.SportComplexesMapScreen;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

@Accessors(prefix = "_")
public final class SportComplexesMapPresenter
    extends BaseSportComplexesViewerPagePresenter<SportComplexesMapScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexesMapPresenter.class);

    public SportComplexesMapPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final GoogleApiManager googleApiManager,
        @Nullable final GoogleMapManager googleMapManager,
        @Nullable final SportComplexInfoRenderer sportComplexInfoRenderer) {
        super(
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null"),
            Contracts.requireNonNull(googleApiManager, "googleApiManager == null"));
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _proSportNavigationManager = proSportNavigationManager;
        _googleMapManager = googleMapManager;
        _sportComplexInfoRenderer = sportComplexInfoRenderer;
    }

    protected void displayInsufficientMapPermissionsError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayInsufficientMapPermissionsError();
        }
    }

    protected void displaySportComplexes(
        @NonNull final GoogleMap googleMap,
        @Nullable final List<SportComplexLocation> sportComplexes,
        final boolean lastPage) {
        Contracts.requireNonNull(googleMap, "googleMap == null");

        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexes(googleMap, sportComplexes, lastPage);
        }
    }

    protected void displaySportComplexesPage(
        @NonNull final GoogleMap googleMap,
        @Nullable final List<SportComplexLocation> sportComplexes,
        final boolean lastPage) {
        Contracts.requireNonNull(googleMap, "googleMap == null");

        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexesPage(googleMap, sportComplexes, lastPage);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final SportComplexesMapScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getRetryViewSportComplexesEvent().addHandler(_retryViewSportComplexesHandler);
        screen.getViewSportComplexInfoEvent().addHandler(_viewSportComplexInfoHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final SportComplexesMapScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getRetryViewSportComplexesEvent().removeHandler(_retryViewSportComplexesHandler);
        screen.getViewSportComplexInfoEvent().removeHandler(_viewSportComplexInfoHandler);
    }

    @Override
    protected void performLoadSportComplexesPage(
        final int page,
        @Nullable final String sportComplexNameSearchParams,
        @Nullable final Long dateSearchParams,
        @Nullable final Long startTimeSearchParams,
        @Nullable final Long endTimeSearchParams,
        @Nullable final SportComplexesFilter sportComplexesFilter,
        @Nullable final SportComplexesSortOrder sportComplexesSortOrder) {
        final val googleMapManager = getGoogleMapManager();
        if (googleMapManager != null) {
            final val proSportApiManager = getProSportApiManager();
            final val proSportApi = proSportApiManager.getProSportApi();

            final boolean favorites = sportComplexesFilter == SportComplexesFilter.FAVORITES;
            final val order =
                getSportComplexesSortOrderQueryParam(SportComplexesSortOrder.DISTANCE);

            final val rxManager = getRxManager();

            final val dateParam =
                dateSearchParams == null ? null : ProSportApiDataUtils.formatDate(dateSearchParams);

            final val startTimeParam = startTimeSearchParams == null
                                       ? null
                                       : ProSportApiDataUtils.formatTime(startTimeSearchParams);
            final val endTimeParam = endTimeSearchParams == null
                                     ? null
                                     : ProSportApiDataUtils.formatTime(endTimeSearchParams);

            final boolean allowAskPermissions =
                !_mapPermissionAsked && page == ProSportDataContract.FIRST_PAGE_INDEX;

            _mapPermissionAsked = true;

            googleMapManager
                .getGoogleMapWithPermissions(allowAskPermissions)
                .flatMap(new Func1<GoogleMap, Observable<Tuple2<Location, GoogleMap>>>() {
                    @Override
                    public Observable<Tuple2<Location, GoogleMap>> call(final GoogleMap googleMap) {
                        return getGoogleApiManager()
                            .getLastLocation(false)
                            .map(new Func1<Location, Tuple2<Location, GoogleMap>>() {
                                @Override
                                public Tuple2<Location, GoogleMap> call(final Location location) {
                                    return Tuples.from(location, googleMap);
                                }
                            });
                    }
                })
                .doOnNext(new Action1<Tuple2<Location, GoogleMap>>() {
                    @Override
                    public void call(final Tuple2<Location, GoogleMap> arg) {
                        Contracts.requireMainThread();

                        final val location = arg.get1();
                        final val googleMap = arg.get2();

                        Contracts.requireNonNull(googleMap, "googleMap == null");

                        if (location != null) {
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();

                            final val coordinates = new LatLng(latitude, longitude);
                            final val cameraUpdate =
                                CameraUpdateFactory.newLatLngZoom(coordinates, 12);
                            googleMap.animateCamera(cameraUpdate, 3500, null);
                        }
                    }
                })
                .observeOn(rxManager.getIOScheduler())
                .flatMap(new Func1<Tuple2<Location, GoogleMap>, Observable<Tuple2<GoogleMap,
                    PageEntity<SportComplexLocationEntity>>>>() {
                    @Override
                    public Observable<Tuple2<GoogleMap, PageEntity<SportComplexLocationEntity>>>
                    call(
                        final Tuple2<Location, GoogleMap> arg) {
                        Contracts.requireWorkerThread();

                        final val location = arg.get1();
                        final val googleMap = arg.get2();

                        Contracts.requireNonNull(googleMap, "googleMap == null");

                        final String locationParam;
                        if (location != null) {
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();
                            locationParam =
                                ProSportApiDataUtils.makeLocationParam(latitude, longitude);
                        } else {
                            locationParam = null;
                        }

                        return getProSportAccountHelper()
                            .withAccessToken(new Func1<String,
                                Observable<PageEntity<SportComplexLocationEntity>>>() {
                                @Override
                                public Observable<PageEntity<SportComplexLocationEntity>> call(
                                    final String token) {
                                    final val playgrounds = proSportApi.getSportComplexesLocationsPages(
                                        page,
                                        sportComplexNameSearchParams,
                                        startTimeParam,
                                        endTimeParam,
                                        dateParam,
                                        locationParam,
                                        order,
                                        favorites,
                                        token);
                                    return rxManager.autoManage(playgrounds);
                                }
                            }, false)
                            .flatMap(new Func1<PageEntity<SportComplexLocationEntity>,
                                Observable<Tuple2<GoogleMap,
                                    PageEntity<SportComplexLocationEntity>>>>() {
                                @Override
                                public Observable<Tuple2<GoogleMap,
                                    PageEntity<SportComplexLocationEntity>>> call(
                                    final PageEntity<SportComplexLocationEntity>
                                        playgroundsResponse) {
                                    return Observable.just(Tuples.from(googleMap,
                                                                       playgroundsResponse));
                                }
                            });
                    }
                })
                .observeOn(rxManager.getUIScheduler())
                .subscribe(new Action1<Tuple2<GoogleMap, PageEntity<SportComplexLocationEntity>>>
                    () {
                    @Override
                    public void call(final Tuple2<GoogleMap,
                        PageEntity<SportComplexLocationEntity>> arg) {
                        Contracts.requireMainThread();

                        final val googleMap = arg.get1();
                        final val response = arg.get2();

                        Contracts.requireNonNull(googleMap, "googleMap == null");
                        Contracts.requireNonNull(response, "response == null");

                        final val sportComplexes =
                            (List<SportComplexLocation>) (List<? extends SportComplexLocation>)
                                response
                                .getEntries();
                        if (page == ProSportDataContract.FIRST_PAGE_INDEX) {
                            displaySportComplexes(googleMap,
                                                  sportComplexes,
                                                  response.getNextPageUri() == null);
                        } else {
                            displaySportComplexesPage(googleMap,
                                                      sportComplexes,
                                                      response.getNextPageUri() == null);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable error) {
                        Contracts.requireMainThread();

                        Log.w(_LOG_TAG, "Failed to load sport complexes. Page: " + page, error);

                        if (error instanceof InsufficientPermissionException) {
                            displayInsufficientMapPermissionsError();
                        } else {
                            getMessageManager().showInfoMessage(R.string
                                                                    .sport_complexes_list_load_fail);
                            displaySportComplexesLoadingError();
                        }
                    }
                });
        } else {
            throw new IllegalStateException(GoogleMapManager.class.getSimpleName() + " is null.");
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private final GoogleMapManager _googleMapManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _proSportNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private final SportComplexInfoRenderer _sportComplexInfoRenderer;

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexInfoHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val playgroundInfoRenderer = getSportComplexInfoRenderer();
                if (playgroundInfoRenderer != null) {
                    playgroundInfoRenderer.renderSportComplexInfo(eventArgs.getId());
                }
            }
        };

    private boolean _mapPermissionAsked = false;

    @NonNull
    private final EventHandler<SportComplexesSearchEventArgs> _retryViewSportComplexesHandler =
        new EventHandler<SportComplexesSearchEventArgs>() {
            @Override
            public void onEvent(@NonNull final SportComplexesSearchEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val googleMapManager = getGoogleMapManager();
                if (googleMapManager != null) {
                    final val checkPermissionsResult =
                        googleMapManager.checkPlaygroundsMapPermissions();

                    if (!checkPermissionsResult.getNeverAskAgainPermissions().isEmpty()) {
                        //@formatter:off
                        if (!getProSportNavigationManager().navigateToApplicationSettings()) {
                            getMessageManager()
                                .showErrorMessage(R.string
                                                      .message_error_settings_not_found_allow_permission_manual);
                        }
                        //@formatter:on
                    } else {
                        resetCurrentSportComplexesPage();

                        displaySportComplexesLoading();
                        performLoadSportComplexesPage(ProSportDataContract.FIRST_PAGE_INDEX,
                                                      eventArgs.getNameSearchParams(),
                                                      eventArgs.getDateSearchParams(),
                                                      eventArgs.getStartTimeSearchParams(),
                                                      eventArgs.getEndTimeSearchParams(),
                                                      eventArgs.getSportComplexesFilter(),
                                                      eventArgs.getSportComplexesSortOrder());
                    }
                }
            }
        };
}
