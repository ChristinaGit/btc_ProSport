package com.btc.prosport.core.manager.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

public interface AccountManager<TAccount> extends BaseAccountManager<TAccount> {
    @NonNull
    Observable<TAccount> addAccount(@Nullable String name);

    @NonNull
    Observable<Boolean> confirmCredentials(@NonNull TAccount account);

    @NonNull
    Observable<Boolean> removeAccount(@NonNull TAccount account);
}
