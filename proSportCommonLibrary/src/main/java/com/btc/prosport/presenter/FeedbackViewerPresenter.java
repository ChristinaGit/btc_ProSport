package com.btc.prosport.presenter;

import android.support.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.screen.FeedbackViewerScreen;

@Accessors(prefix = "_")
public class FeedbackViewerPresenter extends BasePresenter<FeedbackViewerScreen> {

    public FeedbackViewerPresenter(
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final RxManager rxManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportNavigationManager navigationManager) {
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(navigationManager, "navigationManager == null");

        _accountHelper = proSportAccountHelper;
        _accountManager = accountManager;
        _rxManager = rxManager;
        _proSportApiManager = proSportApiManager;
        _messageManager = messageManager;
        _navigationManager = navigationManager;
    }

    @Override
    protected void onScreenAppear(
        @NonNull final FeedbackViewerScreen screen) {
        super.onScreenAppear(screen);
    }

    @Override
    protected void onScreenDestroy(
        @NonNull final FeedbackViewerScreen screen) {
        super.onScreenDestroy(screen);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _accountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final AccountManager<ProSportAccount> _accountManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNavigationManager _navigationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    private void disableLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.disableLoading();
        }
    }

    private void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoading();
        }
    }

    private void displaySuccessMessageSentView() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displaySuccessMessageSentView();
        }
    }

    private void performSendMessage(final String name, final String email, final String message) {

        final val rxManager = getRxManager();
        final val accountApiHelper = getAccountHelper();
        final val apiManager = getProSportApiManager();
        final val proSportApi = apiManager.getProSportApi();

        //TODO 05.04.2017 need to add real observable
    }
}
