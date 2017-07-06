package com.btc.prosport.player.core.eventArgs;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class CreateOrderEventArgs extends EventArgs {
    public CreateOrderEventArgs(
        final long playgroundId,
        final long dateStart,
        final long dateEnd,
        final long timeStart,
        final long timeEnd) {
        _playgroundId = playgroundId;
        _dateStart = dateStart;
        _dateEnd = dateEnd;
        _timeStart = timeStart;
        _timeEnd = timeEnd;
    }

    @Getter
    private final long _dateEnd;

    @Getter
    private final long _dateStart;

    @Getter
    private final long _playgroundId;

    @Getter
    private final long _timeEnd;

    @Getter
    private final long _timeStart;
}
