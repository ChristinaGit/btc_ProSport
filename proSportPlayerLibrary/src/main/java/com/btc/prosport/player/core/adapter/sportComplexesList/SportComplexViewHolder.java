package com.btc.prosport.player.core.adapter.sportComplexesList;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class SportComplexViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final TextView sportComplexDistanceView;

    @NonNull
    public final TextView sportComplexMinimumPriceView;

    @NonNull
    public final TextView sportComplexNameView;

    @NonNull
    public final ImageView sportComplexPhotoView;

    @NonNull
    public final TextView sportComplexSubwayStationsView;

    protected SportComplexViewHolder(@NonNull final View itemView) {
        super(Contracts.requireNonNull(itemView, "itemView == null"));

        sportComplexPhotoView = (ImageView) itemView.findViewById(R.id.sport_complex_photo);
        sportComplexNameView = (TextView) itemView.findViewById(R.id.sport_complex_name);
        sportComplexMinimumPriceView =
            (TextView) itemView.findViewById(R.id.sport_complex_minimum_price);
        sportComplexDistanceView = (TextView) itemView.findViewById(R.id.sport_complex_distance);
        sportComplexSubwayStationsView =
            (TextView) itemView.findViewById(R.id.sport_complex_subway_stations);
    }
}
