package com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class SportComplexPropertyViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView propertyNameView;

    @NonNull
    public final TextView propertyValueView;

    public SportComplexPropertyViewHolder(@NonNull final View itemView) {
        super(itemView);

        propertyNameView = (TextView) itemView.findViewById(R.id.sport_complex_property_name);
        propertyValueView = (TextView) itemView.findViewById(R.id.sport_complex_property_value);
    }
}
