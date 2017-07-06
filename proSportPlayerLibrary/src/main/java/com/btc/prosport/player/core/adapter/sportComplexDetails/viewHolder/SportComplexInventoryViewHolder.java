package com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class SportComplexInventoryViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView inventoryChargeableView;

    @NonNull
    public final TextView inventoryDescriptionView;

    public SportComplexInventoryViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        inventoryChargeableView =
            (TextView) itemView.findViewById(R.id.sport_complex_inventory_chargeable);
        inventoryDescriptionView =
            (TextView) itemView.findViewById(R.id.sport_complex_inventory_description);
    }
}
