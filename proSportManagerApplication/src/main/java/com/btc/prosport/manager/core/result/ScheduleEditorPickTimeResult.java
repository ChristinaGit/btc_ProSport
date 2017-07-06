package com.btc.prosport.manager.core.result;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.manager.core.adapter.playgroundScheduleEditor.item.TimeItem;

@Accessors(prefix = "_")
public class ScheduleEditorPickTimeResult extends EventArgs {
    public ScheduleEditorPickTimeResult(
        @NonNull final TimeItem.Type type,
        final int dayIndex,
        final int hourOfDay,
        final int minute) {
        Contracts.requireNonNull(type, "type == null");

        _type = type;
        _dayIndex = dayIndex;
        _hourOfDay = hourOfDay;
        _minute = minute;
    }

    @Getter
    private final int _dayIndex;

    @Getter
    private final int _hourOfDay;

    @Getter
    private final int _minute;

    @Getter
    @NonNull
    private final TimeItem.Type _type;
}
