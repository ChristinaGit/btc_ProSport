package com.btc.prosport.manager.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.common.extension.eventArgs.IdEventArgs;

import java.util.List;

public interface OrderViewerScreen extends Screen {
    void displayLoading();

    void displayLoadingError();

    void displayOrder(
        @Nullable final Order order, @Nullable List<Interval> intervals, final long startDate);

    @NonNull
    Event<IdEventArgs> getReservationEditEvent();

    @NonNull
    Event<IdEventArgs> getViewOrderEvent();
}
