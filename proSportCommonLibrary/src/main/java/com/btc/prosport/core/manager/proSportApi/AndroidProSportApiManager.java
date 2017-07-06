package com.btc.prosport.core.manager.proSportApi;

import android.content.Context;
import android.support.annotation.NonNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.ProSportApi;

@Accessors(prefix = "_")
public final class AndroidProSportApiManager implements ProSportApiManager {
    public AndroidProSportApiManager(
        @NonNull final Context context, @NonNull final ProSportApi proSportApi) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");

        _context = context;
        _proSportApi = proSportApi;
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Getter(onMethod = @__(@Override))
    @NonNull
    private final ProSportApi _proSportApi;
}
