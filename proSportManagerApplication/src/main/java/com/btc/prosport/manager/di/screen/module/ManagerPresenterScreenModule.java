package com.btc.prosport.manager.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.notificationEvent.ProSportMangerNotificationEventManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.di.screen.module.ProSportPresenterScreenModule;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.presenter.FeedbackPresenter;
import com.btc.prosport.manager.presenter.ManagerSettingsPresenter;
import com.btc.prosport.manager.presenter.OrderDetailsPresenter;
import com.btc.prosport.manager.presenter.OrderEditorPresenter;
import com.btc.prosport.manager.presenter.PlaygroundEditorPresenter;
import com.btc.prosport.manager.presenter.SaleEditorPresenter;
import com.btc.prosport.manager.presenter.WorkspacePresenter;
import com.btc.prosport.manager.screen.FeedbackScreen;
import com.btc.prosport.manager.screen.OrderDetailsScreen;
import com.btc.prosport.manager.screen.OrderEditorScreen;
import com.btc.prosport.manager.screen.PlaygroundEditorScreen;
import com.btc.prosport.manager.screen.SaleEditorScreen;
import com.btc.prosport.manager.screen.WorkspaceScreen;
import com.btc.prosport.screen.ProSportSettingsScreen;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ManagerPresenterScreenModule extends ProSportPresenterScreenModule {
    @Named(PresenterNames.FEEDBACK)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<FeedbackScreen> provideFeedbackPresenter(
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final ProSportMangerNotificationEventManager notificationEventManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        //@formatter:off
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(notificationEventManager, "notificationEventManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        //@formatter:on

        return new FeedbackPresenter(accountManager,
                                     proSportAccountHelper,
                                     rxManager,
                                     messageManager,
                                     proSportApiManager,
                                     managerNavigationManager,
                                     notificationEventManager,
                                     photoManager,
                                     proSportNavigationManager);
    }

    @Named(PresenterNames.ORDER_EDITOR)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<OrderEditorScreen> provideOrderEditorPresenter(
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApi proSportApi) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");

        return new OrderEditorPresenter(rxManager,
                                        proSportAccountHelper,
                                        messageManager,
                                        proSportApi);
    }

    @Named(PresenterNames.SETTINGS)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<ProSportSettingsScreen> providePlayerSettingsPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ManagerNavigationManager playerNavigationManager,
        @NonNull final UserSettingsHelper userSettingsHelper) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null");

        return new ManagerSettingsPresenter(rxManager,
                                            messageManager,
                                            accountManager,
                                            proSportApiManager,
                                            proSportAccountHelper,
                                            userSettingsHelper);
    }

    @Named(PresenterNames.PLAYGROUND_EDITOR)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<PlaygroundEditorScreen> providePlaygroundEditorPresenter() {
        return new PlaygroundEditorPresenter();
    }

    @Named(PresenterNames.ORDER_DETAILS)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<OrderDetailsScreen> provideReservationPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final ProSportMangerNotificationEventManager notificationEventManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(notificationEventManager, "notificationEventManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new OrderDetailsPresenter(proSportAccountHelper,
                                         rxManager,
                                         messageManager,
                                         accountManager,
                                         proSportApiManager,
                                         managerNavigationManager,
                                         notificationEventManager,
                                         photoManager,
                                         proSportNavigationManager);
    }

    @Named(PresenterNames.SALE_EDITOR)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<SaleEditorScreen> provideSaleEditorPresenter(
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApi proSportApi) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");

        return new SaleEditorPresenter(messageManager,
                                       proSportAccountHelper,
                                       proSportApiManager,
                                       rxManager);
    }

    @Named(PresenterNames.WORKSPACE)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<WorkspaceScreen> provideViewerPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportMangerNotificationEventManager notificationEventManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(notificationEventManager, "notificationEventManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new WorkspacePresenter(proSportAccountHelper,
                                      rxManager,
                                      messageManager,
                                      proSportApiManager,
                                      managerNavigationManager,
                                      accountManager,
                                      notificationEventManager,
                                      photoManager,
                                      proSportNavigationManager);
    }
}
