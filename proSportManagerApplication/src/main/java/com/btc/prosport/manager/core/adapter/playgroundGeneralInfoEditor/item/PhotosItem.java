package com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.item;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoAdapter;
import com.btc.prosport.manager.core.adapter.playgroundGeneralInfoEditor.GeneralInfoItem;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.item.PhotoItem;

import java.util.ArrayList;
import java.util.List;

@Accessors(prefix = "_")
public class PhotosItem implements GeneralInfoItem {

    @Override
    public int getItemType() {
        return GeneralInfoAdapter.VIEW_TYPE_PHOTOS;
    }

    @Getter
    @Setter
    @NonNull
    private List<PhotoItem> _photoItems = new ArrayList<>();
}
