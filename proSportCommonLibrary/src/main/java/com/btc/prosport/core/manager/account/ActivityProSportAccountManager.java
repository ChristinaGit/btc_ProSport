package com.btc.prosport.core.manager.account;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.Subscriber;

import com.btc.common.contract.Contracts;

import java.io.IOException;
import java.util.List;

@Accessors(prefix = "_")
public class ActivityProSportAccountManager extends BaseProSportAccountManager
    implements AccountManager<ProSportAccount> {
    public ActivityProSportAccountManager(
        @NonNull final android.accounts.AccountManager nativeAccountManager,
        @NonNull final String accountType,
        @NonNull final BaseAccountManager<ProSportAccount> baseAccountManager,
        @NonNull final Activity activity) {
        super(
            Contracts.requireNonNull(nativeAccountManager, "nativeAccountManager == null"),
            Contracts.requireNonNull(accountType, "accountType == null"));
        Contracts.requireNonNull(baseAccountManager, "baseAccountManager == null");
        Contracts.requireNonNull(activity, "activity == null");

        _activity = activity;
        _baseAccountManager = baseAccountManager;
    }

    @NonNull
    @Override
    public Observable<ProSportAccount> addAccount(@Nullable final String name) {
        return Observable.create(new Observable.OnSubscribe<ProSportAccount>() {
            @Override
            public void call(final Subscriber<? super ProSportAccount> subscriber) {
                Contracts.requireWorkerThread();

                try {
                    Bundle bundle = null;
                    if (!TextUtils.isEmpty(name)) {
                        bundle = new Bundle(1);
                        bundle.putString(android.accounts.AccountManager.KEY_ACCOUNT_NAME, name);
                    }

                    val nativeAccountManager = getNativeAccountManager();
                    val accountManagerFuture = nativeAccountManager.addAccount(
                        getAccountType(),
                        AccountContracts.TOKEN_TYPE_FULL_ACCESS,
                        null,
                        bundle,
                        getActivity(),
                        null,
                        null);

                    val accountName = accountManagerFuture
                        .getResult()
                        .getString(android.accounts.AccountManager.KEY_ACCOUNT_NAME);

                    subscriber.onNext(new ProSportAccount(accountName));
                    subscriber.onCompleted();
                } catch (AuthenticatorException | IOException | OperationCanceledException error) {
                    subscriber.onError(error);
                }
            }
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> confirmCredentials(
        @NonNull final ProSportAccount proSportAccount) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                Contracts.requireWorkerThread();

                val nativeAccountByName = getNativeAccountByName(proSportAccount.getPhoneNumber());
                try {
                    val confirmed = getNativeAccountManager()
                        .confirmCredentials(nativeAccountByName, null, getActivity(), null, null)
                        .getResult()
                        .getBoolean(android.accounts.AccountManager.KEY_BOOLEAN_RESULT);

                    subscriber.onNext(confirmed);
                    subscriber.onCompleted();
                } catch (final Exception error) {
                    subscriber.onError(error);
                }
            }
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> removeAccount(@NonNull final ProSportAccount proSportAccount) {
        Contracts.requireNonNull(proSportAccount, "account == null");

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                Contracts.requireWorkerThread();

                try {
                    val nativeAccountByName =
                        getNativeAccountByName(proSportAccount.getPhoneNumber());
                    val nativeAccountManager = getNativeAccountManager();

                    final boolean removed;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        removed = nativeAccountManager
                            .removeAccount(nativeAccountByName, getActivity(), null, null)
                            .getResult()
                            .getBoolean(android.accounts.AccountManager.KEY_BOOLEAN_RESULT);
                    } else {
                        //noinspection deprecation
                        removed = nativeAccountManager
                            .removeAccount(nativeAccountByName, null, null)
                            .getResult();
                    }
                    subscriber.onNext(removed);
                    subscriber.onCompleted();
                } catch (final Exception error) {
                    subscriber.onError(error);
                }
            }
        });
    }

    @Override
    @Nullable
    public ProSportAccount getAccountByName(@NonNull final String name) {
        return getBaseAccountManager().getAccountByName(name);
    }

    @Override
    @Nullable
    public List<ProSportAccount> getAccounts() {
        return getBaseAccountManager().getAccounts();
    }

    @Override
    @NonNull
    public Observable<String> getToken(
        @NonNull final ProSportAccount proSportAccount) {
        return getBaseAccountManager().getToken(proSportAccount);
    }

    @Override
    public void invalidateToken(@NonNull final String authToken) {
        getBaseAccountManager().invalidateToken(authToken);
    }

    @Override
    @NonNull
    public String getAccountType() {
        return getBaseAccountManager().getAccountType();
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Activity _activity;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final BaseAccountManager<ProSportAccount> _baseAccountManager;
}
