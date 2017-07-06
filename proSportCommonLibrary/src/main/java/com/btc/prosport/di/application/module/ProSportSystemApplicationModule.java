package com.btc.prosport.di.application.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.XmlRes;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.di.application.ApplicationScope;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import dagger.Module;
import dagger.Provides;

@Module
@ApplicationScope
public class ProSportSystemApplicationModule {
    public ProSportSystemApplicationModule(
        @NonNull final Context context,
        @XmlRes final int trackerConfigId,
        final boolean enableAnalytics) {
        Contracts.requireNonNull(context, "context == null");

        _context = context;
        _trackerConfigId = trackerConfigId;
        _enableAnalytics = enableAnalytics;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final Context provideContext() {
        return _context;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final GoogleAnalytics provideGoogleAnalytics(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        final val googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.setDryRun(!_enableAnalytics);

        return googleAnalytics;
    }

    @Provides
    @ApplicationScope
    @NonNull
    public final Tracker provideTracker(@NonNull final GoogleAnalytics googleAnalytics) {
        final val tracker = googleAnalytics.newTracker(_trackerConfigId);

        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        return tracker;
    }

    @NonNull
    private final Context _context;

    private final boolean _enableAnalytics;

    @XmlRes
    private final int _trackerConfigId;
}
