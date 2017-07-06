package com.btc.prosport.player.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.btc.common.event.generic.Event;
import com.btc.common.extension.eventArgs.UriEventArgs;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.core.eventArgs.ViewIntervalsEventArgs;
import com.btc.prosport.player.core.eventArgs.CreateOrderEventArgs;

import java.util.List;

public interface IntervalsScreen extends Screen {
    void displayCreateOrderProgress();

    void displayCreatedOrder(@Nullable Order order);

    void displayIntervals(
        @Nullable SportComplexTitle sportComplex,
        @Nullable PlaygroundTitle playground,
        @Nullable List<Interval> intervals);

    void displayIntervalsLoading();

    void displayIntervalsLoadingError();

    @NonNull
    Event<CreateOrderEventArgs> getCreateOrderEvent();

    @NonNull
    Event<ViewIntervalsEventArgs> getRefreshIntervalsEvent();

    @NonNull
    Event<UriEventArgs> getSportComplexPhoneCallEvent();

    @NonNull
    Event<ViewIntervalsEventArgs> getViewIntervalsEvent();
}
