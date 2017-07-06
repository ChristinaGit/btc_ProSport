package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.SubwayStation;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;

import java.util.HashMap;

@Accessors(prefix = "_")
public class SubwayItem implements GeneralInfoItem {
    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_SUBWAY;
    }

    @Getter
    @Setter
    private Long _selectedSubwayId;

    @Getter
    @Setter
    @Nullable
    private HashMap<Long, SubwayStation> _subways;
}
