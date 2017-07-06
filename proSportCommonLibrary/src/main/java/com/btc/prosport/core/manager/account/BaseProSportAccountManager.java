package com.btc.prosport.core.manager.account;

import android.accounts.Account;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

@Accessors(prefix = "_")
public abstract class BaseProSportAccountManager {
    protected BaseProSportAccountManager(
        @NonNull final android.accounts.AccountManager nativeAccountManager,
        @NonNull final String accountType) {
        Contracts.requireNonNull(nativeAccountManager, "nativeAccountManager == null");
        Contracts.requireNonNull(accountType, "accountType == null");

        _nativeAccountManager = nativeAccountManager;
        _accountType = accountType;
    }

    @CallSuper
    @NonNull
    protected ProSportAccount createProSportAccount(@NonNull final Account account) {
        Contracts.requireNonNull(account, "account == null");

        return new ProSportAccount(account.name);
    }

    @CallSuper
    @Nullable
    protected Account getNativeAccountByName(@NonNull final String name) {
        Contracts.requireNonNull(name, "name == null");

        Account account = null;
        val accountsByType = getNativeAccountManager().getAccountsByType(getAccountType());
        for (final val accountByName : accountsByType) {
            if (accountByName.name.equals(name)) {
                account = accountByName;
                break;
            }
        }

        return account;
    }

    @Getter
    @NonNull
    private final String _accountType;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final android.accounts.AccountManager _nativeAccountManager;
}
