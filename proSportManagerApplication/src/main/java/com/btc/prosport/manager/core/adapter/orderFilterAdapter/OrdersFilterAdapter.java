package com.btc.prosport.manager.core.adapter.orderFilterAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.listView.adapter.ArrayAdapter;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.OrderStatusFilter;

public final class OrdersFilterAdapter extends ArrayAdapter<OrderStatusFilter> {
    public OrdersFilterAdapter(@NonNull final Context context) {
        super(
            Contracts.requireNonNull(context, "context == null"),
            R.layout.layout_spinner_item,
            OrderStatusFilter.values());

        setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
    }

    @Nullable
    @Override
    protected CharSequence getItemName(@Nullable final OrderStatusFilter item) {
        if (item == OrderStatusFilter.ALL) {
            return getContext().getString(R.string.orders_filter_all);
        } else if (item == OrderStatusFilter.CONFIRMED) {
            return getContext().getString(R.string.orders_filter_confirmed);
        } else if (item == OrderStatusFilter.NOT_CONFIRMED) {
            return getContext().getString(R.string.orders_filter_not_confirmed);
        } else if (item == OrderStatusFilter.CANCELED) {
            return getContext().getString(R.string.orders_filter_canceled);
        } else {
            return super.getItemName(item);
        }
    }
}