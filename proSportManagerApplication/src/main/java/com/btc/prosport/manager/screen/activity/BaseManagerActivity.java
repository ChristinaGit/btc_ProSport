package com.btc.prosport.manager.screen.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.prosport.di.screen.ProSportScreenComponent;
import com.btc.prosport.di.screen.module.ProSportAccountScreenModule;
import com.btc.prosport.di.screen.module.ProSportAdviserScreenModule;
import com.btc.prosport.di.screen.module.ProSportAuthScreenModule;
import com.btc.prosport.di.screen.module.ProSportRxScreenModule;
import com.btc.prosport.di.screen.module.ProSportSystemScreenModule;
import com.btc.prosport.manager.di.ManagerApplicationComponentProvider;
import com.btc.prosport.manager.di.ManagerScreenComponentProvider;
import com.btc.prosport.manager.di.application.ManagerApplicationComponent;
import com.btc.prosport.manager.di.screen.ManagerScreenComponent;
import com.btc.prosport.manager.di.screen.module.ManagerManagerScreenModule;
import com.btc.prosport.manager.di.screen.module.ManagerPresenterScreenModule;
import com.btc.prosport.screen.activity.BaseProSportActivity;

@Accessors(prefix = "_")
public abstract class BaseManagerActivity extends BaseProSportActivity
    implements ManagerScreenComponentProvider {

    @NonNull
    public final ManagerApplicationComponent getManagerApplicationComponent() {
        val application = getApplication();
        if (application instanceof ManagerApplicationComponentProvider) {
            final val componentProvider = (ManagerApplicationComponentProvider) application;
            return componentProvider.getManagerApplicationComponent();
        } else {
            throw new IllegalStateException("The application must implement " +
                                            ManagerApplicationComponentProvider.class.getName());
        }
    }

    @Override
    @NonNull
    public final ManagerScreenComponent getManagerScreenComponent() {
        if (_managerScreenComponent == null) {
            throw new IllegalStateException("The activity has not yet been created.");
        }

        return _managerScreenComponent;
    }

    @NonNull
    protected ManagerScreenComponent onCreateMangerScreenComponent() {
        return getManagerApplicationComponent().addManagerScreenComponent(
            new ManagerPresenterScreenModule(),
            new ManagerManagerScreenModule(),
            new ProSportSystemScreenModule(this),
            new ProSportAdviserScreenModule(this),
            new ProSportRxScreenModule(this),
            new ProSportAuthScreenModule(),
            new ProSportAccountScreenModule());
    }

    @NonNull
    @Override
    protected ProSportScreenComponent onCreateProSportScreenComponent() {
        return _managerScreenComponent = onCreateMangerScreenComponent();
    }

    @Nullable
    private ManagerScreenComponent _managerScreenComponent;
}
