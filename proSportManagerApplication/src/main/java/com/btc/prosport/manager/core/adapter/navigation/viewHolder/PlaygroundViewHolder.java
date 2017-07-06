package com.btc.prosport.manager.core.adapter.navigation.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public final class PlaygroundViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView playgroundNameView;

    @NonNull
    public final TextView playgroundOrdersCountView;

    public PlaygroundViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        playgroundNameView = (TextView) itemView.findViewById(R.id.playground_name);
        playgroundOrdersCountView = (TextView) itemView.findViewById(R.id.playground_orders_count);
    }
}
