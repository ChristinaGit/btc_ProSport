package com.btc.prosport.presenter;

import android.support.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.screen.ProSportSettingsScreen;

@Accessors(prefix = "_")
public final class ProSportUserSettingsPresenter extends BasePresenter<ProSportSettingsScreen> {

    public ProSportUserSettingsPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper) {

        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _accountHelper = proSportAccountHelper;
        _proSportApiManager = proSportApiManager;
    }

    @Override
    protected void onScreenAppear(@NonNull final ProSportSettingsScreen screen) {
        super.onScreenAppear(screen);
        screen.getViewCitiesEvent().addHandler(_viewCitiesHandler);
        screen.getViewUserEvent().addHandler(_viewUserHandler);
    }

    @Override
    protected void onScreenDestroy(@NonNull final ProSportSettingsScreen screen) {
        super.onScreenDestroy(screen);
        screen.getViewCitiesEvent().addHandler(_viewCitiesHandler);
        screen.getViewUserEvent().addHandler(_viewUserHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _accountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final NoticeEventHandler _viewCitiesHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            displayLoading();
            performLoadCities();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewUserHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            displayLoading();
            performLoadUser();
        }
    };

    private void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoading();
        }
    }

    private void performLoadCities() {

    }

    private void performLoadUser() {
        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

/*        rxManager.autoManage(accountHelper.withAccessToken(new Func1<String, Observable<? extends Object>>() {
            @Override
            public Observable<? extends Object> call(final String s) {
                return null;
            }
        }))*/
    }
}
