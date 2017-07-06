package com.btc.prosport.manager.core.adapter.orderDetails;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class OrderGeneralInfoViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView orderDateView;

    @NonNull
    public final View orderEditView;

    @NonNull
    public final TextView orderIdView;

    @NonNull
    public final TextView orderIntervalsView;

    @NonNull
    public final View orderMoreView;

    @NonNull
    public final TextView orderPlaygroundView;

    @NonNull
    public final TextView orderPriceView;

    @NonNull
    public final TextView orderSportComplexView;

    @NonNull
    public final View orderStateMarkerView;

    @NonNull
    public final TextView userNameView;

    public OrderGeneralInfoViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        orderDateView = (TextView) itemView.findViewById(R.id.order_date);
        orderMoreView = itemView.findViewById(R.id.order_more);
        userNameView = (TextView) itemView.findViewById(R.id.order_player_name);
        orderSportComplexView = (TextView) itemView.findViewById(R.id.order_sport_complex);
        orderPlaygroundView = (TextView) itemView.findViewById(R.id.order_playground);
        orderIdView = (TextView) itemView.findViewById(R.id.order_id);
        orderPriceView = (TextView) itemView.findViewById(R.id.order_price);
        orderStateMarkerView = itemView.findViewById(R.id.order_state_marker);
        orderIntervalsView = (TextView) itemView.findViewById(R.id.order_intervals);
        orderEditView = itemView.findViewById(R.id.order_edit);
    }
}
