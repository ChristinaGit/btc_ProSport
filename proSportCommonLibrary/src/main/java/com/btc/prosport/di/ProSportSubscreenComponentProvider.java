package com.btc.prosport.di;

import android.support.annotation.NonNull;

import com.btc.prosport.di.subscreen.ProSportSubscreenComponent;

public interface ProSportSubscreenComponentProvider {
    @NonNull
    ProSportSubscreenComponent getProSportSubscreenComponent();
}
