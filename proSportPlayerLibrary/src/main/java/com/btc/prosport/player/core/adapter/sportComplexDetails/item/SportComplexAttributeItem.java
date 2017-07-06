package com.btc.prosport.player.core.adapter.sportComplexDetails.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;

@Accessors(prefix = "_")
public final class SportComplexAttributeItem implements SportComplexDetailsItem {
    public SportComplexAttributeItem(
        @Nullable final CharSequence attribute, @Nullable final String icon) {
        _attribute = attribute;
        _icon = icon;
    }

    @Override
    public final int getItemType() {
        return SportComplexDetailsAdapter.VIEW_TYPE_SPORT_COMPLEX_ATTRIBUTE;
    }

    @Getter
    @Nullable
    private final CharSequence _attribute;

    @Getter
    @Nullable
    private final String _icon;
}
