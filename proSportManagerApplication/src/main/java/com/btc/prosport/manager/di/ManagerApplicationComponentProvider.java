package com.btc.prosport.manager.di;

import android.support.annotation.NonNull;

import com.btc.prosport.manager.di.application.ManagerApplicationComponent;

public interface ManagerApplicationComponentProvider {
    @NonNull
    ManagerApplicationComponent getManagerApplicationComponent();
}
