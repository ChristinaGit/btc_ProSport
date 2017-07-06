package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.manager.core.OrdersSortOrder;
import com.btc.prosport.manager.screen.activity.workspace.OrdersFilterParams;

@ToString
@Accessors(prefix = "_")
public class OrdersParamsEventArgs extends EventArgs {
    public OrdersParamsEventArgs(
        @Nullable final OrdersFilterParams ordersFilterParams,
        @Nullable final OrdersSortOrder ordersSortOrder) {
        _ordersFilterParams = ordersFilterParams;
        _ordersSortOrder = ordersSortOrder;
    }

    @Getter
    @Nullable
    private final OrdersFilterParams _ordersFilterParams;

    @Getter
    @Nullable
    private final OrdersSortOrder _ordersSortOrder;
}
