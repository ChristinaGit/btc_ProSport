package com.btc.prosport.core.manager.proSportServiceHelper;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Func1;

public interface ProSportBaseAccountHelper {
    @NonNull
    <T> Observable<T> withAccessToken(
        @NonNull final Func1<String, Observable<T>> method);
}
