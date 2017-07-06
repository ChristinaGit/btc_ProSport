package com.btc.prosport.manager.core.adapter.orderStatusAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.listView.adapter.ArrayAdapter;
import com.btc.prosport.api.model.utility.OrderState;
import com.btc.prosport.manager.R;

public final class OrderStatesAdapter extends ArrayAdapter<OrderState> {
    public OrderStatesAdapter(@NonNull final Context context) {
        super(
            Contracts.requireNonNull(context, "context == null"),
            R.layout.layout_dialog_item,
            OrderState.values());
        setDropDownViewResource(R.layout.layout_dialog_dropdown_item);
    }

    @Nullable
    @Override
    protected CharSequence getItemName(@Nullable final OrderState item) {
        if (OrderState.CONFIRMED == item) {
            return getContext().getString(R.string.orders_state_confirmed);
        } else if (OrderState.NOT_CONFIRMED == item) {
            return getContext().getString(R.string.orders_state_not_confirmed);
        } else if (OrderState.CANCELED == item) {
            return getContext().getString(R.string.orders_state_canceled);
        } else {
            return super.getItemName(item);
        }
    }
}