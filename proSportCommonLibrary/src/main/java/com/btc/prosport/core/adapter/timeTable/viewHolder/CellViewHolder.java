package com.btc.prosport.core.adapter.timeTable.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.common.R;

@Accessors(prefix = "_")
public class CellViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView valueView;

    public CellViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        valueView = (TextView) itemView.findViewById(R.id.value);
    }
}
