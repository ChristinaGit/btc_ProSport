package com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.item;

import android.net.Uri;
import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.Item;
import com.btc.prosport.manager.core.adapter.playgroundPhotoEditor.PhotosAdapter;

@Accessors(prefix = "_")
public class PhotoItem implements Item {
    public PhotoItem(@NonNull final Uri uri) {
        Contracts.requireNonNull(uri, "uri == null");

        _uri = uri;
    }

    @Override
    public int getItemType() {
        return PhotosAdapter.VIEW_TYPE_PHOTO;
    }

    @Getter
    @NonNull
    private final Uri _uri;
}
