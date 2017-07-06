package com.btc.prosport.di.subscreen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.rx.AndroidRxManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.subscreen.SubscreenScope;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@SubscreenScope
public class ProSportRxSubscreenModule {
    public ProSportRxSubscreenModule(
        @NonNull final LifecycleProvider<FragmentEvent> lifecycleProvider) {
        Contracts.requireNonNull(lifecycleProvider, "lifecycleProvider == null");

        _lifecycleProvider = lifecycleProvider;
    }

    @Named(ScopeNames.SUBSCREEN)
    @Provides
    @SubscreenScope
    @NonNull
    public final LifecycleProvider<FragmentEvent> provideLifecycleProvider() {
        return _lifecycleProvider;
    }

    @Named(ScopeNames.SUBSCREEN)
    @Provides
    @SubscreenScope
    @NonNull
    public final RxManager provideRxManager(
        @NonNull @Named(ScopeNames.SUBSCREEN)
        final LifecycleProvider<FragmentEvent> lifecycleProvider) {
        Contracts.requireNonNull(lifecycleProvider, "lifecycleProvider == null");

        return new AndroidRxManager<>(lifecycleProvider);
    }

    @NonNull
    private final LifecycleProvider<FragmentEvent> _lifecycleProvider;
}
