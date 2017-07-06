package com.btc.prosport.core.adapter.timeTable.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.common.R;

@Accessors(prefix = "_")
public class DayViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public TextView dayOfWeekView;

    @NonNull
    public TextView dayView;

    public DayViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        dayView = (TextView) itemView.findViewById(R.id.day);
        dayOfWeekView = (TextView) itemView.findViewById(R.id.day_of_week);
    }
}
