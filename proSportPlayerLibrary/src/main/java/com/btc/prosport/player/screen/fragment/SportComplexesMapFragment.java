package com.btc.prosport.player.screen.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.delegate.loading.FadeVisibilityHandler;
import com.btc.common.extension.delegate.loading.LoadingViewDelegate;
import com.btc.common.extension.delegate.loading.ProgressVisibilityHandler;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.extension.view.ContentLoaderProgressBar;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.model.SportComplexLocation;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;
import com.btc.prosport.player.core.manager.plugin.GoogleMapPlugin;
import com.btc.prosport.player.core.utility.SportComplexUtils;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.di.subscreen.module.PlayerManagerSubscreenScreenModule;
import com.btc.prosport.player.screen.SportComplexesMapScreen;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Accessors(prefix = "_")
public final class SportComplexesMapFragment extends BaseSportComplexesViewerPageFragment
    implements GoogleMap.OnMarkerClickListener, SportComplexesMapScreen {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SportComplexesMapFragment.class);

    @Override
    public final void displayInsufficientMapPermissionsError() {
        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
        if (_mapInsufficientPermissionContainerView != null) {
            _mapInsufficientPermissionContainerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public final void displaySportComplexes(
        @NonNull final GoogleMap googleMap,
        @Nullable final List<SportComplexLocation> sportComplexes,
        final boolean lastPage) {
        Contracts.requireNonNull(googleMap, "googleMap == null");

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //noinspection MissingPermission
        googleMap.setMyLocationEnabled(true);

        _sportComplexes = sportComplexes;

        googleMap.clear();

        if (sportComplexes != null) {
            addSportComplexesMarkers(googleMap, sportComplexes);
        }

        googleMap.setOnMarkerClickListener(this);

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showContent(sportComplexes != null && !sportComplexes.isEmpty());
        }
    }

    @Override
    public void displaySportComplexesPage(
        @NonNull final GoogleMap googleMap,
        @Nullable final List<SportComplexLocation> sportComplexes,
        final boolean lastPage) {
        if (sportComplexes != null) {
            if (_sportComplexes != null) {
                _sportComplexes.addAll(sportComplexes);
            } else {
                _sportComplexes = sportComplexes;
            }

            // TODO: 04.05.2017 Optimize page loading for map
            addSportComplexesMarkers(googleMap, sportComplexes);
        }
    }

    @NonNull
    @Override
    public final Event<SportComplexesSearchEventArgs> getRetryViewSportComplexesEvent() {
        return _retryViewSportComplexesEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexInfoEvent() {
        return _viewSportComplexInfoEvent;
    }

    @Override
    public final void displaySportComplexesLoading() {
        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.showLoading();
        }
    }

    @Override
    public final void displaySportComplexesLoadingError() {
        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            final val sportComplexes = getSportComplexes();
            if (sportComplexes != null && sportComplexes.isEmpty()) {
                loadingViewDelegate.showError();
            } else {
                loadingViewDelegate.showContent();
            }
        }
    }

    @NonNull
    @Override
    public final Event<SportComplexesSearchEventArgs> getRefreshSportComplexesEvent() {
        return _refreshSportComplexesEvent;
    }

    @NonNull
    @Override
    public final Event<SportComplexesSearchEventArgs> getViewNextSportComplexesPageEvent() {
        return _viewNextSportComplexesPageEvent;
    }

    @NonNull
    @Override
    public final Event<IdEventArgs> getViewSportComplexDetailsEvent() {
        return _viewSportComplexDetailsEvent;
    }

    @NonNull
    @Override
    public final Event<SportComplexesSearchEventArgs> getViewSportComplexesEvent() {
        return _viewSportComplexesEvent;
    }

    @CallSuper
    @Override
    public void bindViews(@NonNull final View view) {
        super.bindViews(view);

        _sportComplexesMapView = (MapView) view.findViewById(R.id.sport_complexes_map);
        _sportComplexesNoContentView = view.findViewById(R.id.no_content);
        _sportComplexesLoadingView = (ContentLoaderProgressBar) view.findViewById(R.id.loading);
        _sportComplexesLoadingErrorView = view.findViewById(R.id.loading_error);

        _mapInsufficientPermissionContainerView =
            view.findViewById(R.id.map_insufficient_permission_container);
        _mapRequestPermissionsView = view.findViewById(R.id.map_request_permissions);
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onStart();
        }
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onSaveInstanceState(outState);
        }
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbindViews();
    }

    @CallSuper
    @Override
    protected void onReleaseInjectedMembers() {
        super.onReleaseInjectedMembers();

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.unbindScreen();
        }
    }

    @Override
    public boolean hasContent() {
        final val sportComplexes = getSportComplexes();
        return sportComplexes != null && !sportComplexes.isEmpty();
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(
        final LayoutInflater inflater,
        @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final val view = inflater.inflate(R.layout.fragment_sport_complexes_map, container, false);

        bindViews(view);

        MapsInitializer.initialize(getContext());

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onCreate(savedInstanceState);
        }

        if (_mapRequestPermissionsView != null) {
            _mapRequestPermissionsView.setOnClickListener(_controlsClickListener);
        }

        //@formatter:off
        _sportComplexesLoadingViewDelegate = LoadingViewDelegate
            .builder()
            .setContentView(_sportComplexesMapView)
            .setLoadingView(_sportComplexesLoadingView)
            .setNoContentView(_sportComplexesNoContentView)
            .setErrorView(_sportComplexesLoadingErrorView)
            .setContentVisibilityHandler(new FadeVisibilityHandler(R.anim.fade_in_long,
                                                                   FadeVisibilityHandler.NO_ANIMATION))
            .setLoadingVisibilityHandler(new ProgressVisibilityHandler())
            .setVisibilityChangedListener(new LoadingViewDelegate.VisibilityChangedListener() {
                @Override
                public void onVisibilityChanged() {
                    final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
                    if (loadingViewDelegate != null) {
                        if (loadingViewDelegate.isVisible()) {
                            if (_mapInsufficientPermissionContainerView != null) {
                                _mapInsufficientPermissionContainerView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            })
            .build();
        //@formatter:on

        return view;
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onStop();
        }
    }

    @CallSuper
    @Override
    protected void onEnterPage() {
        super.onEnterPage();

        _viewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
    }

    @CallSuper
    @Override
    protected void onLeavePage() {
        super.onLeavePage();

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
    }

    @CallSuper
    @Override
    protected void onSportComplexSearchParamsChangedWithInputDelay() {
        super.onSportComplexSearchParamsChangedWithInputDelay();

        if (isResumed() && isPageActive()) {
            _viewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
        }
    }

    @CallSuper
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onLowMemory();
        }
    }

    @CallSuper
    @Override
    public boolean onMarkerClick(final Marker marker) {
        final boolean consumed;

        final val tag = marker.getTag();
        if (tag instanceof SportComplexLocation) {
            consumed = false;

            final val sportComplex = (SportComplexLocation) tag;

            _viewSportComplexInfoEvent.rise(new IdEventArgs(sportComplex.getId()));
        } else {
            consumed = false;
        }

        return consumed;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onResume();
        }

        if (isPageActive()) {
            _viewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onPause();
        }

        final val loadingViewDelegate = getSportComplexesLoadingViewDelegate();
        if (loadingViewDelegate != null) {
            loadingViewDelegate.hideAll();
        }
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_sportComplexesMapView != null) {
            _sportComplexesMapView.onDestroy();
        }
    }

    @CallSuper
    @Override
    protected void onConfigureManagerModule(
        @NonNull final PlayerManagerSubscreenScreenModule.Builder builder) {
        super.onConfigureManagerModule(Contracts.requireNonNull(builder, "builder == null"));

        builder.addGoogleMapPlugin(new GoogleMapPlugin(R.id.sport_complexes_map));
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getPlayerSubscreenComponent().inject(this);

        final val presenter = getPresenter();
        if (presenter != null) {
            presenter.bindScreen(this);
        }
    }

    @CallSuper
    protected void onMapRequestPermissionsClick() {
        _retryViewSportComplexesEvent.rise(getSportComplexesSearchEventArgs());
    }

    @Named(PresenterNames.SPORT_COMPLEXES_MAP)
    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Presenter<SportComplexesMapScreen> _presenter;

    private final ManagedEvent<SportComplexesSearchEventArgs> _refreshSportComplexesEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<SportComplexesSearchEventArgs> _retryViewSportComplexesEvent =
        Events.createEvent();

    @NonNull
    private final View.OnClickListener _controlsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final int id = v.getId();
            if (R.id.map_request_permissions == id) {
                onMapRequestPermissionsClick();
            }
        }
    };

    @NonNull
    private final ManagedEvent<SportComplexesSearchEventArgs> _viewNextSportComplexesPageEvent =
        Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexDetailsEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewSportComplexInfoEvent = Events.createEvent();

    @NonNull
    private final ManagedEvent<SportComplexesSearchEventArgs> _viewSportComplexesEvent =
        Events.createEvent();

    @Nullable
    private View _mapInsufficientPermissionContainerView;

    @Nullable
    private View _mapRequestPermissionsView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private List<SportComplexLocation> _sportComplexes;

    @Nullable
    private View _sportComplexesLoadingErrorView;

    @Nullable
    private ContentLoaderProgressBar _sportComplexesLoadingView;

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private LoadingViewDelegate _sportComplexesLoadingViewDelegate;

    @Nullable
    private MapView _sportComplexesMapView;

    @Nullable
    private View _sportComplexesNoContentView;

    private void addSportComplexesMarkers(
        @NonNull final GoogleMap googleMap,
        @NonNull final List<SportComplexLocation> sportComplexes) {
        Contracts.requireNonNull(googleMap, "googleMap == null");
        Contracts.requireNonNull(sportComplexes, "sportComplexes == null");

        if (!sportComplexes.isEmpty()) {
            final int markerHue = getResources().getInteger(R.integer.map_marker_hue);
            final val markerIcon = BitmapDescriptorFactory.defaultMarker(markerHue);
            for (final val sportComplex : sportComplexes) {
                final val coordinates = SportComplexUtils.getCoordinates(sportComplex);
                if (coordinates != null) {
                    final val markerOptions =
                        new MarkerOptions().position(coordinates).icon(markerIcon);

                    final val marker = googleMap.addMarker(markerOptions);
                    marker.setTag(sportComplex);
                }
            }
        }
    }
}