package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface Sale {
    @Nullable
    String getCreateDate();

    @Nullable
    String getDescription();

    long getId();

    @Nullable
    List<? extends SaleMetadataInterval> getIntervals();

    @Nullable
    List<? extends PlaygroundTitle> getPlaygrounds();

    int getSaleTypeCode();

    @Nullable
    List<? extends SportComplexTitle> getSportComplexes();

    int getValue();
}
