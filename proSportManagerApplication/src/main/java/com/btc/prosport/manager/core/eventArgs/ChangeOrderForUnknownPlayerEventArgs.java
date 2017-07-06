package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.core.adapter.repeatableIntervals.item.RepeatableIntervalItem;

import java.util.List;

@Accessors(prefix = "_")
public class ChangeOrderForUnknownPlayerEventArgs extends BaseChangeOrderEventArgs {
    public ChangeOrderForUnknownPlayerEventArgs(
        final long orderId,
        @NonNull final String playerPhoneNumber,
        @Nullable final String playerFirstName,
        @Nullable final String lastName,
        final long playgroundId,
        @Nullable final List<RepeatableIntervalItem> orderIntervals,
        @Nullable final Integer price) {
        super(
            orderId,
            playgroundId,
            Contracts.requireNonNull(orderIntervals, "orderIntervals == null"),
            price);

        _playerPhoneNumber = playerPhoneNumber;
        _playerFirstName = playerFirstName;
        _playerLastName = lastName;
    }

    @Getter
    @Nullable
    private final String _playerFirstName;

    @Getter
    @Nullable
    private final String _playerLastName;

    @Getter
    @NonNull
    private final String _playerPhoneNumber;
}
