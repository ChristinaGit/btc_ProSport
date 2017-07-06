package com.btc.prosport.player.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class SelectSearchTimeEventArgs extends EventArgs {
    public SelectSearchTimeEventArgs(@NonNull final Mode mode) {
        Contracts.requireNonNull(mode, "mode == null");

        _mode = mode;
    }

    @Getter
    @NonNull
    private final Mode _mode;

    public enum Mode {
        START, END
    }
}
