package com.btc.prosport.manager.core.adapter.orderEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public final class PlayerNameViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextInputLayout playerNameContainerView;

    @NonNull
    public final EditText playerNameView;

    public PlayerNameViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        playerNameView = (EditText) itemView.findViewById(R.id.player_name);
        playerNameContainerView =
            (TextInputLayout) itemView.findViewById(R.id.player_name_container);
    }
}
