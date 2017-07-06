package com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.item;

import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.Item;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.PhotosAdapter;

public class AddPhotoItem implements Item {
    @Override
    public int getItemType() {
        return PhotosAdapter.VIEW_TYPE_ADD_PHOTO;
    }
}
