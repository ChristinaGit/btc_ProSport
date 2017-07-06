package com.btc.prosport.manager.di.subscreen.module;

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
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.btc.prosport.di.subscreen.module.ProSportPresenterSubscreenModule;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.di.qualifier.PresenterNames;
import com.btc.prosport.manager.presenter.OrderViewerPresenter;
import com.btc.prosport.manager.presenter.OrdersListPresenter;
import com.btc.prosport.manager.presenter.PlaygroundAttributesEditorPresenter;
import com.btc.prosport.manager.presenter.PlaygroundCoveringEditorPresenter;
import com.btc.prosport.manager.presenter.PlaygroundDimensionsEditorPresenter;
import com.btc.prosport.manager.presenter.PlaygroundGeneralInfoEditorPresenter;
import com.btc.prosport.manager.presenter.PlaygroundScheduleEditorPresenter;
import com.btc.prosport.manager.presenter.PriceEditorPresenter;
import com.btc.prosport.manager.presenter.SalesListPresenter;
import com.btc.prosport.manager.presenter.SchedulePresenter;
import com.btc.prosport.manager.screen.OrderViewerScreen;
import com.btc.prosport.manager.screen.OrdersListScreen;
import com.btc.prosport.manager.screen.PlaygroundAttributesEditorScreen;
import com.btc.prosport.manager.screen.PlaygroundCoveringEditorScreen;
import com.btc.prosport.manager.screen.PlaygroundDimensionsEditorScreen;
import com.btc.prosport.manager.screen.PlaygroundGeneralInfoEditorScreen;
import com.btc.prosport.manager.screen.PlaygroundScheduleEditorScreen;
import com.btc.prosport.manager.screen.PriceEditorScreen;
import com.btc.prosport.manager.screen.SalesListScreen;
import com.btc.prosport.manager.screen.ScheduleScreen;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@SubscreenScope
public class ManagerPresenterSubscreenModule extends ProSportPresenterSubscreenModule {
    @Named(PresenterNames.ATTRIBUTE_EDITOR)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PlaygroundAttributesEditorScreen> provideAttributesEditorPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        return new PlaygroundAttributesEditorPresenter(messageManager,
                                                       proSportAccountHelper,
                                                       proSportApiManager,
                                                       rxManager);
    }

    @Named(PresenterNames.COVERING_EDITOR)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PlaygroundCoveringEditorScreen> provideCoveringEditorPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        return new PlaygroundCoveringEditorPresenter(messageManager,
                                                     proSportAccountHelper,
                                                     proSportApiManager,
                                                     rxManager);
    }

    @Named(PresenterNames.ORDER)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<OrderViewerScreen> provideDetailsReservationPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        return new OrderViewerPresenter(rxManager,
                                        proSportApiManager,
                                        messageManager,
                                        managerNavigationManager,
                                        accountManager,
                                        proSportAccountHelper);
    }

    @Named(PresenterNames.DIMENSIONS_EDITOR)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PlaygroundDimensionsEditorScreen> provideDimensionsEditorPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        return new PlaygroundDimensionsEditorPresenter(messageManager,
                                                       proSportAccountHelper,
                                                       proSportApiManager,
                                                       rxManager);
    }

    @Named(PresenterNames.SCHEDULE)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<ScheduleScreen> provideManagerSchedulePresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final MessageManager messageManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");

        return new SchedulePresenter(rxManager,
                                     proSportApiManager,
                                     proSportAccountHelper,
                                     proSportAccountManager,
                                     managerNavigationManager,
                                     messageManager);
    }

    @Named(PresenterNames.ORDER_LIST)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<OrdersListScreen> provideOrderListPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager,
        @NonNull final DialerManager dialerManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");
        Contracts.requireNonNull(dialerManager, "dialerManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        return new OrdersListPresenter(rxManager,
                                       proSportApiManager,
                                       messageManager,
                                       managerNavigationManager,
                                       proSportNavigationManager,
                                       dialerManager,
                                       proSportAccountHelper);
    }

    @Named(PresenterNames.PLAYGROUND_GENERAL_INFO_EDITOR)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PlaygroundGeneralInfoEditorScreen>
    providePlaygroundGeneralInfoEditorPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null");
        Contracts.requireNonNull(photoManager, "photoManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new PlaygroundGeneralInfoEditorPresenter(rxManager,
                                                        proSportApiManager,
                                                        managerNavigationManager,
                                                        photoManager,
                                                        messageManager,
                                                        proSportNavigationManager);
    }

    @Named(PresenterNames.PLAYGROUND_SCHEDULE_EDITOR)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PlaygroundScheduleEditorScreen> providePlaygroundScheduleEditorPresenter(
        @NonNull final TimePickerManager timePickerManager) {
        Contracts.requireNonNull(timePickerManager, "dataPickerManager == null");

        return new PlaygroundScheduleEditorPresenter(timePickerManager);
    }

    @Named(PresenterNames.PRICE_EDITOR)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<PriceEditorScreen> providePriceEditorPresenter(
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager) {
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        return new PriceEditorPresenter(messageManager,
                                        proSportAccountHelper,
                                        proSportApiManager,
                                        rxManager);
    }

    @Named(PresenterNames.SALES_LIST)
    @Provides
    @SubscreenScope
    @NonNull
    public final Presenter<SalesListScreen> provideSalesListPresenter(
        @NonNull @Named(ScopeNames.SUBSCREEN) final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");

        return new SalesListPresenter(rxManager,
                                      messageManager,
                                      proSportApiManager,
                                      proSportAccountHelper);
    }
}
