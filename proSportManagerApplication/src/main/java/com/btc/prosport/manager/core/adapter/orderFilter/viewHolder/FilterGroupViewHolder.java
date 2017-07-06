package com.btc.prosport.manager.core.adapter.orderFilter.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class FilterGroupViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView groupTitle;

    public FilterGroupViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        groupTitle = (TextView) itemView.findViewById(R.id.group_title);
    }
}
