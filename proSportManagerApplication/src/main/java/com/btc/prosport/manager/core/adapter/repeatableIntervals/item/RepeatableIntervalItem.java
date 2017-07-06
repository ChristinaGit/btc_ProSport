package com.btc.prosport.manager.core.adapter.repeatableIntervals.item;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.utility.TimeUtils;
import com.btc.prosport.api.model.utility.WeekDay;
import com.btc.prosport.core.utility.IntervalUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Accessors(prefix = "_")
public final class RepeatableIntervalItem {
    public static RepeatableIntervalItem createEmpty() {
        return new RepeatableIntervalItem();
    }

    @NonNull
    public static RepeatableIntervalItem createDefault() {
        return createDefault(System.currentTimeMillis());
    }

    @NonNull
    public static RepeatableIntervalItem createCopy(@NonNull final RepeatableIntervalItem item) {
        return new RepeatableIntervalItem(item);
    }

    @NonNull
    public static RepeatableIntervalItem createDefault(final long currentTime) {
        final val calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        return createDefault(calendar);
    }

    @NonNull
    public static RepeatableIntervalItem createDefault(@NonNull final Calendar calendar) {
        Contracts.requireNonNull(calendar, "calendar == null");

        final val item = new RepeatableIntervalItem();

        final val date = calendar.getTimeInMillis();
        final val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final val minute = calendar.get(Calendar.MINUTE);

        final val day = TimeUtils.getAlignedDay(date);
        final val time = TimeUtils.getTime(hourOfDay, minute);

        item.setDateStart(day);
        item.setDateEnd(day);

        item.setTimeStart(time);
        item.setTimeEnd(time);

        return item;
    }

    @NonNull
    public final List<WeekDay> getRepeatWeekDays() {
        if (_repeatWeekDays == null) {
            _repeatWeekDays = new ArrayList<>();
        }

        return _repeatWeekDays;
    }

    public void setDateEnd(final long dateEnd) {
        _dateEnd = TimeUtils.getAlignedDay(dateEnd);
    }

    public void setDateStart(final long dateStart) {
        _dateStart = TimeUtils.getAlignedDay(dateStart);
    }

    public void setTimeEnd(final long timeEnd) {
        _timeEnd = IntervalUtils.getAlignedIntervalTime(timeEnd);
    }

    public void setTimeStart(final long timeStart) {
        _timeStart = IntervalUtils.getAlignedIntervalTime(timeStart);
    }

    @Getter
    private long _dateEnd;

    @Getter
    private long _dateStart;

    @Getter
    @Setter
    private Long _id;

    @Nullable
    private List<WeekDay> _repeatWeekDays;

    @Getter
    @Setter
    private boolean _repeatable;

    @Getter
    private long _timeEnd;

    @Getter
    private long _timeStart;

    private RepeatableIntervalItem(@NonNull final RepeatableIntervalItem that) {
        Contracts.requireNonNull(that, "that == null");

        _repeatable = that._repeatable;
        _repeatWeekDays =
            that._repeatWeekDays == null ? null : new ArrayList<>(that._repeatWeekDays);
        _timeStart = that._timeStart;
        _timeEnd = that._timeEnd;
        _dateStart = that._dateStart;
        _dateEnd = that._dateEnd;
    }

    private RepeatableIntervalItem() {
    }
}
