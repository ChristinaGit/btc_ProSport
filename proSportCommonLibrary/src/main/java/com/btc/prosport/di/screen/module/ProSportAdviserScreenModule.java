package com.btc.prosport.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportAdviserScreenModule {
    public ProSportAdviserScreenModule(@NonNull final ResourceAdviser resourceAdviser) {
        Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null");

        _resourceAdviser = resourceAdviser;
    }

    @Named(ScopeNames.SCREEN)
    @Provides
    @ScreenScope
    @NonNull
    public final ResourceAdviser provideResourceAdviser() {
        return _resourceAdviser;
    }

    @NonNull
    private final ResourceAdviser _resourceAdviser;
}
