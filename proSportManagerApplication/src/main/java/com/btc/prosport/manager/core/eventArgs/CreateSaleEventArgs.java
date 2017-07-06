package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.api.model.utility.SaleType;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;

import java.util.List;

@Accessors(prefix = "_")
public class CreateSaleEventArgs extends EventArgs {
    public CreateSaleEventArgs(
        @NonNull final List<RepeatableIntervalItem> saleIntervals,
        @NonNull final List<Long> playgroundIds,
        @NonNull final SaleType saleType,
        final int saleValue) {
        Contracts.requireNonNull(saleIntervals, "saleIntervals == null");
        Contracts.requireNonNull(playgroundIds, "playgroundIds == null");
        Contracts.requireNonNull(saleType, "saleType == null");

        _saleIntervals = saleIntervals;
        _playgroundIds = playgroundIds;
        _saleType = saleType;
        _saleValue = saleValue;
    }

    @Getter
    @NonNull
    private final List<RepeatableIntervalItem> _saleIntervals;

    @Getter
    @NonNull
    private final List<Long> _playgroundIds;

    @Getter
    @NonNull
    private final SaleType _saleType;

    @Getter
    private final int _saleValue;
}
