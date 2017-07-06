package com.btc.prosport.player.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.dialer.DialerManager;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.di.screen.module.ProSportPresenterScreenModule;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.core.utility.PresenterHelper;
import com.btc.prosport.player.di.qualifier.PresenterNames;
import com.btc.prosport.player.presenter.BookingPresenter;
import com.btc.prosport.player.presenter.FeedbackPresenter;
import com.btc.prosport.player.presenter.OrdersViewerPresenter;
import com.btc.prosport.player.presenter.PhotosViewerPresenter;
import com.btc.prosport.player.presenter.PlayerSettingsPresenter;
import com.btc.prosport.player.presenter.SportComplexViewerPresenter;
import com.btc.prosport.player.presenter.SportComplexesViewerPresenter;
import com.btc.prosport.player.screen.BookingScreen;
import com.btc.prosport.player.screen.FeedbackScreen;
import com.btc.prosport.player.screen.OrdersViewerScreen;
import com.btc.prosport.player.screen.PhotosViewerScreen;
import com.btc.prosport.player.screen.SportComplexViewerScreen;
import com.btc.prosport.player.screen.SportComplexesViewerScreen;
import com.btc.prosport.screen.ProSportSettingsScreen;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class PlayerPresenterScreenModule extends ProSportPresenterScreenModule {
    @Named(PresenterNames.BOOKING)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<BookingScreen> provideBookingPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new BookingPresenter(proSportAccountManager,
                                    proSportAccountHelper,
                                    rxManager,
                                    proSportApiManager,
                                    playerNavigationManager,
                                    messageManager,
                                    photoManager,
                                    proSportNavigationManager);
    }

    @Named(PresenterNames.FEEDBACK)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<FeedbackScreen> provideFeedbackPresenter(
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new FeedbackPresenter(accountManager,
                                     proSportAccountHelper,
                                     rxManager,
                                     proSportApiManager,
                                     playerNavigationManager,
                                     messageManager,
                                     photoManager,
                                     proSportNavigationManager);
    }

    @Named(PresenterNames.ORDERS_VIEWER)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<OrdersViewerScreen> provideOrdersViewerPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new OrdersViewerPresenter(proSportAccountManager,
                                         proSportAccountHelper,
                                         rxManager,
                                         proSportApiManager,
                                         playerNavigationManager,
                                         messageManager,
                                         photoManager,
                                         proSportNavigationManager);
    }

    @Named(PresenterNames.PHOTOS_VIEWER)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<PhotosViewerScreen> providePhotosViewerPresenter(
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        return new PhotosViewerPresenter(rxManager, proSportApiManager);
    }

    @Named(PresenterNames.SETTINGS)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<ProSportSettingsScreen> providePlayerSettingsPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final UserSettingsHelper userSettingsHelper) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null");

        return new PlayerSettingsPresenter(rxManager,
                                           messageManager,
                                           accountManager,
                                           proSportApiManager,
                                           proSportAccountHelper,
                                           userSettingsHelper);
    }

    @Provides
    @ScreenScope
    @NonNull
    public final PresenterHelper providePresenterHelper(
        @NonNull final DialerManager dialerManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager) {
        Contracts.requireNonNull(dialerManager, "dialerManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        return new PresenterHelper(dialerManager,
                                   messageManager,
                                   proSportNavigationManager,
                                   rxManager);
    }

    @Named(PresenterNames.SPORT_COMPLEX_VIEWER)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<SportComplexViewerScreen> provideSportComplexViewerPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == " + "null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new SportComplexViewerPresenter(proSportAccountManager,
                                               proSportAccountHelper,
                                               playerNavigationManager,
                                               rxManager,
                                               messageManager,
                                               proSportApiManager,
                                               photoManager,
                                               proSportNavigationManager);
    }

    @Named(PresenterNames.SPORT_COMPLEXES_VIEWER)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<SportComplexesViewerScreen> provideSportComplexesViewerPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final TimePickerManager timePickerManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null");
        Contracts.requireNonNull(timePickerManager, "dataPickerManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new SportComplexesViewerPresenter(proSportAccountManager,
                                                 proSportAccountHelper,
                                                 rxManager,
                                                 proSportApiManager,
                                                 playerNavigationManager,
                                                 timePickerManager,
                                                 messageManager,
                                                 photoManager,
                                                 proSportNavigationManager);
    }
}
