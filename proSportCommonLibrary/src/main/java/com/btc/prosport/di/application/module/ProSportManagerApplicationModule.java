package com.btc.prosport.di.application.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.task.AndroidTaskManager;
import com.btc.common.control.manager.task.TaskManager;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.core.manager.credential.CredentialManager;
import com.btc.prosport.core.manager.credential.ProSportCredentialManager;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.core.manager.firebaseMessaging.ProSportMessagingManager;
import com.btc.prosport.core.manager.notification.NotificationType;
import com.btc.prosport.core.manager.notification.ProSportNotificationManager;
import com.btc.prosport.core.manager.proSportApi.AndroidProSportApiManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportBaseAccountHelper;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.di.application.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
@ApplicationScope
@Accessors(prefix = "_")
public class ProSportManagerApplicationModule {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ProSportManagerApplicationModule.class);

    public ProSportManagerApplicationModule(
        @NonNull
        final ProSportNotificationManager<? extends NotificationType> proSportNotificationManager,
        @NonNull final UserSettingsHelper userSettingsHelper) {
        Contracts.requireNonNull(proSportNotificationManager,
                                 "proSportNotificationManager == null");
        Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null");

        _proSportNotificationManager = proSportNotificationManager;
        _userSettingsHelper = userSettingsHelper;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final CredentialManager provideCredentialManager(
        @NonNull final ApplicationInfo applicationInfo) {
        Contracts.requireNonNull(applicationInfo, "applicationInfo == null");

        return new ProSportCredentialManager(applicationInfo);
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final FirebaseMessagingManager provideMessagingManager(
        @NonNull final Context context,
        @NonNull final ProSportApi proSportApi,
        @NonNull final ProSportBaseAccountHelper proSportBaseAccountHelper) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");
        Contracts.requireNonNull(proSportBaseAccountHelper, "proSportBaseAccountHelper == null");

        return new ProSportMessagingManager(context, proSportApi, proSportBaseAccountHelper);
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ApplicationInfo provideMetadataApplicationInfo(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        ApplicationInfo applicationInfo;

        try {
            applicationInfo = context
                .getPackageManager()
                .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (final PackageManager.NameNotFoundException exception) {
            Log.w(_LOG_TAG, exception.getMessage(), exception);
            applicationInfo = null;
        }
        if (applicationInfo == null) {
            throw new RuntimeException("Do not get the application info");
        }

        return applicationInfo;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportApiManager provideProSportApiManager(
        @NonNull final Context context, @NonNull final ProSportApi proSportApi) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");

        return new AndroidProSportApiManager(context, proSportApi);
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final ProSportNotificationManager<NotificationType> provideProSportNotificationManager
        () {
        // FIXME: 03.05.2017 Fix cast.
        return (ProSportNotificationManager<NotificationType>) getProSportNotificationManager();
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final TaskManager provideTaskManager() {
        return new AndroidTaskManager();
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final UserSettingsHelper provideUserSettingsHelper(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        return _userSettingsHelper;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportNotificationManager<? extends NotificationType>
        _proSportNotificationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final UserSettingsHelper _userSettingsHelper;
}
