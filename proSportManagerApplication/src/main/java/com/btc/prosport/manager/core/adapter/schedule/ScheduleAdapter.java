package com.btc.prosport.manager.core.adapter.schedule;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.common.extension.eventArgs.IdEventArgs;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.utility.IntervalState;
import com.btc.prosport.core.adapter.intervalBooking.IntervalsBookingAdapter;
import com.btc.prosport.core.utility.IntervalUtils;

@Accessors(prefix = "_")
public final class ScheduleAdapter extends IntervalsBookingAdapter {
    @NonNull
    public final Event<IdEventArgs> getViewOrderEvent() {
        return _viewOrderEvent;
    }

    @Override
    public boolean isCellClickable(@Nullable final Interval interval) {
        return super.isCellClickable(interval) ||
               IntervalUtils.getState(interval) == IntervalState.BUSY;
    }

    @CallSuper
    @Override
    protected void onCellClick(final int position) {
        super.onCellClick(position);

        final val relativePosition = getCellVerticalRelativePosition(position);
        final val interval = getCellItemByRelativePosition(relativePosition);

        final val intervalStateCode = interval == null ? null : interval.getStateCode();
        final val intervalState =
            intervalStateCode == null ? null : IntervalState.byCode(intervalStateCode);

        if (intervalState == IntervalState.BUSY) {
            if (hasSelection()) {
                clearSelection();

                riseCellsSelectionChanged();
                riseCellsSelectionStateChanged();
            }

            final val orderId = interval.getOrderId();
            if (orderId != null) {
                _viewOrderEvent.rise(new IdEventArgs(orderId));
            }
        }
    }

    @NonNull
    private final ManagedEvent<IdEventArgs> _viewOrderEvent = Events.createEvent();
}
