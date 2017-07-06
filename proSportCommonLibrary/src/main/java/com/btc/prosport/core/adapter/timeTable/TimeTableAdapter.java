package com.btc.prosport.core.adapter.timeTable;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.extension.view.recyclerView.viewHolder.ExtendedRecyclerViewHolder;
import com.btc.common.utility.TimeUtils;
import com.btc.prosport.common.R;
import com.btc.prosport.core.adapter.singleTable.SingleTableAdapter;
import com.btc.prosport.core.adapter.timeTable.viewHolder.CellViewHolder;
import com.btc.prosport.core.adapter.timeTable.viewHolder.CornerViewHolder;
import com.btc.prosport.core.adapter.timeTable.viewHolder.DayViewHolder;
import com.btc.prosport.core.adapter.timeTable.viewHolder.TimeViewHolder;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;

@Accessors(prefix = "_")
public abstract class TimeTableAdapter extends SingleTableAdapter {
    private static final String _LOG_TAG = ConstantBuilder.logTag(TimeTableAdapter.class);

    public final boolean isCellHeaderVisible(@NonNull final GridLayoutManager layoutManager) {
        Contracts.requireNonNull(layoutManager, "layoutManager == null");

        final int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();

        final int firstPosition = getInnerItemRelativePosition(0);
        final int lastPosition = firstPosition + getRowLength();

        return firstVisiblePosition < lastPosition;
    }

    public final void setStartTime(@Nullable final Long startTime) {
        if (!Objects.equals(_startTime, startTime)) {
            _startTime = startTime;

            onStartTimeChanged();
        }
    }

    public void bindOverlayHeaderView(
        @NonNull final RecyclerView recyclerView, @NonNull final LinearLayout headerView) {
        Contracts.requireNonNull(headerView, "headerView == null");

        _overlayHeaderViews.put(recyclerView, headerView);
    }

    @Nullable
    public Long getDayByRelativePosition(final int relativePosition) {
        final Long day;

        final val startDay = getStartDay();
        if (startDay != null) {
            day = startDay +
                  getColumnByVerticalRelativePosition(relativePosition) * DateUtils.DAY_IN_MILLIS;
        } else {
            day = null;
        }

        return day;
    }

