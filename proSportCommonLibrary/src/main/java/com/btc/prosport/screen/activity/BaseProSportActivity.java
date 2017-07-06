package com.btc.prosport.screen.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.extension.activity.ScreenActivity;
import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.ProSportScreenComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportManagerScreenModule;
import com.btc.prosport.di.screen.module.ProSportPresenterScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

@Accessors(prefix = "_")
public abstract class BaseProSportActivity extends ScreenActivity
    implements ProSportScreenComponentProvider {

    @NonNull
    public final ProSportApplicationComponent getProSportApplicationComponent() {
        val application = getApplication();
        if (application instanceof ProSportApplicationComponentProvider) {
            final val componentProvider = (ProSportApplicationComponentProvider) application;
            return componentProvider.getProSportApplicationComponent();
        } else {
            throw new IllegalStateException("The application must implement " +
                                            ProSportApplicationComponentProvider.class.getName());
        }
    }

    @Override
    @NonNull
    public final ProSportScreenComponent getProSportScreenComponent() {
        if (_proSportScreenComponent == null) {
            throw new IllegalStateException("The activity has not yet been created.");
        }

        return _proSportScreenComponent;
    }

    @NonNull
    protected ProSportScreenComponent onCreateProSportScreenComponent() {
        //@formatter:off
        return getProSportApplicationComponent().addProSportScreenComponent(
                new ProSportSystemScreenModule(this),
                new ProSportAdviserScreenModule(this),
                new ProSportRxScreenModule(this),
                new ProSportPresenterScreenModule(),
                new ProSportAuthScreenModule(),
                new ProSportAccountScreenModule(),
                new ProSportManagerScreenModule());
        //@formatter:on
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        _proSportScreenComponent = onCreateProSportScreenComponent();
    }

    @Inject
    @Nullable
    @Getter(AccessLevel.PROTECTED)
    /*package-private*/ Tracker _analyticsTracker;

    @Nullable
    private ProSportScreenComponent _proSportScreenComponent;
}
