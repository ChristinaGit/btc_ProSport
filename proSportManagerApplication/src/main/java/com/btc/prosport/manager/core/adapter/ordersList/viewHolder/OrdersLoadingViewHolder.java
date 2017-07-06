package com.btc.prosport.manager.core.adapter.ordersList.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;

public class OrdersLoadingViewHolder extends ExtendedRecyclerViewHolder {
    public OrdersLoadingViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
    }
}
