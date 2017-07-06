package com.btc.prosport.player.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.entity.SportComplexPreviewEntity;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.utility.ProSportApiDataUtils;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;
import com.btc.prosport.player.core.eventArgs.SportComplexesSearchEventArgs;
import com.btc.prosport.player.core.manager.googleApiClient.GoogleApiManager;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.SportComplexesViewerPageScreen;

@Accessors(prefix = "_")
public abstract class BaseSportComplexesViewerPagePresenter<TScreen extends
    SportComplexesViewerPageScreen>
    extends BasePresenter<TScreen> {
    protected BaseSportComplexesViewerPagePresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final GoogleApiManager googleApiManager) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(googleApiManager, "googleApiManager == null");

        _proSportAccountHelper = proSportAccountHelper;
        _proSportApiManager = proSportApiManager;
        _playerNavigationManager = playerNavigationManager;
        _googleApiManager = googleApiManager;
    }

    @NonNull
    protected final String getSportComplexesSortOrderQueryParam(
        @NonNull final SportComplexesSortOrder sportComplexesSortOrder) {
        Contracts.requireNonNull(sportComplexesSortOrder, "sportComplexesSortOrder == null");

        final String queryParam;

        //@formatter:off
        switch (sportComplexesSortOrder) {
            case PRICE_ASCENDING:
                queryParam =
                    ProSportApiDataUtils.makeQueryOrderParam(
                        SportComplexPreviewEntity.FIELD_MINIMUM_PRICE,
                        false);
                break;
            case PRICE_DESCENDING:
                queryParam =
                    ProSportApiDataUtils.makeQueryOrderParam(
                        SportComplexPreviewEntity.FIELD_MINIMUM_PRICE,
                        true);
                break;
            case NAME:
                queryParam =
                    ProSportApiDataUtils.makeQueryOrderParam(
                        SportComplexPreviewEntity.FIELD_NAME,
                        false);
                break;
            case DISTANCE:
                queryParam =
                    ProSportApiDataUtils.makeQueryOrderParam(
                        SportComplexPreviewEntity.FIELD_DISTANCE,
                        false);
                break;
            default: {
                throw new IllegalArgumentException(
                    "Unknown sport complexes order type:" + sportComplexesSortOrder);
            }
        }
        //@formatter:on

        return queryParam;
    }

    protected final void resetCurrentSportComplexesPage() {
        setCurrentSportComplexesPage(ProSportDataContract.FIRST_PAGE_INDEX);
    }

    protected void displaySportComplexesLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexesLoading();
        }
    }

    protected void displaySportComplexesLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySportComplexesLoadingError();
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final TScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        resetCurrentSportComplexesPage();

        screen.getViewSportComplexesEvent().addHandler(_viewSportComplexesHandler);
        screen.getViewNextSportComplexesPageEvent().addHandler(_viewNextSportComplexesPageHandler);
        screen.getRefreshSportComplexesEvent().addHandler(_refreshSportComplexesHandler);
        screen.getViewSportComplexDetailsEvent().addHandler(_viewSportComplexDetailsHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final TScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewSportComplexesEvent().removeHandler(_viewSportComplexesHandler);
        screen
            .getViewNextSportComplexesPageEvent()
            .removeHandler(_viewNextSportComplexesPageHandler);
        screen.getRefreshSportComplexesEvent().removeHandler(_refreshSportComplexesHandler);
        screen.getViewSportComplexDetailsEvent().removeHandler(_viewSportComplexDetailsHandler);
    }

    protected abstract void performLoadSportComplexesPage(
        final int page,
        @Nullable final String sportComplexNameSearchParams,
        @Nullable final Long dateSearchParams,
        @Nullable final Long startTimeSearchParams,
        @Nullable final Long endTimeSearchParams,
        @Nullable final SportComplexesFilter sportComplexesFilter,
        @Nullable final SportComplexesSortOrder sportComplexesSortOrder);

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final GoogleApiManager _googleApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final PlayerNavigationManager _playerNavigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @NonNull
    private final EventHandler<IdEventArgs> _viewSportComplexDetailsHandler =
        new EventHandler<IdEventArgs>() {
            @Override
            public void onEvent(@NonNull final IdEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                getPlayerNavigationManager().navigateToSportComplexDetails(eventArgs.getId());
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int _currentSportComplexesPage;

    @NonNull
    private final EventHandler<SportComplexesSearchEventArgs> _viewNextSportComplexesPageHandler =
        new EventHandler<SportComplexesSearchEventArgs>() {
            @Override
            public void onEvent(@NonNull final SportComplexesSearchEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                setCurrentSportComplexesPage(getCurrentSportComplexesPage() + 1);

                performLoadSportComplexesPage(
                    getCurrentSportComplexesPage(),
                    eventArgs.getNameSearchParams(),
                    eventArgs.getDateSearchParams(),
                    eventArgs.getStartTimeSearchParams(),
                    eventArgs.getEndTimeSearchParams(),
                    eventArgs.getSportComplexesFilter(),
                    eventArgs.getSportComplexesSortOrder());
            }
        };

    @NonNull
    private final EventHandler<SportComplexesSearchEventArgs> _refreshSportComplexesHandler =
        new EventHandler<SportComplexesSearchEventArgs>() {
            @Override
            public void onEvent(@NonNull final SportComplexesSearchEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                resetCurrentSportComplexesPage();
                performLoadSportComplexesPage(
                    ProSportDataContract.FIRST_PAGE_INDEX,
                    eventArgs.getNameSearchParams(),
                    eventArgs.getDateSearchParams(),
                    eventArgs.getStartTimeSearchParams(),
                    eventArgs.getEndTimeSearchParams(),
                    eventArgs.getSportComplexesFilter(),
                    eventArgs.getSportComplexesSortOrder());
            }
        };

    @NonNull
    private final EventHandler<SportComplexesSearchEventArgs> _viewSportComplexesHandler =
        new EventHandler<SportComplexesSearchEventArgs>() {
            @Override
            public void onEvent(@NonNull final SportComplexesSearchEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                resetCurrentSportComplexesPage();

                displaySportComplexesLoading();

                performLoadSportComplexesPage(
                    ProSportDataContract.FIRST_PAGE_INDEX,
                    eventArgs.getNameSearchParams(),
                    eventArgs.getDateSearchParams(),
                    eventArgs.getStartTimeSearchParams(),
                    eventArgs.getEndTimeSearchParams(),
                    eventArgs.getSportComplexesFilter(),
                    eventArgs.getSportComplexesSortOrder());
            }
        };
}
