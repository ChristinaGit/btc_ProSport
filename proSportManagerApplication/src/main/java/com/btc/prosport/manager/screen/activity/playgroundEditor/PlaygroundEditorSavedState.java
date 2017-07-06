package com.btc.prosport.manager.screen.activity.playgroundEditor;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.parceler.Parcel;

@Parcel
@Accessors(prefix = "_")
/*package-private*/ class PlaygroundEditorSavedState {
    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _playgroundId;
}
