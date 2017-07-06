package com.btc.prosport.player.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.player.core.SportComplexesFilter;
import com.btc.prosport.player.core.SportComplexesSortOrder;

@ToString
@Accessors(prefix = "_")
public class SportComplexesSearchEventArgs extends EventArgs {
    public SportComplexesSearchEventArgs(
        @Nullable final Long dateSearchParams,
        @Nullable final Long startTimeSearchParams,
        @Nullable final Long endTimeSearchParams,
        @Nullable final String nameSearchParams,
        @Nullable final SportComplexesSortOrder sportComplexesSortOrder,
        @Nullable final SportComplexesFilter sportComplexesFilter) {
        _nameSearchParams = nameSearchParams;
        _dateSearchParams = dateSearchParams;
        _startTimeSearchParams = startTimeSearchParams;
        _endTimeSearchParams = endTimeSearchParams;
        _sportComplexesSortOrder = sportComplexesSortOrder;
        _sportComplexesFilter = sportComplexesFilter;
    }

    @Getter
    @Nullable
    private final Long _dateSearchParams;

    @Getter
    @Nullable
    private final Long _endTimeSearchParams;

    @Getter
    @Nullable
    private final String _nameSearchParams;

    @Getter
    @Nullable
    private final SportComplexesFilter _sportComplexesFilter;

    @Getter
    @Nullable
    private final SportComplexesSortOrder _sportComplexesSortOrder;

    @Getter
    @Nullable
    private final Long _startTimeSearchParams;
}
