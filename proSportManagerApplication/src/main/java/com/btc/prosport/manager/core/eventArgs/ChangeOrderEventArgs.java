package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;

import java.util.List;

@Accessors(prefix = "_")
public class ChangeOrderEventArgs extends BaseChangeOrderEventArgs {
    public ChangeOrderEventArgs(
        final long orderId,
        final long playerId,
        final long playgroundId,
        @Nullable final List<RepeatableIntervalItem> orderIntervals,
        @Nullable final Integer price) {
        super(orderId, playgroundId, orderIntervals, price);

        _playerId = playerId;
    }

    @Getter
    private final long _playerId;
}
