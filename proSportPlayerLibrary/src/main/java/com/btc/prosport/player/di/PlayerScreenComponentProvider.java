package com.btc.prosport.player.di;

import android.support.annotation.NonNull;

import com.btc.prosport.player.di.screen.PlayerScreenComponent;

public interface PlayerScreenComponentProvider {
    @NonNull
    PlayerScreenComponent getPlayerScreenComponent();
}
