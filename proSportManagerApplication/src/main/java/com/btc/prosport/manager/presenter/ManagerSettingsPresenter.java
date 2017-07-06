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
import com.btc.common.event.generic.EventHandler;
import com.btc.common.event.notice.NoticeEventHandler;
import com.btc.common.extension.exception.AccountNotFoundException;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.api.model.User;
import com.btc.prosport.api.model.entity.CityEntity;
import com.btc.prosport.api.model.entity.UserEntity;
import com.btc.prosport.api.request.ChangeUserCityParams;
import com.btc.prosport.api.request.ChangeUserEmailParams;
import com.btc.prosport.api.request.ChangeUserFirstNameParams;
import com.btc.prosport.api.request.ChangeUserLastNameParams;
import com.btc.prosport.api.request.ChangeUserPhoneNumberParams;
import com.btc.prosport.core.eventArgs.ChangeUserCityEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserEmailEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserFirstNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserLastNameEventArgs;
import com.btc.prosport.core.eventArgs.ChangeUserPhoneNumberEventArgs;
import com.btc.prosport.core.eventArgs.LogoutEventArgs;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.manager.R;
import com.btc.prosport.screen.ProSportSettingsScreen;

import java.util.List;

@Accessors(prefix = "_")
public final class ManagerSettingsPresenter extends BasePresenter<ProSportSettingsScreen> {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ManagerSettingsPresenter.class);

    public ManagerSettingsPresenter(
        @NonNull final RxManager rxManager,
        @NonNull final MessageManager messageManager,
        @NonNull final AccountManager<ProSportAccount> accountManager,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final ProSportAccountHelper proSportAccountHelper,
        @NonNull final UserSettingsHelper userSettingsHelper) {
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(messageManager, "messageManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");
        Contracts.requireNonNull(proSportAccountHelper, "proSportAccountHelper == null");
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null");

        _rxManager = rxManager;
        _messageManager = messageManager;
        _accountManager = accountManager;
        _accountHelper = proSportAccountHelper;
        _proSportApiManager = proSportApiManager;
        _userSettingsHelper = userSettingsHelper;
    }

    protected final void displayRemoveAccountResult(final boolean success) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayRemoveAccountResult(success);
        }
    }

    @CallSuper
    @Override
    protected void onScreenAppear(@NonNull final ProSportSettingsScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewCitiesEvent().addHandler(_viewCitiesHandler);
        screen.getViewUserEvent().addHandler(_viewUserHandler);
        screen.getChangeCityEvent().addHandler(_changeCityHandler);
        screen.getChangeFirstNameEvent().addHandler(_changeFirstNameHandler);
        screen.getChangeLastNameEvent().addHandler(_changeLastNameHandler);
        screen.getChangeEmailEvent().addHandler(_changeEmailHandler);
        screen.getChangePhoneNumberEvent().addHandler(_changePhoneNumberHandler);
        screen.getLogoutEvent().addHandler(_logoutHandler);
    }

    @CallSuper
    @Override
    protected void onScreenDestroy(@NonNull final ProSportSettingsScreen screen) {
        super.onScreenDestroy(Contracts.requireNonNull(screen, "screen == null"));

        screen.getViewCitiesEvent().removeHandler(_viewCitiesHandler);
        screen.getViewUserEvent().removeHandler(_viewUserHandler);
        screen.getChangeCityEvent().removeHandler(_changeCityHandler);
        screen.getChangeFirstNameEvent().removeHandler(_changeFirstNameHandler);
        screen.getChangeLastNameEvent().removeHandler(_changeLastNameHandler);
        screen.getChangeEmailEvent().removeHandler(_changeEmailHandler);
        screen.getChangePhoneNumberEvent().removeHandler(_changePhoneNumberHandler);
        screen.getLogoutEvent().addHandler(_logoutHandler);
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
    private final ProSportApiManager _proSportApiManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final RxManager _rxManager;

    @NonNull
    private final EventHandler<LogoutEventArgs> _logoutHandler =
        new EventHandler<LogoutEventArgs>() {
            @Override
            public void onEvent(@NonNull final LogoutEventArgs eventArgs) {
                Contracts.requireNonNull(eventArgs, "eventArgs == null");

                final val rxManager = getRxManager();

                getAccountManager()
                    .removeAccount(eventArgs.getAccount())
                    .subscribeOn(rxManager.getIOScheduler())
                    .observeOn(rxManager.getUIScheduler())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(final Boolean success) {
                            Contracts.requireMainThread();

                            displayRemoveAccountResult(success);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(final Throwable error) {
                            Contracts.requireMainThread();

                            Log.w(_LOG_TAG, "Failed to remove account.", error);

                            displayRemoveAccountResult(false);
                        }
                    });
            }
        };

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final UserSettingsHelper _userSettingsHelper;

    @NonNull
    private final EventHandler<ChangeUserCityEventArgs> _changeCityHandler =
        new EventHandler<ChangeUserCityEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserCityEventArgs eventArgs) {
                displayLoading();
                performChangeUserCity(eventArgs.getNewCityId(), eventArgs.getOldCityId());
            }
        };

    @NonNull
    private final EventHandler<ChangeUserLastNameEventArgs> _changeLastNameHandler =
        new EventHandler<ChangeUserLastNameEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserLastNameEventArgs eventArgs) {
                displayLoading();
                performChangeUserLastName(eventArgs.getNewLastName(), eventArgs.getOldLastName());
            }
        };

    @NonNull
    private final EventHandler<ChangeUserEmailEventArgs> _changeEmailHandler =
        new EventHandler<ChangeUserEmailEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserEmailEventArgs eventArgs) {
                displayLoading();
                performChangeUserEmail(eventArgs.getNewEmail(), eventArgs.getOldEmail());
            }
        };

    @NonNull
    private final EventHandler<ChangeUserFirstNameEventArgs> _changeFirstNameHandler =
        new EventHandler<ChangeUserFirstNameEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserFirstNameEventArgs eventArgs) {
                displayLoading();
                performChangeUserFirstName(eventArgs.getNewFirstName(),
                                           eventArgs.getOldFirstName());
            }
        };

    @NonNull
    private final EventHandler<ChangeUserPhoneNumberEventArgs> _changePhoneNumberHandler =
        new EventHandler<ChangeUserPhoneNumberEventArgs>() {
            @Override
            public void onEvent(@NonNull final ChangeUserPhoneNumberEventArgs eventArgs) {
                displayLoading();
                performChangeUserPhoneNumber(eventArgs.getNewPhoneNumber(),
                                             eventArgs.getOldPhoneNumber());
            }
        };

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

    private void disableLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.disableLoading();
        }
    }

    private void displayCities(@Nullable final List<CityEntity> cities) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayCities(cities);
        }
    }

    private void displayLoading() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayLoading();
        }
    }

    private void displayManager(@NonNull final User user) {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayPlayer(user);
        }
    }

    private void displayRequireAuthorizationError() {
        final val screen = getScreen();
        if (screen != null) {
            screen.displayRequireAuthorizationError();
        }
    }

    private void performChangeUserCity(
        @NonNull final String newCity, @NonNull final String oldCity) {
        Contracts.requireNonNull(newCity, "newCity == null");

        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val changeCityParams = new ChangeUserCityParams(newCity);

        rxManager
            .autoManage(accountHelper.withRequiredAccessToken(new Func1<String,
                Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    Contracts.requireWorkerThread();

                    return proSportApi.changeManagerCity(token, changeCityParams);
                }
            }, true))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity userEntity) {
                    Contracts.requireMainThread();

                    displayManager(userEntity);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, error);

                    getUserSettingsHelper().setUserCityId(oldCity);
                    getMessageManager().showErrorMessage(R.string.request_failed);
                    disableLoading();
                }
            });
    }

    private void performChangeUserEmail(
        @NonNull final String newEmail, @Nullable final String oldEmail) {
        Contracts.requireNonNull(newEmail, "newEmail == null");

        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val changeEmailParams = new ChangeUserEmailParams(newEmail);

        rxManager
            .autoManage(accountHelper.withRequiredAccessToken(new Func1<String,
                Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    Contracts.requireWorkerThread();

                    return proSportApi.changeManagerEmail(token, changeEmailParams);
                }
            }, true))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity userEntity) {
                    Contracts.requireMainThread();

                    displayManager(userEntity);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, error);

                    getUserSettingsHelper().setUserEmail(oldEmail);
                    getMessageManager().showErrorMessage(R.string.request_failed);
                    disableLoading();
                }
            });
    }

    private void performChangeUserFirstName(
        @NonNull final String newFirstName, @Nullable final String oldFirstName) {
        Contracts.requireNonNull(newFirstName, "newFirstName == null");

        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val changeFirstNameParams = new ChangeUserFirstNameParams(newFirstName);

        rxManager
            .autoManage(accountHelper.withRequiredAccessToken(new Func1<String,
                Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    return proSportApi.changeManagerFirstName(token, changeFirstNameParams);
                }
            }, true))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity userEntity) {
                    displayManager(userEntity);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, error);

                    getUserSettingsHelper().setUserFirstName(oldFirstName);
                    getMessageManager().showErrorMessage(R.string.request_failed);
                    disableLoading();
                }
            });
    }

    private void performChangeUserLastName(
        @NonNull final String newLastName, final String oldLastName) {
        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val changeLastNameParams = new ChangeUserLastNameParams(newLastName);

        rxManager
            .autoManage(accountHelper.withRequiredAccessToken(new Func1<String,
                Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    Contracts.requireWorkerThread();

                    return proSportApi.changeManagerLastName(token, changeLastNameParams);
                }
            }, true))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    displayManager(user);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, error);

                    getUserSettingsHelper().setUserLastName(oldLastName);
                    getMessageManager().showErrorMessage(R.string.request_failed);
                    disableLoading();
                }
            });
    }

    private void performChangeUserPhoneNumber(
        @NonNull final String newPhoneNumber, final String oldPhoneNumber) {
        Contracts.requireNonNull(newPhoneNumber, "newPhoneNumber == null");

        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        final val changeUserPhoneNumberParams = new ChangeUserPhoneNumberParams(newPhoneNumber);

        rxManager
            .autoManage(accountHelper.withRequiredAccessToken(new Func1<String,
                Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    Contracts.requireWorkerThread();

                    return proSportApi.changeManagerPhoneNumber(token, changeUserPhoneNumberParams);
                }
            }, true))
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity user) {
                    Contracts.requireMainThread();

                    displayManager(user);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, error);

                    getUserSettingsHelper().setUserPhoneNumber(oldPhoneNumber);
                    getMessageManager().showErrorMessage(R.string.request_failed);
                    disableLoading();
                }
            });
    }

    private void performLoadCities() {
        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();

        accountHelper
            .withRequiredAccessToken(new Func1<String, Observable<List<CityEntity>>>() {
                @Override
                public Observable<List<CityEntity>> call(final String token) {
                    Contracts.requireWorkerThread();

                    final val cities = proSportApi.getCities();
                    return rxManager.autoManage(cities);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<List<CityEntity>>() {
                @Override
                public void call(final List<CityEntity> cities) {
                    Contracts.requireMainThread();

                    displayCities(cities);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load cities.", error);

                    if (error instanceof AccountNotFoundException) {
                        displayRequireAuthorizationError();
                    } else {
                        disableLoading();
                        getMessageManager().showInfoMessage(R.string.request_failed);
                    }
                }
            });
    }

    private void performLoadUser() {
        final val rxManager = getRxManager();
        final val accountHelper = getAccountHelper();
        final val proSportApiManager = getProSportApiManager();
        final val proSportApi = proSportApiManager.getProSportApi();
        accountHelper
            .withRequiredAccessToken(new Func1<String, Observable<UserEntity>>() {
                @Override
                public Observable<UserEntity> call(final String token) {
                    final val user = proSportApi.getManager(token);
                    return rxManager.autoManage(user);
                }
            }, true)
            .subscribeOn(rxManager.getIOScheduler())
            .observeOn(rxManager.getUIScheduler())
            .subscribe(new Action1<UserEntity>() {
                @Override
                public void call(final UserEntity userEntity) {
                    Contracts.requireMainThread();
                    displayManager(userEntity);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(final Throwable error) {
                    Contracts.requireMainThread();

                    Log.w(_LOG_TAG, "Failed to load user.", error);

                    if (error instanceof AccountNotFoundException) {
                        displayRequireAuthorizationError();
                    } else {
                        disableLoading();
                        getMessageManager().showInfoMessage(R.string.request_failed);
                    }
                }
            });
    }
}
