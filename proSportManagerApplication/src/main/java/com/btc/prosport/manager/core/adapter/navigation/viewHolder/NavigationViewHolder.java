package com.btc.prosport.manager.core.adapter.navigation.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class NavigationViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView labelView;

    @NonNull
    public final View navigationDividerView;

    public NavigationViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        labelView = (TextView) itemView.findViewById(R.id.navigation_label);
        navigationDividerView = itemView.findViewById(R.id.navigation_divider);
    }
}
