package com.btc.prosport.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.mvp.presenter.Presenter;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.di.qualifier.PresenterNames;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.presenter.ProSportVerificationPresenter;
import com.btc.prosport.screen.ProSportVerificationScreen;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ProSportPresenterScreenModule {
    @Named(PresenterNames.AUTH)
    @Provides
    @ScreenScope
    @NonNull
    public final Presenter<ProSportVerificationScreen> provideAuthPresenter(
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new ProSportVerificationPresenter(proSportNavigationManager);
    }
}
