package com.btc.prosport.api.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Accessors(prefix = "_")
public final class CreateSaleParams {
    public CreateSaleParams(
        @NonNull final List<Long> playgroundsIds,
        @NonNull final List<Interval> intervals,
        final int type,
        final int value,
        @Nullable final String description) {
        Contracts.requireNonNull(playgroundsIds, "playgroundsIds == null");
        Contracts.requireNonNull(intervals, "intervals == null");

        _playgroundsIds = playgroundsIds;
        _intervals = intervals;
        _type = type;
        _value = value;
        _description = StringUtils.defaultString(description);
    }

    @Getter
    @SerializedName("description")
    @NonNull
    private final String _description;

    @Getter
    @SerializedName("intervals")
    @NonNull
    private final List<Interval> _intervals;

    @Getter
    @SerializedName("playground")
    @NonNull
    private final List<Long> _playgroundsIds;

    @Getter
    @SerializedName("value_type")
    private final int _type;

    @Getter
    @SerializedName("value")
    private final int _value;

    public static final class Interval {
        public Interval(
            @NonNull final String dateStart,
            @NonNull final String timeStart,
            @NonNull final String dateEnd,
            @NonNull final String timeEnd) {
            this(dateStart, timeStart, dateEnd, timeEnd, StringUtils.EMPTY);
        }

        public Interval(
            @NonNull final String dateStart,
            @NonNull final String timeStart,
            @NonNull final String endDate,
            @NonNull final String timeEnd,
            @NonNull final String repeatWeekDays) {
            _dateStart = dateStart;
            _timeStart = timeStart;
            _dateEnd = endDate;
            _timeEnd = timeEnd;
            _repeatWeekDays = repeatWeekDays;
        }

        @Getter
        @SerializedName("date_end")
        @NonNull
        private final String _dateEnd;

        @Getter
        @SerializedName("date_start")
        @NonNull
        private final String _dateStart;

        @Getter
        @SerializedName("weekdays")
        @NonNull
        private final String _repeatWeekDays;

        @Getter
        @SerializedName("time_end")
        @NonNull
        private final String _timeEnd;

        @Getter
        @SerializedName("time_start")
        @NonNull
        private final String _timeStart;
    }
}
