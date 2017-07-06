package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.notificationEvent.ProSportMangerNotificationEventManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.OrderDetailsScreen;

public final class OrderDetailsPresenter
    extends BaseManagerNavigationPresenter<OrderDetailsScreen> {
    public OrderDetailsPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ManagerNavigationManager managerNavigationManager,
        @NonNull final ProSportMangerNotificationEventManager notificationEventManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        //@formatter:off
        super(
            Contracts.requireNonNull(accountManager, "accountManager == null"),
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(rxManager, "rxManager == null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(managerNavigationManager, "managerNavigationManager == null"),
            Contracts.requireNonNull(notificationEventManager, "notificationEventManager == null"),
            Contracts.requireNonNull(photoManager, "photoManager == null"),
            Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null"));
        //@formatter:on
    }
}
