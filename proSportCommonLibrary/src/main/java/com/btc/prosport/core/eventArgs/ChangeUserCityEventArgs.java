package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ChangeUserCityEventArgs extends EventArgs {

    public ChangeUserCityEventArgs(
        @NonNull final String newCityId, @NonNull final String oldCityId) {

        _newCityId = newCityId;
        _oldCityId = oldCityId;
    }

    @NonNull
    @Getter
    private final String _newCityId;

    @NonNull
    @Getter
    private final String _oldCityId;
}
