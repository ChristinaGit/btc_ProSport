package com.btc.prosport.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.di.screen.ScreenScope;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportSystemScreenModule {
    public ProSportSystemScreenModule(@NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        _observableActivity = observableActivity;
    }

    @Provides
    @ScreenScope
    @NonNull
    public final ObservableActivity provideObservableActivity() {
        return _observableActivity;
    }

    @NonNull
    private final ObservableActivity _observableActivity;
}
