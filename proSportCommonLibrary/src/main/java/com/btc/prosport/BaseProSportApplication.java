package com.btc.prosport;

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;

import com.btc.common.ConstantBuilder;
import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;

@Accessors(prefix = "_")
public abstract class BaseProSportApplication extends Application
    implements ProSportApplicationComponentProvider {
    private static final String _LOG_TAG = ConstantBuilder.logTag(BaseProSportApplication.class);

    @Override
    @NonNull
    public final ProSportApplicationComponent getProSportApplicationComponent() {
        if (_proSportApplicationComponent == null) {
            throw new IllegalStateException("The application has not yet been created.");
        }

        return _proSportApplicationComponent;
    }

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();

        _proSportApplicationComponent = onCreateProSportApplicationComponent();
    }

    protected void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                       .detectAll()
                                       .penaltyLog()
                                       .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                   .detectLeakedSqlLiteObjects()
                                   .detectLeakedClosableObjects()
                                   .penaltyLog()
                                   .penaltyDeath()
                                   .build());
    }

    @NonNull
    protected abstract ProSportApplicationComponent onCreateProSportApplicationComponent();

    @Nullable
    private ProSportApplicationComponent _proSportApplicationComponent;
}
