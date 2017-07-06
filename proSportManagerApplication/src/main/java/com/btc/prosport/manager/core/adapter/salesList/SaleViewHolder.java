package com.btc.prosport.manager.core.adapter.salesList;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class SaleViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView saleIntervalsView;

    @NonNull
    public final View saleMoreView;

    @NonNull
    public final TextView salePlaceView;

    public SaleViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        saleMoreView = itemView.findViewById(R.id.sale_more);
        saleIntervalsView = (TextView) itemView.findViewById(R.id.sale_intervals);
        salePlaceView = (TextView) itemView.findViewById(R.id.sale_place);
    }
}