    @NonNull
    public CharSequence getFormattedDay(final long dateTime) {
        _internalCalendar.clear();
        _internalCalendar.setTimeInMillis(dateTime);

        return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "d"),
                                 _internalCalendar);
    }

    @NonNull
    public CharSequence getFormattedDayOfWeek(final long dateTime) {
        _internalCalendar.clear();
        _internalCalendar.setTimeInMillis(dateTime);

        return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "E"),
                                 _internalCalendar);
    }

    @NonNull
    public CharSequence getFormattedTime(@NonNull final Context context, final long time) {
        Contracts.requireNonNull(context, "context == null");

        if (_intervalTimeFormat == null) {
            _intervalTimeFormat = DateFormat.getTimeFormat(context);
            _intervalTimeFormat.getTimeZone().setRawOffset(0);
        }
        return _intervalTimeFormat.format(new Date(time));
    }

    @Nullable
    public Long getStartDay() {
        final Long startDay;

        final val startTime = getStartTime();
        if (startTime != null) {
            startDay = TimeUtils.getAlignedDay(startTime);
        } else {
            startDay = null;
        }

        return startDay;
    }

    public void invalidateOverlayHeader(@NonNull final RecyclerView recyclerView) {
        Contracts.requireNonNull(recyclerView, "recyclerView == null");

        final val overlayHeaderView = getOverlayHeaderView(recyclerView);
        if (overlayHeaderView != null) {

            final int childCount = overlayHeaderView.getChildCount();
            for (int i = 1; i < childCount; i++) {
                final val dayItem = overlayHeaderView.getChildAt(i);
                final val intervalDayView = (TextView) dayItem.findViewById(R.id.day);
                final val intervalDayOfWeekView = (TextView) dayItem.findViewById(R.id.day_of_week);

                if (intervalDayView != null && intervalDayOfWeekView != null) {
                    onBindOverlayHeaderItem(i - 1, dayItem, intervalDayView, intervalDayOfWeekView);
                }
            }
        }
    }

    public void invalidateOverlayHeaderVisibility(@NonNull final RecyclerView recyclerView) {
        Contracts.requireNonNull(recyclerView, "recyclerView == null");

        final val overlayHeaderView = getOverlayHeaderView(recyclerView);

        if (overlayHeaderView != null) {
            boolean visible = false;

            if (recyclerView.getVisibility() == View.VISIBLE) {
                final val layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    visible = !isCellHeaderVisible(layoutManager);
                }
            }

            overlayHeaderView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void notifyColumnChanged(final int column) {
        super.notifyColumnChanged(column);

        invalidateOverlayHeaders();
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(_scrollListener);
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        recyclerView.removeOnScrollListener(_scrollListener);
    }

    @Override
    protected void onBindColumnHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        Contracts.requireNonNull(genericHolder, "genericHolder == null");

        final val column = getColumnHeaderCellRelativePosition(position);

        final val holder = (DayViewHolder) genericHolder;

        final CharSequence formattedDay;
        final CharSequence formattedDayOfWeek;

        final val startTime = getStartTime();
        if (startTime != null) {
            final long cellTime = startTime + column * DateUtils.DAY_IN_MILLIS;

            formattedDay = getFormattedDay(cellTime);

            formattedDayOfWeek = getFormattedDayOfWeek(cellTime);
        } else {
            formattedDayOfWeek = null;
            formattedDay = null;
        }

        holder.dayView.setText(formattedDay);
        holder.dayOfWeekView.setText(formattedDayOfWeek);
    }

    @Override
    protected void onBindCornerCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder holder, final int position) {
        Contracts.requireNonNull(holder, "holder == null");
    }

    @Override
    protected void onBindRowHeaderCellViewHolder(
        @NonNull final ExtendedRecyclerViewHolder genericHolder, final int position) {
        Contracts.requireNonNull(genericHolder, "genericHolder == null");

        final val holder = (TimeViewHolder) genericHolder;

        final val context = holder.getContext();

        final int relativePosition = getRowHeaderCellRelativePosition(position);

        final long intervalTime = relativePosition * getIntervalLength();

        final val formattedIntervalTime = getFormattedTime(context, intervalTime);

        holder.timeView.setText(formattedIntervalTime);
    }

    @NonNull
    @Override
    protected CellViewHolder onCreateCellViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_time_table_cell, parent);

        return new CellViewHolder(view);
    }

    @NonNull
    @Override
    protected DayViewHolder onCreateColumnHeaderCellViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_time_table_day, parent);

        return new DayViewHolder(view);
    }

    @NonNull
    @Override
    protected CornerViewHolder onCreateCornerCellViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_time_table_corner, parent);

        return new CornerViewHolder(view);
    }

    @NonNull
    @Override
    protected TimeViewHolder onCreateRowHeaderCellViewHolder(@NonNull final ViewGroup parent) {
        Contracts.requireNonNull(parent, "parent == null");

        final val view = inflateView(R.layout.layout_time_table_time, parent);

        return new TimeViewHolder(view);
    }

    public void unbindOverlayHeaderView(@NonNull final RecyclerView recyclerView) {
        Contracts.requireNonNull(recyclerView, "recyclerView == null");

        _overlayHeaderViews.remove(recyclerView);
    }

    @NonNull
    protected final Iterable<RecyclerView> getBoundOverlayRecyclerView() {
        return _overlayHeaderViews.keySet();
    }

    @Nullable
    protected final LinearLayout getOverlayHeaderView(@NonNull final RecyclerView recyclerView) {
        Contracts.requireNonNull(recyclerView, "recyclerView == null");

        return _overlayHeaderViews.get(recyclerView);
    }

    protected final void invalidateOverlayHeaders() {
        IterableUtils.forEach(getBoundOverlayRecyclerView(), new Closure<RecyclerView>() {
            @Override
            public void execute(final RecyclerView input) {
                invalidateOverlayHeader(input);
            }
        });
    }

    protected void onBindOverlayHeaderItem(
        final int column,
        @NonNull final View itemView,
        @NonNull final TextView intervalDayView,
        @NonNull final TextView intervalDayOfWeekView) {
        Contracts.requireNonNull(itemView, "itemView == null");
        Contracts.requireNonNull(intervalDayView, "intervalDayView == null");
        Contracts.requireNonNull(intervalDayOfWeekView, "intervalDayOfWeekView == null");

        final val startDay = getStartDay();

        final CharSequence formattedDay;
        final CharSequence formattedDayOfWeek;

        if (startDay != null) {
            final val cellTime = startDay + column * DateUtils.DAY_IN_MILLIS;
            formattedDay = getFormattedDay(cellTime);
            formattedDayOfWeek = getFormattedDayOfWeek(cellTime);
        } else {
            formattedDayOfWeek = null;
            formattedDay = null;
        }

        intervalDayView.setText(formattedDay);
        intervalDayOfWeekView.setText(formattedDayOfWeek);
    }

    @CallSuper
    protected void onStartTimeChanged() {
        invalidateOverlayHeaders();
    }

    @NonNull
    private final Calendar _internalCalendar = Calendar.getInstance(new SimpleTimeZone(0, "UTC"));

    @NonNull
    private final Map<RecyclerView, LinearLayout> _overlayHeaderViews = new HashMap<>();

    @NonNull
    private final RecyclerView.OnScrollListener _scrollListener =
        new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                if (dy != 0) {
                    invalidateOverlayHeaderVisibility(recyclerView);
                }
            }
        };

    @Getter
    @Setter
    private long _intervalLength;

    @Nullable
    private java.text.DateFormat _intervalTimeFormat;

    @Getter
    @Nullable
    private Long _startTime;
}
