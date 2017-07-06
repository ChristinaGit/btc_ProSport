package com.btc.prosport.manager.core.eventArgs;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.extension.eventArgs.IdEventArgs;

@Accessors(prefix = "_")
public class ChangePlaygroundCoveringEventArgs extends IdEventArgs {
    public ChangePlaygroundCoveringEventArgs(
        final long playgroundId, final long coveringId) {
        super(playgroundId);

        _coveringId = coveringId;
    }

    @Getter
    private final long _coveringId;


}