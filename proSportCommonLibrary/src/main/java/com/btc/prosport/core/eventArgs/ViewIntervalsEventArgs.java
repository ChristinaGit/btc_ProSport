package com.btc.prosport.core.eventArgs;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.extension.eventArgs.IdEventArgs;

@Accessors(prefix = "_")
public class ViewIntervalsEventArgs extends IdEventArgs {
    public ViewIntervalsEventArgs(final long id, final long startTime) {
        super(id);

        _startTime = startTime;
    }

    @Getter
    private final long _startTime;
}
