package com.btc.prosport.core.result;

import android.net.Uri;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public class PhotoResult {
    public PhotoResult(@Nullable final Uri uri) {
        _uri = uri;
    }

    @Getter
    @Nullable
    private final Uri _uri;
}
