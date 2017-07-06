package com.btc.prosport.player.presenter;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.screen.BookingScreen;

public final class BookingPresenter extends BasePlayerNavigationPresenter<BookingScreen> {
    public BookingPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final PlayerNavigationManager playerNavigationManager,
        @NonNull final MessageManager messageManager,
        @NonNull final PhotoManager photoManager,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        super(
            Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null"),
            Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null"),
            Contracts.requireNonNull(rxManager, "rxManager == null"),
            Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null"),
            Contracts.requireNonNull(playerNavigationManager, "playerNavigationManager == null"),
            Contracts.requireNonNull(messageManager, "messageManager == null"),
            Contracts.requireNonNull(photoManager, "photoManager == null"),
            Contracts.requireNonNull(proSportNavigationManager,
                                     "proSportNavigationManager == null"));
    }
}
