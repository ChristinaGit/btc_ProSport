package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.manager.core.eventArgs.ScheduleEditorPickTimeEvent;
import com.btc.prosport.manager.core.result.ScheduleEditorPickTimeResult;

public interface PlaygroundScheduleEditorScreen extends Screen {
    void displayTime(@NonNull ScheduleEditorPickTimeResult scheduleEditorPickTimeResult);

    @NonNull
    Event<ScheduleEditorPickTimeEvent> getPickTimeEvent();
}
