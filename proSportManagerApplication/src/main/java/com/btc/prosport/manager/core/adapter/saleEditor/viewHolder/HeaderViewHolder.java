package com.btc.prosport.manager.core.adapter.saleEditor.viewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public class HeaderViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextInputLayout valueContainerView;

    @NonNull
    public final EditText valueView;

    public HeaderViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        valueContainerView = (TextInputLayout) itemView.findViewById(R.id.value_container);
        valueView = (EditText) itemView.findViewById(R.id.value);
    }
}
