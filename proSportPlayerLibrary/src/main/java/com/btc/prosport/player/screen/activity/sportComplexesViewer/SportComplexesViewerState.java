package com.btc.prosport.player.screen.activity.sportComplexesViewer;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.btc.prosport.player.core.SportComplexesSortOrder;

import org.parceler.Parcel;

@Parcel
@Accessors(prefix = "_")
/*package-private*/ final class SportComplexesViewerState {

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Integer _activeTab;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _dateSearchParam;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _endTimeSearchParam;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ String _nameSearchParam;

    @Getter
    @Setter
    /*package-private*/ boolean _searchExpanded = false;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ SportComplexesSortOrder _sportComplexesSortOrder;

    @Getter
    @Setter
    @Nullable
    /*package-private*/ Long _startTimeSearchParam;
}
