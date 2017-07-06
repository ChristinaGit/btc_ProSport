package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;
import com.google.android.gms.maps.model.LatLngBounds;

@Accessors(prefix = "_")
public class PlaceItem implements GeneralInfoItem {
    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_PLACE;
    }

    @Getter
    @Setter
    @Nullable
    private String _address;

    @Getter
    @Setter
    @Nullable
    private Double _latitude;

    @Getter
    @Setter
    @Nullable
    private Double _longitude;

    @Getter
    @Setter
    @Nullable
    private String _placeId;

    @Getter
    @Setter
    @Nullable
    private LatLngBounds _viewPort;
}
