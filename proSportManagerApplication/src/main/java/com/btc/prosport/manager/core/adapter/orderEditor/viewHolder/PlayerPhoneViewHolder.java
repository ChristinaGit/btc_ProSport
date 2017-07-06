package com.btc.prosport.manager.core.adapter.orderEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public final class PlayerPhoneViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextInputLayout playerPhoneContainerView;

    @NonNull
    public final EditText playerPhoneView;

    public PlayerPhoneViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        playerPhoneView = (EditText) itemView.findViewById(R.id.player_phone);
        playerPhoneContainerView =
            (TextInputLayout) itemView.findViewById(R.id.player_phone_container);
    }
}
