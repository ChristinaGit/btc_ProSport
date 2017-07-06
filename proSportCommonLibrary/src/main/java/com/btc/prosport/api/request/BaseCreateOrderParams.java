package com.btc.prosport.api.request;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Accessors(prefix = "_")
public abstract class BaseCreateOrderParams {
    protected BaseCreateOrderParams(
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
            @NonNull final String dateStart,
            @NonNull final String timeStart,
            @NonNull final String dateEnd,
            @NonNull final String timeEnd) {
            this(
                Contracts.requireNonNull(dateStart, "dateStart == null"),
                Contracts.requireNonNull(timeStart, "timeStart == null"),
                Contracts.requireNonNull(dateEnd, "dateEnd == null"),
                Contracts.requireNonNull(timeEnd, "timeEnd == null"),
                StringUtils.EMPTY);
        }

        public Interval(
            @NonNull final String dateStart,
            @NonNull final String timeStart,
            @NonNull final String dateEnd,
            @NonNull final String timeEnd,
            @NonNull final String repeatWeekDays) {
            _dateStart = dateStart;
            _timeStart = timeStart;
            _dateEnd = dateEnd;
            _timeEnd = timeEnd;
            _repeatWeekDays = repeatWeekDays;
            Contracts.requireNonNull(dateStart, "dateStart == null");
            Contracts.requireNonNull(timeStart, "timeStart == null");
            Contracts.requireNonNull(dateEnd, "dateEnd == null");
            Contracts.requireNonNull(timeEnd, "timeEnd == null");
            Contracts.requireNonNull(repeatWeekDays, "repeatWeekDays == null");
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
