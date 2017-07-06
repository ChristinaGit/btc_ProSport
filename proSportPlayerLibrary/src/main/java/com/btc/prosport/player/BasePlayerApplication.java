package com.btc.prosport.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;

import com.btc.prosport.BaseProSportApplication;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.btc.prosport.player.di.PlayerApplicationComponentProvider;
import com.btc.prosport.player.di.application.PlayerApplicationComponent;

@Accessors(prefix = "_")
public abstract class BasePlayerApplication extends BaseProSportApplication
    implements PlayerApplicationComponentProvider {
    @Override
    @NonNull
    public final PlayerApplicationComponent getPlayerApplicationComponent() {
        if (_playerApplicationComponent == null) {
            throw new IllegalStateException("The application has not yet been created.");
        }

        return _playerApplicationComponent;
    }

    @NonNull
    @Override
    protected ProSportApplicationComponent onCreateProSportApplicationComponent() {
        return _playerApplicationComponent = onCreatePlayerApplicationComponent();
    }

    @NonNull
    protected abstract PlayerApplicationComponent onCreatePlayerApplicationComponent();

    @Nullable
    private PlayerApplicationComponent _playerApplicationComponent;
}
