package com.btc.prosport.player.core.adapter.sportComplexDetails.viewHolder;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.btc.common.extension.view.PageIndicatorView;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.prosport.player.R;

public final class SportComplexPhotosViewHolder extends ExtendedRecyclerViewHolder {
    @NonNull
    public final PageIndicatorView photosIndicatorView;

    @NonNull
    public final ViewPager photosView;

    public SportComplexPhotosViewHolder(@NonNull final View itemView) {
        super(itemView);

        photosView = (ViewPager) itemView.findViewById(R.id.sport_complex_photos);
        photosIndicatorView =
            (PageIndicatorView) itemView.findViewById(R.id.sport_complex_photos_indicator);
    }
}
