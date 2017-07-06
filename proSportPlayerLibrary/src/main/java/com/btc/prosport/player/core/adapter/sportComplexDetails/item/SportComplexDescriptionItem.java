package com.btc.prosport.player.core.adapter.sportComplexDetails.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;

@Accessors(prefix = "_")
public final class SportComplexDescriptionItem implements SportComplexDetailsItem {
    public SportComplexDescriptionItem(@Nullable final String description) {
        _description = description;
    }

    @Override
    public final int getItemType() {
        return SportComplexDetailsAdapter.VIEW_TYPE_SPORT_COMPLEX_DESCRIPTION;
    }

    @Getter
    @Nullable
    private final String _description;
}
