package com.btc.prosport.player.screen.activity.photosViewer;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.parceler.Parcel;

@Parcel
@Accessors(prefix = "_")
public final class PhotosViewerState {

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _playgroundId;

    @Getter
    @Setter
    /*package-private*/ int _position = 0;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _sportComplexId;
}
