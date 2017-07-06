package com.btc.prosport.manager.core.adapter.repeatableIntervals.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public final class AddIntervalViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final View addIntervalView;

    public AddIntervalViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        addIntervalView = itemView.findViewById(R.id.add_interval);
    }
}
