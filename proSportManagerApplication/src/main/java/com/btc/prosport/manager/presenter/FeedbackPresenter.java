package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.notificationEvent.ProSportMangerNotificationEventManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;
import com.btc.prosport.manager.screen.FeedbackScreen;

@Accessors(prefix = "_")
public final class FeedbackPresenter extends BaseManagerNavigationPresenter<FeedbackScreen> {
    public FeedbackPresenter(
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
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

    @Override
    protected void onScreenAppear(@NonNull final FeedbackScreen screen) {
        super.onScreenAppear(screen);
        screen.getNavigateToHomeEvent().addHandler(_navigateToHomeHandler);
    }

    @Override
    protected void onScreenDisappear(
        @NonNull final FeedbackScreen screen) {
        super.onScreenDisappear(screen);
        screen.getNavigateToHomeEvent().removeHandler(_navigateToHomeHandler);
    }

    @NonNull
    private final NoticeEventHandler _navigateToHomeHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            getManagerNavigationManager().navigateToHomeScreen();
        }
    };
}
