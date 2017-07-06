package com.btc.prosport.di;

import android.support.annotation.NonNull;

import com.btc.prosport.di.screen.ProSportScreenComponent;

public interface ProSportScreenComponentProvider {
    @NonNull
    ProSportScreenComponent getProSportScreenComponent();
}
