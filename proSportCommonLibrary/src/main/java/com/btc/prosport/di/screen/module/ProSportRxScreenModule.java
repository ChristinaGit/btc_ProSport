package com.btc.prosport.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.rx.AndroidRxManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportRxScreenModule {
    public ProSportRxScreenModule(
        @NonNull final LifecycleProvider<ActivityEvent> lifecycleProvider) {
        Contracts.requireNonNull(lifecycleProvider, "lifecycleProvider == null");

        _lifecycleProvider = lifecycleProvider;
    }

    @Named(ScopeNames.SCREEN)
    @Provides
    @ScreenScope
    @NonNull
    public final LifecycleProvider<ActivityEvent> provideLifecycleProvider() {
        return _lifecycleProvider;
    }

    @Named(ScopeNames.SCREEN)
    @Provides
    @ScreenScope
    @NonNull
    public final RxManager provideRxManager(
        @NonNull @Named(ScopeNames.SCREEN)
        final LifecycleProvider<ActivityEvent> lifecycleProvider) {
        Contracts.requireNonNull(lifecycleProvider, "lifecycleProvider == null");

        return new AndroidRxManager<>(lifecycleProvider);
    }

    @NonNull
    private final LifecycleProvider<ActivityEvent> _lifecycleProvider;
}
