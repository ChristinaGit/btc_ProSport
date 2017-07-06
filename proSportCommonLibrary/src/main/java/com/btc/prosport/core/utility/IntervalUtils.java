package com.btc.prosport.core.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.utility.NumberUtils;
import com.btc.prosport.api.ProSportDataContract;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.OrderInterval;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.utility.IntervalState;

public final class IntervalUtils {
    @Nullable
    public static IntervalState getState(@Nullable final Interval interval) {
        final val stateCode = interval == null ? null : interval.getStateCode();

        return stateCode == null ? null : IntervalState.byCode(stateCode);
    }

    public static long getAlignedIntervalTime(final long time) {
        return NumberUtils.getAlignedValue(time, ProSportDataContract.INTERVAL_LENGTH);
    }

    public static long endTimeToStartTime(final long endTime) {
        return endTime - ProSportDataContract.INTERVAL_LENGTH;
    }

    public static long startTimeToEndTime(final long endTime) {
        return endTime + ProSportDataContract.INTERVAL_LENGTH;
    }

    public static long getStartTime(@NonNull final Interval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final val index = interval.getIndex();
        return getStartTime(index);
    }

    public static long getStartTime(final int index) {
        return ProSportDataContract.INTERVAL_LENGTH *
               (index - ProSportDataContract.FIRST_INTERVAL_INDEX);
    }

    public static long getEndTime(final int index) {
        return ProSportDataContract.INTERVAL_LENGTH *
               (index - ProSportDataContract.FIRST_INTERVAL_INDEX + 1);
    }

    public static long getEndTime(@NonNull final Interval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final val index = interval.getIndex();
        return getEndTime(index);
    }

    public static long getStartTime(@NonNull final OrderInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final val index = interval.getStartIntervalIndex();
        return getStartTime(index);
    }

    public static long getEndTime(@NonNull final OrderInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final val index = interval.getEndIntervalIndex();
        return getEndTime(index);
    }

    @Nullable
    public static Long getStartTime(@NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final Long result;

        final val startTime = ProSportApiDataUtils.parseTime(interval.getTimeStart());
        if (startTime != null) {
            result = startTime.getTime();
        } else {
            result = null;
        }

        return result;
    }

    @Nullable
    public static Long getEndTime(@NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final Long result;

        final val endTime = ProSportApiDataUtils.parseTime(interval.getTimeEnd());
        if (endTime != null) {
            result = endTime.getTime();
        } else {
            result = null;
        }

        return result;
    }

    @Nullable
    public static Long getStartDate(
        @NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final Long result;

        final val startTime = ProSportApiDataUtils.parseDate(interval.getDateStart());
        if (startTime != null) {
            result = startTime.getTime();
        } else {
            result = null;
        }

        return result;
    }

    @Nullable
    public static Long getEndDate(
        @NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        final Long result;

        final val endTime = ProSportApiDataUtils.parseDate(interval.getDateEnd());
        if (endTime != null) {
            result = endTime.getTime();
        } else {
            result = null;
        }

        return result;
    }

    private IntervalUtils() {
        Contracts.unreachable();
    }
}
