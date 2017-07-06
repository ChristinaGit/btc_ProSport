package com.btc.prosport.player.core.adapter.ordersList;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class OrderViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final CardView orderCardView;

    @NonNull
    public final TextView orderDateView;

    @NonNull
    public final TextView orderIdView;

    @NonNull
    public final TextView orderIntervalsView;

    @NonNull
    public final View orderMoreView;

    @NonNull
    public final TextView orderPlaceView;

    @NonNull
    public final TextView orderPriceView;

    @NonNull
    public final View orderStateMarkerView;

    @NonNull
    public final TextView orderStateView;

    public OrderViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        orderCardView = (CardView) itemView.findViewById(R.id.order_card);
        orderIdView = (TextView) itemView.findViewById(R.id.order_id);
        orderMoreView = itemView.findViewById(R.id.order_more);
        orderDateView = (TextView) itemView.findViewById(R.id.order_date);
        orderIntervalsView = (TextView) itemView.findViewById(R.id.order_intervals);
        orderPlaceView = (TextView) itemView.findViewById(R.id.order_place);
        orderPriceView = (TextView) itemView.findViewById(R.id.order_price);
        orderStateView = (TextView) itemView.findViewById(R.id.order_state);
        orderStateMarkerView = itemView.findViewById(R.id.order_state_marker);
    }
}
