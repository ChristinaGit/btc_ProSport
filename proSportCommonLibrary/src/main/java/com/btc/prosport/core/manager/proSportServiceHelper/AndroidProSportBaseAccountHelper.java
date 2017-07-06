package com.btc.prosport.core.manager.proSportServiceHelper;

import android.support.annotation.NonNull;
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
import com.btc.prosport.core.manager.account.BaseAccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;

import java.net.HttpURLConnection;

import retrofit2.adapter.rxjava.HttpException;

@Accessors(prefix = "_")
public class AndroidProSportBaseAccountHelper implements ProSportBaseAccountHelper {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(AndroidProSportBaseAccountHelper.class);

    public AndroidProSportBaseAccountHelper(
        @NonNull final BaseAccountManager<ProSportAccount> proSportAccountManager) {

        _proSportAccountManager = proSportAccountManager;
    }

    // TODO: Handle IOException.
    // TODO: Handle Authenticator exception.
    @NonNull
    @Override
    public <T> Observable<T> withAccessToken(
        @NonNull final Func1<String, Observable<T>> method) {
        Contracts.requireNonNull(method, "method == null");

        final val accountManager = getProSportAccountManager();

        final val accounts = accountManager.getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            final val account = accounts.get(0);

            return accountManager.getToken(account).flatMap(new Func1<String, Observable<T>>() {
                @Override
                public Observable<T> call(final String token) {
                    return method.call(token).doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(final Throwable error) {
                            Log.w(_LOG_TAG, "Failed to perform method.", error);

                            if (error instanceof HttpException) {
                                final val httpException = (HttpException) error;
                                if (httpException.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                    accountManager.invalidateToken(token);
                                }
                            }
                        }
                    });
                }
            }).retry(new Func2<Integer, Throwable, Boolean>() {
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

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final BaseAccountManager<ProSportAccount> _proSportAccountManager;
}
