package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.core.eventArgs.ViewIntervalsEventArgs;
import com.btc.prosport.manager.core.eventArgs.CreateSimpleOrderEventArgs;

import java.util.List;

public interface ScheduleScreen extends Screen {
    void displayIntervals(@Nullable PlaygroundTitle playground, @Nullable List<Interval> intervals);

    void displayIntervalsLoading();

    void displayIntervalsLoadingError();

    @NonNull
    Event<CreateSimpleOrderEventArgs> getCreateOrderEvent();

    @NonNull
    Event<ViewIntervalsEventArgs> getRefreshIntervalsEvent();

    @NonNull
    Event<ViewIntervalsEventArgs> getViewIntervalsEvent();

    @NonNull
    Event<IdEventArgs> getViewOrderEvent();
}
