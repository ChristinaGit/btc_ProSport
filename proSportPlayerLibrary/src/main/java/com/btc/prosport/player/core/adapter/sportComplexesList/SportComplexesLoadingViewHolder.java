package com.btc.prosport.player.core.adapter.sportComplexesList;

import android.support.annotation.NonNull;
import android.view.View;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;

public final class SportComplexesLoadingViewHolder extends ExtendedRecyclerViewHolder {
    public SportComplexesLoadingViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
    }
}
