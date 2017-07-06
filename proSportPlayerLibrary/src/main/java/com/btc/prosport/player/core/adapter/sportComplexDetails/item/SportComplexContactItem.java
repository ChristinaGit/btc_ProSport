package com.btc.prosport.player.core.adapter.sportComplexDetails.item;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;

@Accessors(prefix = "_")
public final class SportComplexContactItem implements SportComplexDetailsItem {
    public SportComplexContactItem(final int iconId, @Nullable final CharSequence info) {
        _iconId = iconId;
        _info = info;
    }

    @Override
    public final int getItemType() {
        return SportComplexDetailsAdapter.VIEW_TYPE_SPORT_COMPLEX_CONTACT;
    }

    @Getter
    @DrawableRes
    private final int _iconId;

    @Getter
    @Nullable
    private final CharSequence _info;
}
