package com.btc.prosport.player.core.adapter.sportComplexDetails.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;

import java.util.List;

@Accessors(prefix = "_")
public final class SportComplexPhotosItem implements SportComplexDetailsItem {
    public SportComplexPhotosItem(@Nullable final List<String> photosUris) {
        _photosUris = photosUris;
    }

    @Override
    public final int getItemType() {
        return SportComplexDetailsAdapter.VIEW_TYPE_SPORT_COMPLEX_PHOTOS;
    }

    @Getter
    @Nullable
    private final List<String> _photosUris;
}
