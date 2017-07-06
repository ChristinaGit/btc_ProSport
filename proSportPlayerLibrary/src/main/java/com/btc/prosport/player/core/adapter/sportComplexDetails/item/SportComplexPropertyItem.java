package com.btc.prosport.player.core.adapter.sportComplexDetails.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;

@Accessors(prefix = "_")
public final class SportComplexPropertyItem implements SportComplexDetailsItem {
    public SportComplexPropertyItem(
        @Nullable final CharSequence propertyName, @Nullable final CharSequence propertyValue) {
        _propertyName = propertyName;
        _propertyValue = propertyValue;
    }

    @Override
    public final int getItemType() {
        return SportComplexDetailsAdapter.VIEW_TYPE_SPORT_COMPLEX_PROPERTY;
    }

    @Getter
    @Nullable
    private final CharSequence _propertyName;

    @Getter
    @Nullable
    private final CharSequence _propertyValue;
}
