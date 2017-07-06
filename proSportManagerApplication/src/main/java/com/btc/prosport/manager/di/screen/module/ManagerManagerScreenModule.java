package com.btc.prosport.manager.di.screen.module;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;
import com.btc.common.control.adviser.ResourceAdviser;
import com.btc.common.control.manager.dialer.ActivityDialerManager;
import com.btc.common.control.manager.dialer.DialerManager;
import com.btc.common.control.manager.permission.ActivityPermissionManager;
import com.btc.common.control.manager.permission.PermissionManager;
import com.btc.common.control.manager.picker.ActivityTimePickerManager;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.extension.activity.ObservableActivity;
import com.btc.prosport.core.manager.navigation.ProSportNavigationManager;
import com.btc.prosport.core.manager.photo.ActivityPhotoManager;
import com.btc.prosport.core.manager.photo.PhotoManager;
import com.btc.prosport.di.qualifier.ScopeNames;
import com.btc.prosport.di.screen.ScreenScope;
import com.btc.prosport.di.screen.module.ProSportManagerScreenModule;
import com.btc.prosport.manager.core.manager.navigation.ActivityManagerNavigationManager;
import com.btc.prosport.manager.core.manager.navigation.ManagerNavigationManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
@ScreenScope
public class ManagerManagerScreenModule extends ProSportManagerScreenModule {
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
    public final ManagerNavigationManager provideManagerNavigationManager(
        @NonNull @Named(ScopeNames.SCREEN) final ResourceAdviser resourceAdviser,
        @NonNull final ObservableActivity observableActivity) {
        Contracts.requireNonNull(resourceAdviser, "resourceAdviser == null");
        Contracts.requireNonNull(observableActivity, "observableActivity == null");

        return new ActivityManagerNavigationManager(resourceAdviser, observableActivity);
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
                                        observableActivity, proSportNavigationManager);
    }
}
