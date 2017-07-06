package com.btc.prosport.player.screen.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.User;
import com.btc.prosport.player.screen.PlayerAccountScreen;

@Accessors(prefix = "_")
public abstract class BasePlayerAccountActivity extends BasePlayerActivity
    implements PlayerAccountScreen {
    @CallSuper
    @Override
    public void displayNoAccountsError() {
    }

    @CallSuper
    @Override
    public void displayPlayer(@Nullable final User player) {
        setPlayer(player);
    }

    @CallSuper
    @Override
    public void displayPlayerLoading() {
    }

    @CallSuper
    @Override
    public void displayPlayerLoadingError() {
    }

    @CallSuper
    protected void onPlayerChanged() {
    }

    @Getter(AccessLevel.PROTECTED)
    @Nullable
    private User _player;

    private void setPlayer(@Nullable final User player) {
        if (_player != player) {
            _player = player;

            onPlayerChanged();
        }
    }
}
