package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.TimeItem;

@Accessors(prefix = "_")
public class ScheduleEditorPickTimeEvent extends EventArgs {
    public ScheduleEditorPickTimeEvent(@NonNull final TimeItem.Type type, final int dayIndex) {
        Contracts.requireNonNull(type, "type == null");

        _type = type;
        _dayIndex = dayIndex;
    }

    @Getter
    private final int _dayIndex;

    @Getter
    @NonNull
    private final TimeItem.Type _type;
}
