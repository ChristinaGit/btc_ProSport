package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;

import java.util.List;

@Accessors(prefix = "_")
public class CreateOrderEventArgs extends BaseCreateOrderEventArgs {
    public CreateOrderEventArgs(
        final long playerId,
        final long playgroundId,
        @Nullable final List<RepeatableIntervalItem> orderIntervals,
        @Nullable final Integer price) {
        super(playgroundId, orderIntervals, price);

        _playerId = playerId;
    }

    @Getter
    private final long _playerId;
}
