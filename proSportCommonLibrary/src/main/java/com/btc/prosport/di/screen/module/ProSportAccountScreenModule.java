package com.btc.prosport.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.core.manager.account.AccountManager;
import com.btc.prosport.core.manager.account.ActivityProSportAccountManager;
import com.btc.prosport.core.manager.account.BaseAccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportServiceHelper.AndroidProSportAccountHelper;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportAccountHelper;
import com.btc.prosport.di.screen.ScreenScope;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportAccountScreenModule {
    @Provides
    @ScreenScope
    @NonNull
    public final AccountManager<ProSportAccount> provideAccountManager(
        @NonNull final android.accounts.AccountManager nativeAccountManager,
        @NonNull final BaseAccountManager<ProSportAccount> baseAccountManager,
        @NonNull final UserRole userRole,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(nativeAccountManager, "nativeAccountManager == null");
        Contracts.requireNonNull(baseAccountManager, "baseAccountManager == null");
        Contracts.requireNonNull(userRole, "userRole == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityProSportAccountManager(
            nativeAccountManager,
            userRole.getAccountType(),
            baseAccountManager,
            observableActivity.asActivity());
    }

    @Provides
    @ScreenScope
    @NonNull
    public final ProSportAccountHelper provideProSportAccountHelper(
        @NonNull final AccountManager<ProSportAccount> proSportAccountManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");

        return new AndroidProSportAccountHelper(proSportAccountManager);
    }
}
