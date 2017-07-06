package com.btc.prosport.manager.core.adapter.playgroundPicker.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public final class PlaygroundViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final CheckBox playgroundSelectView;

    public PlaygroundViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        playgroundSelectView = (CheckBox) itemView.findViewById(R.id.playground_select);
    }
}
