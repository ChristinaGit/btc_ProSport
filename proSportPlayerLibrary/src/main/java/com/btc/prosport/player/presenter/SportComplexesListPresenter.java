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
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.SportComplexPreview;
import com.btc.prosport.api.model.entity.PageEntity;
import com.btc.prosport.api.model.entity.SportComplexPreviewEntity;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;
import com.btc.prosport.player.core.manager.googleApiClient.GoogleApiManager;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.SportComplexesListScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class SportComplexesListPresenter
    extends BaseSportComplexesViewerPagePresenter<SportComplexesListScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(SportComplexesListPresenter.class);

    public SportComplexesListPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final GoogleApiManager googleApiManager) {
        super(
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null"),
            Contracts.requireNonNull(googleApiManager, "googleApiManager == null"));
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
    }

    protected void displaySportComplexes(
        @Nullable final List<SportComplexPreview> sportComplexes, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexes(sportComplexes, lastPage);
        }
    }

    protected void displaySportComplexesPage(
        @Nullable final List<SportComplexPreview> sportComplexes, final boolean lastPage) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexesPage(sportComplexes, lastPage);
        }
    }

    @CallSuper
    @Override
    protected void onScreenCreate(@NonNull final SportComplexesListScreen screen) {
        super.onScreenCreate(Contracts.requireNonNull(screen, "screen == null"));

        _lastLocationPermissionsAsked = false;
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
        final boolean favorites = sportComplexesFilter == SportComplexesFilter.FAVORITES;
        final val order = sportComplexesSortOrder == null
                          ? null
                          : getSportComplexesSortOrderQueryParam(sportComplexesSortOrder);

        final val dateParam =
            dateSearchParams == null ? null : ProSportApiDataUtils.formatDate(dateSearchParams);

        final val startTimeParam = startTimeSearchParams == null
                                   ? null
                                   : ProSportApiDataUtils.formatTime(startTimeSearchParams);
        final val endTimeParam = endTimeSearchParams == null
                                 ? null
                                 : ProSportApiDataUtils.formatTime(endTimeSearchParams);

        final boolean allowAskPermissions =
            !_lastLocationPermissionsAsked && page == ProSportDataContract.FIRST_PAGE_INDEX;

        _lastLocationPermissionsAsked = true;

        // TODO: 02.03.2017 Fix double update when permissions asked
        final val rxManager = getRxManager();
        getGoogleApiManager()
            .getLastLocation(allowAskPermissions)
            .onErrorReturn(new Func1<Throwable, Location>() {
                @Override
                public Location call(final Throwable error) {
                    return null;
                }
            })
            .observeOn(rxManager.getIOScheduler())
            .flatMap(new Func1<Location, Observable<PageEntity<SportComplexPreviewEntity>>>() {
                @Override
                public Observable<PageEntity<SportComplexPreviewEntity>> call(
                    final Location location) {
                    Contracts.requireWorkerThread();

                    final val proSportApiManager = getProSportApiManager();
                    final val proSportApi = proSportApiManager.getProSportApi();

                    final String locationParam;
                    if (location != null) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        locationParam = ProSportApiDataUtils.makeLocationParam(latitude, longitude);
                    } else {
                        locationParam = null;
                    }

                    return getProSportAccountHelper().withAccessToken(new Func1<String,
                        Observable<PageEntity<SportComplexPreviewEntity>>>() {
                        @Override
                        public Observable<PageEntity<SportComplexPreviewEntity>> call(
                            final String token) {
                            final val sportComplexes = proSportApi.getSportComplexesPreviewsPages(
                                page,
                                sportComplexNameSearchParams,
                                startTimeParam,
                                endTimeParam,
                                dateParam,
                                locationParam,
                                order,
                                favorites,
                                token);
                            return rxManager.autoManage(sportComplexes);
                        }
                    }, false);
                }
            })
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<PageEntity<SportComplexPreviewEntity>>() {
                @Override
                public void call(final PageEntity<SportComplexPreviewEntity> pageEntity) {
                    Contracts.requireMainThread();

                    final val sportComplexes =
                        (List<SportComplexPreview>) (List<? extends SportComplexPreview>) pageEntity
                            .getEntries();
                    if (page == ProSportDataContract.FIRST_PAGE_INDEX) {
                        displaySportComplexes(sportComplexes, pageEntity.getNextPageUri() == null);
                    } else {
                        displaySportComplexesPage(sportComplexes,
                                                  pageEntity.getNextPageUri() == null);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load sport complexes. Page: " + page, error);

                    getMessageManager().showInfoMessage(R.string.sport_complexes_list_load_fail);
                    displaySportComplexesLoadingError();
                }
            });
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    private boolean _lastLocationPermissionsAsked = false;
}
