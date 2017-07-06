package com.btc.prosport.player.di;

import android.support.annotation.NonNull;

import com.btc.prosport.player.di.subscreen.PlayerSubscreenComponent;

public interface PlayerSubscreenComponentProvider {
    @NonNull
    PlayerSubscreenComponent getPlayerSubscreenComponent();
}
