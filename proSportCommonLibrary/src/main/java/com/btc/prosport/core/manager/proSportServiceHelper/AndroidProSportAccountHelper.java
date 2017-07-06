package com.btc.prosport.core.manager.proSportServiceHelper;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.exception.AccountNotFoundException;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;

import org.apache.commons.lang3.BooleanUtils;

import java.net.HttpURLConnection;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class AndroidProSportAccountHelper implements ProSportAccountHelper {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(AndroidProSportAccountHelper.class);

    public AndroidProSportAccountHelper(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager) {

        _proSportAccountManager = proSportAccountManager;
    }

    // TODO: Handle IOException.
    // TODO: Handle empty token (confirm credentials).
    // TODO: Handle Authenticator exception.
    @NonNull
    @Override
    public <T> Observable<T> withAccessToken(
        @NonNull final Func1<String, Observable<T>> method, final boolean allowConfirmCredentials) {
        Contracts.requireNonNull(method, "method == null");

        final val accountManager = getProSportAccountManager();

        final val accounts = accountManager.getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            final val account = accounts.get(0);

            return accountManager
                .getToken(account)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String token) {
                        if (allowConfirmCredentials && TextUtils.isEmpty(token)) {
                            return accountManager
                                .confirmCredentials(account)
                                .onErrorResumeNext(new Func1<Throwable, Observable<Boolean>>() {
                                    @Override
                                    public Observable<Boolean> call(final Throwable error) {
                                        return Observable.error(new AccountNotFoundException());
                                    }
                                })
                                .flatMap(new Func1<Boolean, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(final Boolean confirmed) {
                                        if (BooleanUtils.isTrue(confirmed)) {
                                            return accountManager.getToken(account);
                                        } else {
                                            return Observable.just(null);
                                        }
                                    }
                                });
                        } else {
                            return Observable.just(token);
                        }
                    }
                })
                .flatMap(new Func1<String, Observable<T>>() {
                    @Override
                    public Observable<T> call(final String token) {
                        return method.call(token).doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(final Throwable error) {
                                Log.w(_LOG_TAG, "Failed to perform method.", error);

                                if (error instanceof HttpException) {
                                    final val httpException = (HttpException) error;
                                    if (httpException.code() ==
                                        HttpURLConnection.HTTP_UNAUTHORIZED) {
                                        accountManager.invalidateToken(token);
                                    }
                                }
                            }
                        });
                    }
                })
                .retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(final Integer integer, final Throwable error) {
                        boolean retry = false;

                        if (integer == 1 && error instanceof HttpException) {
                            final val httpException = (HttpException) error;
                            retry = httpException.code() == HttpURLConnection.HTTP_UNAUTHORIZED;
                        }

                        if (retry) {
                            Log.w(_LOG_TAG, "Retry...");
                        }

                        return retry;
                    }
                });
        } else {
            return method.call(null);
        }
    }

    @NonNull
    @Override
    public <T> Observable<T> withRequiredAccessToken(
        @NonNull final Func1<String, Observable<T>> method, final boolean allowConfirmCredentials) {
        Contracts.requireNonNull(method, "method == null");

        final val accountManager = getProSportAccountManager();
        final val accounts = accountManager.getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            return withAccessToken(method, allowConfirmCredentials);
        } else {
            return accountManager
                .addAccount(null)
                .onErrorResumeNext(new Func1<Throwable, Observable<ProSportAccount>>() {
                    @Override
                    public Observable<ProSportAccount> call(final Throwable error) {
                        return Observable.error(new AccountNotFoundException());
                    }
                })
                .flatMap(new Func1<ProSportAccount, Observable<T>>() {
                    @Override
                    public Observable<T> call(final ProSportAccount proSportAccount) {
                        return withAccessToken(method, allowConfirmCredentials);
                    }
                });
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final AccountManager<ProSportAccount> _proSportAccountManager;
}
