package com.btc.prosport.player.di;

import android.support.annotation.NonNull;

import com.btc.prosport.player.di.application.PlayerApplicationComponent;

public interface PlayerApplicationComponentProvider {
    @NonNull
    PlayerApplicationComponent getPlayerApplicationComponent();
}
