package com.btc.prosport.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.common.control.manager.message.ActivityMessageManager;
import com.btc.common.control.manager.message.MessageManager;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.common.R;
import com.btc.prosport.core.manager.navigation.ActivityProSportNavigationManager;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportManagerScreenModule {
    @Provides
    @ScreenScope
    @NonNull
    public final MessageManager provideMessageManager(
        @NonNull @Named(ScopeNames.SCREEN) final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityMessageManager(observableActivity, R.id.coordinator);
    }

    @Provides
    @ScreenScope
    @NonNull
    public final ProSportNavigationManager provideProSportNavigationManager(
        @NonNull @Named(ScopeNames.SCREEN) final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityProSportNavigationManager(resourceAdviser,
                                                     observableActivity,
                                                     R.id.content_container);
    }
}
