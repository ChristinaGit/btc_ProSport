package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface SportComplexReport {
    long getId();

    @Nullable
    String getName();

    @Nullable
    List<? extends PlaygroundReport> getPlaygrounds();

    @Nullable
    Integer getNotConfirmedOrdersCount();
}
