package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Accessors(prefix = "_")
public abstract class BaseCreateOrderEventArgs extends EventArgs {
    protected BaseCreateOrderEventArgs(
        final long playgroundId,
        @Nullable final List<RepeatableIntervalItem> orderIntervals,
        @Nullable final Integer price) {
        _playgroundId = playgroundId;
        _orderIntervals = orderIntervals;
        _price = price;
    }

    @Getter
    @Nullable
    private final List<RepeatableIntervalItem> _orderIntervals;

    @Getter
    private final long _playgroundId;

    @Getter
    @Nullable
    private final Integer _price;
}
