package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.api.model.utility.OrderState;

@ToString
@Accessors(prefix = "_")
public class ChangeOrderStateEventArgs extends EventArgs {
    public ChangeOrderStateEventArgs(
        final long orderId,
        @NonNull final OrderState oldState,
        @Nullable final OrderState newState) {
        Contracts.requireNonNull(oldState, "oldState == null");

        _orderId = orderId;
        _oldState = oldState;
        _newState = newState;
    }

    @Getter
    @Nullable
    private final OrderState _newState;

    @Getter
    @NonNull
    private final OrderState _oldState;

    @Getter
    private final long _orderId;
}
