package com.btc.prosport.core.manager.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import rx.Observable;
import rx.Subscriber;

import com.btc.common.contract.Contracts;

import java.util.ArrayList;
import java.util.List;

public class ApplicationProSportBaseAccountManager extends BaseProSportAccountManager
    implements BaseAccountManager<ProSportAccount> {
    public ApplicationProSportBaseAccountManager(
        @NonNull final android.accounts.AccountManager nativeAccountManager,
        @NonNull final String accountType) {
        super(
            Contracts.requireNonNull(nativeAccountManager, "nativeAccountManager == null"),
            Contracts.requireNonNull(accountType, "accountType == null"));
    }

    @Nullable
    @Override
    public ProSportAccount getAccountByName(@NonNull final String name) {
        Contracts.requireNonNull(name, "name == null");

        final ProSportAccount account;

        val nativeAccountByName = getNativeAccountByName(name);
        if (nativeAccountByName != null) {
            account = createProSportAccount(nativeAccountByName);
        } else {
            account = null;
        }

        return account;
    }

    @Nullable
    @Override
    public List<ProSportAccount> getAccounts() {
        val accounts = getNativeAccountManager().getAccountsByType(getAccountType());

        final ArrayList<ProSportAccount> proSportAccounts;
        final int length = accounts.length;
        if (length > 0) {
            val accountsList = new ArrayList<ProSportAccount>(length);
            for (final val account : accounts) {
                accountsList.add(createProSportAccount(account));
            }

            proSportAccounts = accountsList;
        } else {
            proSportAccounts = null;
        }

        return proSportAccounts;
    }

    @NonNull
    @Override
    public Observable<String> getToken(@NonNull final ProSportAccount proSportAccount) {
        Contracts.requireNonNull(proSportAccount, "proSportAccount == null");

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Contracts.requireWorkerThread();

                val account = getNativeAccountByName(proSportAccount.getPhoneNumber());
                if (account != null) {
                    try {
                        val authToken = getNativeAccountManager().blockingGetAuthToken(account,
                                                                                       AccountContracts.TOKEN_TYPE_FULL_ACCESS,
                                                                                       false);
                        subscriber.onNext(authToken);
                        subscriber.onCompleted();
                    } catch (final Exception error) {
                        subscriber.onError(error);
                    }
                } else {
                    subscriber.onError(new RuntimeException("Account not exists"));
                }
            }
        });
    }

    @Override
    public void invalidateToken(@NonNull final String authToken) {
        Contracts.requireNonNull(authToken, "authToken == null");

        getNativeAccountManager().invalidateAuthToken(getAccountType(), authToken);
    }
}
