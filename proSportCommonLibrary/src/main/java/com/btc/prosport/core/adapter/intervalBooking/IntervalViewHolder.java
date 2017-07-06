package com.btc.prosport.core.adapter.intervalBooking;

import android.support.annotation.NonNull;
import android.view.View;

import com.btc.common.contract.Contracts;
import com.btc.prosport.common.R;
import com.btc.prosport.core.adapter.timeTable.viewHolder.CellViewHolder;

public class IntervalViewHolder extends CellViewHolder {
    @NonNull
    public final View priceMarkerView;

    public IntervalViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        priceMarkerView = itemView.findViewById(R.id.sale_marker);
    }
}
