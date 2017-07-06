package com.btc.prosport.di;

import android.support.annotation.NonNull;

import com.btc.prosport.di.application.ProSportApplicationComponent;

public interface ProSportApplicationComponentProvider {
    @NonNull
    ProSportApplicationComponent getProSportApplicationComponent();
}
