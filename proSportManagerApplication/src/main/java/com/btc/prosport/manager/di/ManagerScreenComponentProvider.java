package com.btc.prosport.manager.di;

import android.support.annotation.NonNull;

import com.btc.prosport.manager.di.screen.ManagerScreenComponent;

public interface ManagerScreenComponentProvider {
    @NonNull
    ManagerScreenComponent getManagerScreenComponent();
}
