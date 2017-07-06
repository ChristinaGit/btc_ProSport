package com.btc.prosport.di.screen.module;

import android.accounts.AccountManager;
import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.core.manager.account.AccountContracts;
import com.btc.prosport.core.manager.auth.AuthManager;
import com.btc.prosport.core.manager.auth.ProSportAuthManager;
import com.btc.prosport.core.manager.credential.CredentialManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.di.screen.ScreenScope;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportAuthScreenModule {
    @Provides
    @ScreenScope
    @NonNull
    public final AuthManager provideProSportManagerAuthManager(
        @NonNull final UserRole userRole,
        @NonNull final ProSportApiManager proSportApiManager,
        @NonNull final CredentialManager credentialManager,
        @NonNull final AccountManager accountManager) {
        Contracts.requireNonNull(proSportApiManager, "proSportApiManager == null");
        Contracts.requireNonNull(credentialManager, "credentialManager == null");
        Contracts.requireNonNull(accountManager, "accountManager == null");

        return new ProSportAuthManager(
            proSportApiManager,
            credentialManager,
            accountManager,
            userRole.getAccountType(),
            AccountContracts.TOKEN_TYPE_FULL_ACCESS);
    }
}
