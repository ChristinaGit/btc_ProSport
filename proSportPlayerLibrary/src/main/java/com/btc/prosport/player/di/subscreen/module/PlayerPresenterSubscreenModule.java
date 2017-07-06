package com.btc.prosport.player.di.subscreen.module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.btc.prosport.di.subscreen.module.ProSportPresenterSubscreenModule;
import com.btc.prosport.player.core.manager.googleApiClient.GoogleApiManager;
import com.btc.prosport.player.core.manager.googleMap.GoogleMapManager;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.core.manager.render.SportComplexInfoRenderer;
import com.btc.prosport.player.core.utility.PresenterHelper;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.presenter.IntervalsPresenter;
import com.btc.prosport.player.presenter.OrdersListPresenter;
import com.btc.prosport.player.presenter.PlaygroundsListPresenter;
import com.btc.prosport.player.presenter.SportComplexDetailPresenter;
import com.btc.prosport.player.presenter.SportComplexInfoPresenter;
import com.btc.prosport.player.presenter.SportComplexesListPresenter;
import com.btc.prosport.player.presenter.SportComplexesMapPresenter;
import com.btc.prosport.player.screen.IntervalsScreen;
import com.btc.prosport.player.screen.OrdersListScreen;
import com.btc.prosport.player.screen.PlaygroundsListScreen;
import com.btc.prosport.player.screen.SportComplexDetailScreen;
import com.btc.prosport.player.screen.SportComplexInfoScreen;
import com.btc.prosport.player.screen.SportComplexesListScreen;
import com.btc.prosport.player.screen.SportComplexesMapScreen;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@SubscreenScope
public class PlayerPresenterSubscreenModule extends ProSportPresenterSubscreenModule {
    @Named(PresenterNames.INTERVALS)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<IntervalsScreen> provideIntervalsPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PresenterHelper presenterHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(presenterHelper, "presenterHelper == null");

        return new IntervalsPresenter(rxManager,
                                      proSportApiManager,
                                      proSportAccountHelper,
                                      proSportAccountManager,
                                      messageManager,
                                      presenterHelper);
    }

    @Named(PresenterNames.ORDERS_LIST)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<OrdersListScreen> provideOrdersListPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        return new OrdersListPresenter(rxManager,
                                       messageManager,
                                       proSportApiManager,
                                       proSportAccountHelper);
    }

    @Named(PresenterNames.PLAYGROUNDS_LIST)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PlaygroundsListScreen> providePlaygroundsListPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        return new PlaygroundsListPresenter(messageManager, rxManager, proSportApiManager);
    }

    @Named(PresenterNames.SPORT_COMPLEX_DETAIL)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<SportComplexDetailScreen> provideSportComplexDetailPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PresenterHelper presenterHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(presenterHelper, "presenterHelper == null");

        return new SportComplexDetailPresenter(rxManager,
                                               messageManager,
                                               playerNavigationManager,
                                               proSportApiManager,
                                               presenterHelper);
    }

    @Named(PresenterNames.SPORT_COMPLEX_INFO)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<SportComplexInfoScreen> provideSportComplexInfoPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final ProSportApiManager proSportApiManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        return new SportComplexInfoPresenter(rxManager,
                                             messageManager,
                                             playerNavigationManager,
                                             proSportApiManager);
    }

    @Named(PresenterNames.SPORT_COMPLEXES_LIST)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<SportComplexesListScreen> provideSportComplexesListPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @Named(PresenterNames.SPORT_COMPLEXES_VIEWER) @NonNull
        final GoogleApiManager googleApiManager) {
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(googleApiManager, "googleApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");

        return new SportComplexesListPresenter(rxManager,
                                               messageManager,
                                               proSportApiManager,
                                               proSportAccountHelper,
                                               playerNavigationManager,
                                               googleApiManager);
    }

    @Named(PresenterNames.SPORT_COMPLEXES_MAP)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<SportComplexesMapScreen> provideSportComplexesMapPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(PresenterNames.SPORT_COMPLEXES_VIEWER)
        final GoogleApiManager googleApiManager,
        @Nullable final GoogleMapManager googleMapManager,
        @Nullable final SportComplexInfoRenderer sportComplexInfoRenderer) {
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(googleApiManager, "googleApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new SportComplexesMapPresenter(rxManager,
                                              messageManager,
                                              proSportNavigationManager,
                                              proSportApiManager,
                                              proSportAccountHelper,
                                              playerNavigationManager,
                                              googleApiManager,
                                              googleMapManager,
                                              sportComplexInfoRenderer);
    }
}
