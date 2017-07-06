package com.btc.prosport.player.core.adapter.sportComplexDetails.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.adapter.sportComplexDetails.SportComplexDetailsAdapter;

@Accessors(prefix = "_")
public class SportComplexInventoryItem implements SportComplexDetailsItem {
    public SportComplexInventoryItem(
        @Nullable final CharSequence inventoryDescription, final boolean inventoryChargeable) {
        _inventoryDescription = inventoryDescription;
        _inventoryChargeable = inventoryChargeable;
    }

    @Override
    public final int getItemType() {
        return SportComplexDetailsAdapter.VIEW_TYPE_SPORT_COMPLEX_INVENTORY;
    }

    @Getter
    private final boolean _inventoryChargeable;

    @Getter
    @Nullable
    private final CharSequence _inventoryDescription;
}
