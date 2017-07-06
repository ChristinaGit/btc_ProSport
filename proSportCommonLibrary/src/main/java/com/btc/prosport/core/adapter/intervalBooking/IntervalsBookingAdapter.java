package com.btc.prosport.core.adapter.intervalBooking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.utility.IntervalState;
import com.btc.prosport.common.R;
import com.btc.prosport.core.adapter.cell.SelectableCellsAdapter;
import com.btc.prosport.core.utility.IntervalUtils;

@Accessors(prefix = "_")
public class IntervalsBookingAdapter extends SelectableCellsAdapter<Interval> {
    @Nullable
    public Long getSelectionEndTime() {
        Long reservationTime = null;

        final val reservationEndInterval = getLastSelectedCell();
        if (reservationEndInterval != null) {
            reservationTime = IntervalUtils.getEndTime(reservationEndInterval);
        }

        return reservationTime;
    }

    @Nullable
    public Long getSelectionStartTime() {
        Long reservationTime = null;

        final val reservationStartInterval = getFirstSelectedCell();
        if (reservationStartInterval != null) {
            reservationTime = IntervalUtils.getStartTime(reservationStartInterval);
        }

        return reservationTime;
    }

    @Override
    public boolean isCellSelectable(@Nullable final Interval interval) {
        return IntervalUtils.getState(interval) == IntervalState.FREE;
    }

    @NonNull
    @Override
    protected IntervalViewHolder onCreateCellViewHolder(
        @NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_inervals_booking_cell, parent);

        return new IntervalViewHolder(view);
    }
}