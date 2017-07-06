package com.btc.prosport.manager.core.adapter.playgroundPicker.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.manager.R;

public final class SportComplexViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView sportComplexNameView;

    @NonNull
    public final CheckBox sportComplexSelectView;

    public SportComplexViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        sportComplexNameView = (TextView) itemView.findViewById(R.id.sport_complex_name);
        sportComplexSelectView = (CheckBox) itemView.findViewById(R.id.sport_complex_select);
    }
}
