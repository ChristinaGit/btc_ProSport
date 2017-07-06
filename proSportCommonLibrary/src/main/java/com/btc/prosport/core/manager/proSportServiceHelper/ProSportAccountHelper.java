package com.btc.prosport.core.manager.proSportServiceHelper;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

public interface ProSportAccountHelper {
    @NonNull
    <T> Observable<T> withAccessToken(
        @NonNull final Func1<String, Observable<T>> method, boolean allowConfirmCredentials);

    @NonNull
    <T> Observable<T> withRequiredAccessToken(
        @NonNull final Func1<String, Observable<T>> method, boolean allowConfirmCredentials);
}
