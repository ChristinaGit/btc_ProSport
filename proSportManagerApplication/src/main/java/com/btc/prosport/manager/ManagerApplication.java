package com.btc.prosport.manager;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.prosport.BaseProSportApplication;
import com.btc.prosport.api.model.utility.UserRole;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.btc.prosport.di.application.module.ProSportAccountApplicationModule;
import com.btc.prosport.di.application.module.ProSportApiApplicationModule;
import com.btc.prosport.di.application.module.ProSportManagerApplicationModule;
import com.btc.prosport.di.application.module.ProSportSystemApplicationModule;
import com.btc.prosport.manager.core.manager.notification.ManagerProSportNotificationManager;
import com.btc.prosport.manager.di.ManagerApplicationComponentProvider;
import com.btc.prosport.manager.di.application.DaggerManagerApplicationComponent;
import com.btc.prosport.manager.di.application.ManagerApplicationComponent;

public final class ManagerApplication extends BaseProSportApplication
    implements ManagerApplicationComponentProvider {

    @Override
    @NonNull
    public final ManagerApplicationComponent getManagerApplicationComponent() {
        if (_managerApplicationComponent == null) {
            throw new IllegalStateException("The application has not yet been created.");
        }

        return _managerApplicationComponent;
    }

    @NonNull
    protected ManagerApplicationComponent onCreateManagerApplicationComponent() {
        val userSettingsHelper =
            new UserSettingsHelper(PreferenceManager.getDefaultSharedPreferences(this), this);

        //@formatter:off
        return DaggerManagerApplicationComponent
            .builder()
            .proSportSystemApplicationModule(new ProSportSystemApplicationModule(this, R.xml.tracker_config_manager, !BuildConfig.DEBUG))
            .proSportManagerApplicationModule(new ProSportManagerApplicationModule(new ManagerProSportNotificationManager(this, userSettingsHelper), userSettingsHelper))
            .proSportApiApplicationModule(new ProSportApiApplicationModule(UserRole.MANGER, true))
            .proSportAccountApplicationModule(new ProSportAccountApplicationModule())
            .build();
        //@formatter:on
    }

    @NonNull
    @Override
    protected ProSportApplicationComponent onCreateProSportApplicationComponent() {
        return _managerApplicationComponent = onCreateManagerApplicationComponent();
    }

    @Nullable
    private ManagerApplicationComponent _managerApplicationComponent;
}
