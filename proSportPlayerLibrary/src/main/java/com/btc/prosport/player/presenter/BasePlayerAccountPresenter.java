package com.btc.prosport.player.presenter;

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
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.User;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.player.screen.PlayerAccountScreen;

@Accessors(prefix = "_")
public abstract class BasePlayerAccountPresenter<TScreen extends PlayerAccountScreen>
    extends BasePresenter<TScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(BasePlayerAccountPresenter.class);

    protected BasePlayerAccountPresenter(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final RxManager rxManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");

        _proSportAccountManager = proSportAccountManager;
        _proSportAccountHelper = proSportAccountHelper;
        _proSportApiManager = proSportApiManager;
        _rxManager = rxManager;
    }

    protected final void displayNoAccountsError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayNoAccountsError();
        }
    }

    protected final void displayPlayer(@Nullable final User user) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlayer(user);
        }
    }

    protected final void displayPlayerLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlayerLoading();
        }
    }

    protected final void displayPlayerLoadingError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlayerLoadingError();
        }
    }

    protected final void performLoadPlayer() {
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withAccessToken(new Func1<String, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    if (token != null) {
                        return rxManager.autoManage(getProSportApiManager()
                                                        .getProSportApi()
                                                        .getPlayer(token));
                    } else {
                        return Observable.just(null);
                    }
                }
            }, false)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    if (user != null) {
                        displayPlayer(user);
                    } else {
                        displayNoAccountsError();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    displayPlayerLoadingError();

                    Log.w(_LOG_TAG, "Failed to get user.", error);
                }
            });
    }

    protected final void performPlayerLogin() {
        final val rxManager = getRxManager();

        getProSportAccountHelper()
            .withRequiredAccessToken(new Func1<String, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    if (token != null) {
                        return rxManager.autoManage(getProSportApiManager()
                                                        .getProSportApi()
                                                        .getPlayer(token));
                    } else {
                        return Observable.just(null);
                    }
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    if (user != null) {
                        displayPlayer(user);
                    } else {
                        displayNoAccountsError();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    displayPlayerLoadingError();

                    Log.w(_LOG_TAG, "Failed to get user.", error);
                }
            });
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final TScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPlayerEvent().addHandler(_viewPlayerHandler);
        screen.getPlayerLoginEvent().addHandler(_playerLoginHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDisappear(@NonNull final TScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewPlayerEvent().removeHandler(_viewPlayerHandler);
        screen.getPlayerLoginEvent().removeHandler(_playerLoginHandler);
    }

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
    private final NoticeEventHandler _playerLoginHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            performPlayerLogin();
        }
    };

    @NonNull
    private final NoticeEventHandler _viewPlayerHandler = new NoticeEventHandler() {
        @Override
        public void onEvent() {
            displayPlayerLoading();

            performLoadPlayer();
        }
    };
}
