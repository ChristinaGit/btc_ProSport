package com.btc.prosport.api.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Accessors(prefix = "_")
public abstract class BaseChangeOrderParams {
    protected BaseChangeOrderParams(
        final long playgroundId, @NonNull final List<Interval> intervals) {
        Contracts.requireNonNull(intervals, "intervals == null");

        _playgroundId = playgroundId;
        _intervals = intervals;
    }

    @Getter
    @SerializedName("meta_interval")
    @NonNull
    private final List<Interval> _intervals;

    @Getter
    @SerializedName("playground")
    private final long _playgroundId;

    public static final class Interval {
        public Interval(
            @Nullable final Long id,
            @NonNull final String dateStart,
            @NonNull final String timeStart,
            @NonNull final String dateEnd,
            @NonNull final String timeEnd,
            @NonNull final String repeatWeekDays) {
            Contracts.requireNonNull(dateStart, "dateStart == null");
            Contracts.requireNonNull(timeStart, "timeStart == null");
            Contracts.requireNonNull(dateEnd, "dateEnd == null");
            Contracts.requireNonNull(timeEnd, "timeEnd == null");
            Contracts.requireNonNull(repeatWeekDays, "repeatWeekDays == null");

            _id = id;
            _dateStart = dateStart;
            _timeStart = timeStart;
            _dateEnd = dateEnd;
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
        @SerializedName("id")
        @Nullable
        private final Long _id;

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
