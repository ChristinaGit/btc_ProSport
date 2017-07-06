package com.btc.prosport.core.adapter.timeTable.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;

@Accessors(prefix = "_")
public class CornerViewHolder extends ExtendedRecyclerViewHolder {
    public CornerViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
    }
}
