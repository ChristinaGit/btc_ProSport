package com.btc.prosport.player.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.common.control.manager.dialer.ActivityDialerManager;
import com.btc.common.control.manager.dialer.DialerManager;
import com.btc.common.control.manager.permission.ActivityPermissionManager;
import com.btc.common.control.manager.permission.PermissionManager;
import com.btc.common.control.manager.picker.ActivityTimePickerManager;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.control.manager.rx.RxManager;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.ActivityPhotoManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.di.screen.module.ProSportManagerScreenModule;
import com.btc.prosport.player.core.manager.googleApiClient.ActivityGoogleApiManager;
import com.btc.prosport.player.core.manager.googleApiClient.GoogleApiManager;
import com.btc.prosport.player.core.manager.googleApiClient.GoogleApiMethod;
import com.btc.prosport.player.core.manager.navigation.ActivityPlayerNavigationManager;
import com.btc.prosport.player.core.manager.navigation.PlayerNavigationManager;
import com.btc.prosport.player.di.qualifier.PresenterNames;

import java.util.Arrays;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class PlayerManagerScreenModule extends ProSportManagerScreenModule {
    @Provides
    @ScreenScope
    @NonNull
    public final TimePickerManager provideDataPickerManager(
        @NonNull @Named(ScopeNames.SCREEN) final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityTimePickerManager(observableActivity, resourceAdviser);
    }

    @Provides
    @ScreenScope
    @NonNull
    public final DialerManager provideDialerManager(
        @NonNull final PermissionManager permissionManager,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(permissionManager, "permissionManager == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityDialerManager(permissionManager, observableActivity);
    }

    @Provides
    @ScreenScope
    @NonNull
    public final PermissionManager providePermissionManager(
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityPermissionManager(observableActivity, "btc:request_permissions");
    }

    @Provides
    @ScreenScope
    @NonNull
    public final PhotoManager providePhotoManager(
        @NonNull final PermissionManager permissionManager,
        @NonNull final ObservableActivity observableActivity,
        @NonNull final ProSportNavigationManager proSportNavigationManager) {
        Contracts.requireNonNull(permissionManager, "permissionManager == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");
        Contracts.requireNonNull(proSportNavigationManager, "proSportNavigationManager == null");

        return new ActivityPhotoManager(permissionManager,
                                        observableActivity,
                                        proSportNavigationManager);
    }

    @Provides
    @ScreenScope
    @NonNull
    public final PlayerNavigationManager providePlayerNavigationManager(
        @NonNull @Named(ScopeNames.SCREEN) final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityPlayerNavigationManager(resourceAdviser, observableActivity);
    }

    @Provides
    @ScreenScope
    @Named(PresenterNames.SPORT_COMPLEXES_VIEWER)
    @NonNull
    public final GoogleApiManager provideSportComplexViewerGoogleApiManager(
        @NonNull final ObservableActivity observableActivity,
        @NonNull @Named(ScopeNames.SCREEN) final RxManager rxManager,
        @NonNull final PermissionManager permissionManager) {
        Contracts.requireNonNull(observableActivity, "observableActivity == null");
        Contracts.requireNonNull(rxManager, "rxManager == null");
        Contracts.requireNonNull(permissionManager, "permissionManager == null");

        return new ActivityGoogleApiManager(observableActivity,
                                            rxManager,
                                            permissionManager,
                                            Arrays.asList(GoogleApiMethod.LAST_LOCATION));
    }
}
