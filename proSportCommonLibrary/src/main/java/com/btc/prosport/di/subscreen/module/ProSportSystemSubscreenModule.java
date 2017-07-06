package com.btc.prosport.di.subscreen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.fragment.ObservableFragment;
import com.btc.prosport.di.subscreen.SubscreenScope;

import dagger.Module;
import dagger.Provides;

@Module
@SubscreenScope
public class ProSportSystemSubscreenModule {
    public ProSportSystemSubscreenModule(@NonNull final ObservableFragment observableFragment) {
        Contracts.requireNonNull(observableFragment, "observableFragment == null");

        _observableFragment = observableFragment;
    }

    @Provides
    @SubscreenScope
    @NonNull
    public final ObservableFragment provideObservableFragment() {
        return _observableFragment;
    }

    @NonNull
    private final ObservableFragment _observableFragment;
}
