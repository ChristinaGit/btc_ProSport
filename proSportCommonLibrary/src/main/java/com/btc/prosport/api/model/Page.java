package com.btc.prosport.api.model;

import android.support.annotation.Nullable;

import java.util.List;

public interface Page<TEntity> {
    @Nullable
    List<TEntity> getEntries();

    int getEntriesCount();

    @Nullable
    String getNextPageUri();

    @Nullable
    String getPreviousPageUri();
}
