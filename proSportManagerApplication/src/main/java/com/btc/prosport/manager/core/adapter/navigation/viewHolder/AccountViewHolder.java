package com.btc.prosport.manager.core.adapter.navigation.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;

public class AccountViewHolder extends ExtendedRecyclerViewHolder {
    public AccountViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));
    }
}
