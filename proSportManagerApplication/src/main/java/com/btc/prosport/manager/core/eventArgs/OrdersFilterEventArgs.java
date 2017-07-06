package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.manager.core.FilterType;

@Accessors(prefix = "_")
public class OrdersFilterEventArgs extends EventArgs {
    public OrdersFilterEventArgs(
        @NonNull final FilterType filterType, final long id, @Nullable final String name) {
        Contracts.requireNonNull(filterType, "filterType == null");

        _filterType = filterType;
        _id = id;
        _name = name;
    }

    @Getter
    @NonNull
    private final FilterType _filterType;

    @Getter
    private final long _id;

    @Getter
    @Nullable
    private final String _name;
}
