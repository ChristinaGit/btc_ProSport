package com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class SportComplexAttributeViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView attributeView;

    public SportComplexAttributeViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        attributeView = (TextView) itemView.findViewById(R.id.sport_complex_attribute);
    }
}
