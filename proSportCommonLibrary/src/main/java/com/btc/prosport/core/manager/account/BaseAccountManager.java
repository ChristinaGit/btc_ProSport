package com.btc.prosport.core.manager.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

import java.util.List;

public interface BaseAccountManager<TAccount> {
    @Nullable
    TAccount getAccountByName(@NonNull String name);

    @NonNull
    String getAccountType();

    @Nullable
    List<TAccount> getAccounts();

    @NonNull
    Observable<String> getToken(@NonNull TAccount account);

    void invalidateToken(@NonNull String authToken);
}
