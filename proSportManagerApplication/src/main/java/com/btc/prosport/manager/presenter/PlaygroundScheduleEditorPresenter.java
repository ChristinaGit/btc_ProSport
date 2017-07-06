package com.btc.prosport.manager.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.functions.Action1;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.control.manager.picker.TimePickerManager;
import com.btc.common.control.manager.picker.TimePickerResult;
import com.btc.common.event.generic.EventHandler;
import com.btc.common.mvp.presenter.BasePresenter;
import com.btc.prosport.manager.core.eventArgs.ScheduleEditorPickTimeEvent;
import com.btc.prosport.manager.core.result.ScheduleEditorPickTimeResult;
import com.btc.prosport.manager.screen.PlaygroundScheduleEditorScreen;

@Accessors(prefix = "_")
public final class PlaygroundScheduleEditorPresenter
    extends BasePresenter<PlaygroundScheduleEditorScreen> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(PlaygroundScheduleEditorPresenter.class);

    public PlaygroundScheduleEditorPresenter(@NonNull final TimePickerManager timePickerManager) {
        Contracts.requireNonNull(timePickerManager, "dataPickerManager == null");

        _timePickerManager = timePickerManager;
    }

    @Override
    protected void onScreenAppear(
        @NonNull final PlaygroundScheduleEditorScreen screen) {
        super.onScreenAppear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getPickTimeEvent().addHandler(_pickTimeEventHandler);
    }

    @Override
    protected void onScreenDisappear(
        @NonNull final PlaygroundScheduleEditorScreen screen) {
        super.onScreenDisappear(Contracts.requireNonNull(screen, "screen == null"));

        screen.getPickTimeEvent().removeHandler(_pickTimeEventHandler);
    }

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private final TimePickerManager _timePickerManager;

    @NonNull
    private final EventHandler<ScheduleEditorPickTimeEvent> _pickTimeEventHandler =
        new EventHandler<ScheduleEditorPickTimeEvent>() {
            @Override
            public void onEvent(@NonNull final ScheduleEditorPickTimeEvent eventArgs) {
                getTimePickerManager().pickTime().subscribe(new Action1<TimePickerResult>() {
                    @Override
                    public void call(final TimePickerResult timePickerResult) {
                        if (timePickerResult.isSelected()) {
                            displayTime(new ScheduleEditorPickTimeResult(
                                eventArgs.getType(),
                                eventArgs.getDayIndex(),
                                timePickerResult.getHourOfDay(),
                                timePickerResult.getMinute()));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        Log.w(_LOG_TAG, throwable);
                    }
                });
            }
        };

    private void displayTime(
        @NonNull final ScheduleEditorPickTimeResult pickTimeResult) {
        Contracts.requireNonNull(pickTimeResult, "pickTimeResult == null");

        val screen = getScreen();
        if (screen != null) {
            screen.displayTime(pickTimeResult);
        }
    }
}
