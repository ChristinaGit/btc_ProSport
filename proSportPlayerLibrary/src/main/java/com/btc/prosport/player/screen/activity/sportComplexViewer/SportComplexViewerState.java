package com.btc.prosport.player.screen.activity.sportComplexViewer;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.parceler.Parcel;

@Parcel
@Accessors(prefix = "_")
/*package-private*/ final class SportComplexViewerState {

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Integer _activeTab;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _sportComplexId;
}
