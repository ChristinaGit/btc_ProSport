package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.City;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;

import java.util.HashMap;

@Accessors(prefix = "_")
public class CityItem implements GeneralInfoItem {
    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_CITY;
    }

    @Getter
    @Setter
    @Nullable
    private HashMap<Long, City> _cities;

    @Getter
    @Setter
    @Nullable
    private Long _selectedCityId;

    @Getter
    @Setter
    @NonNull
    private State _state = State.LOADING;

    public enum State {
        LOADED, ERROR, LOADING
    }
}
