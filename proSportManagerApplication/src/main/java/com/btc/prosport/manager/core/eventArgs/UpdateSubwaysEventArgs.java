package com.btc.prosport.manager.core.eventArgs;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class UpdateSubwaysEventArgs extends EventArgs {
    public UpdateSubwaysEventArgs(final long id) {
        _id = id;
    }

    @Getter
    private final long _id;
}
