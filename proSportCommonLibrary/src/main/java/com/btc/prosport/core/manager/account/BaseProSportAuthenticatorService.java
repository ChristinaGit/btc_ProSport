package com.btc.prosport.core.manager.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;

public class BaseProSportAuthenticatorService extends Service {
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

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();

        onInjectMembers();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();

        onReleaseInjectedMembers();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @CallSuper
    protected void onInjectMembers() {
    }

    @CallSuper
    protected void onReleaseInjectedMembers() {
    }
}
