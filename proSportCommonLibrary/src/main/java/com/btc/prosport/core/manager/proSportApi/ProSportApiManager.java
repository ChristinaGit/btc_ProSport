package com.btc.prosport.core.manager.proSportApi;

import android.support.annotation.NonNull;

import com.btc.prosport.api.ProSportApi;

public interface ProSportApiManager {
    @NonNull
    ProSportApi getProSportApi();
}
