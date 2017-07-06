package com.btc.prosport.di.application.module;

import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.core.manager.account.ApplicationProSportBaseAccountManager;
import com.btc.prosport.core.manager.account.BaseAccountManager;
import com.btc.prosport.core.manager.account.ProSportAccount;
import com.btc.prosport.core.manager.proSportServiceHelper.AndroidProSportBaseAccountHelper;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportBaseAccountHelper;
import com.btc.prosport.di.application.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
@ApplicationScope
public class ProSportAccountApplicationModule {
    @Provides
    @ApplicationScope
    @NonNull
    public final BaseAccountManager<ProSportAccount> provideAccountManager(
        @NonNull final AccountManager nativeAccountManager, @NonNull final UserRole userRole) {
        Contracts.requireNonNull(nativeAccountManager, "nativeAccountManager == null");
        Contracts.requireNonNull(userRole, "userRole == null");

        return new ApplicationProSportBaseAccountManager(nativeAccountManager,
                                                         userRole.getAccountType());
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final AccountManager provideNativeAccountManager(
        @NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return AccountManager.get(context);
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportBaseAccountHelper provideProSportAccountHelper(
        @NonNull final BaseAccountManager<ProSportAccount> proSportAccountManager) {
        Contracts.requireNonNull(proSportAccountManager, "proSportAccountManager == null");

        return new AndroidProSportBaseAccountHelper(proSportAccountManager);
    }
}
