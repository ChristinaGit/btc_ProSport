package com.btc.prosport.player.core;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public enum SportComplexesFilter {
    ALL(false), FAVORITES(true);

    @NonNull
    public static SportComplexesFilter[] values(final boolean withUserSpecific) {
        if (withUserSpecific) {
            return values();
        } else {
            return new SportComplexesFilter[]{ALL};
        }
    }

    @Getter
    private final boolean _userSpecific;

    SportComplexesFilter(final boolean userSpecific) {
        _userSpecific = userSpecific;
    }
}
