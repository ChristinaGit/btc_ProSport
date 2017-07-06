package com.btc.prosport.manager.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.User;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.screen.ManagerAccountScreen;

@Accessors(prefix = "_")
public abstract class BaseManagerAccountPresenter<TScreen extends ManagerAccountScreen>
    extends BasePresenter<TScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(BaseManagerAccountPresenter.class);

    protected BaseManagerAccountPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final MessageManager messageManager,
        @NonNull final RxManager rxManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        _proSportAccountManager = proSportAccountManager;
        _proSportAccountHelper = proSportAccountHelper;
        _proSportApiManager = proSportApiManager;
        _messageManager = messageManager;
        _rxManager = rxManager;
    }

    protected final void displayLoadManagerError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayManagerLoadingError();
        }
    }

    protected final void displayManager(@Nullable final User user) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayManager(user);
        }
    }

    protected final void displayManagerLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayManagerLoading();
        }
    }

    protected final void displayNoAccountsError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayNoAccountsError();
        }
    }

    protected final void performLoadManager() {
        final val rxManager = getRxManager();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    return rxManager.autoManage(proSportApi.getManager(token));
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    if (user != null) {
                        displayManager(user);
                    } else {
                        displayNoAccountsError();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    getMessageManager().showNotificationMessage(R.string
                                                                    .message_you_must_be_authorized);

                    displayLoadManagerError();

                    Log.w(_LOG_TAG, "Failed to get user.", error);
                }
            });
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final TScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewManagerEvent().addHandler(_viewManagerHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final TScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewManagerEvent().removeHandler(_viewManagerHandler);
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final MessageManager _messageManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportAccountHelper _proSportAccountHelper;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final AccountManager<ProSportAccount> _proSportAccountManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final NoticeEventHandler _viewManagerHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            displayManagerLoading();

            performLoadManager();
        }
    };
}
