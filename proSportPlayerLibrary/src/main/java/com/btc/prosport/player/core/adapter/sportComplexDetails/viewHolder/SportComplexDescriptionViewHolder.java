package com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class SportComplexDescriptionViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView descriptionView;

    public SportComplexDescriptionViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        descriptionView = (TextView) itemView.findViewById(R.id.sport_complex_description);
    }
}
