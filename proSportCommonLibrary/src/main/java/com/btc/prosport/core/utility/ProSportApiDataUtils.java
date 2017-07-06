package com.btc.prosport.core.utility;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.utility.WeekDay;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public final class ProSportApiDataUtils {
    private static final DateFormat TIME_FORMAT;

    private static final DateFormat DATE_FORMAT;

    private static final DateFormat DATE_TIME_FORMAT;

    static {
        TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
        TIME_FORMAT.getTimeZone().setRawOffset(0);

        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DATE_FORMAT.getTimeZone().setRawOffset(0);

        DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        DATE_TIME_FORMAT.getTimeZone().setRawOffset(0);
    }

    @Nullable
    public static Integer parseColor(@Nullable final String colorString) {
        Integer color = null;

        if (!TextUtils.isEmpty(colorString)) {
            try {
                color = Color.parseColor(colorString);
            } catch (final IllegalArgumentException ignored) {
                color = null;
            }
        }

        return color;
    }

    @Nullable
    public static String composeQueryOrder(@NonNull final String... queryOrders) {
        Contracts.requireNonNull(queryOrders, "queryOrders == null");

        return TextUtils.join(",", queryOrders);
    }

    @NonNull
    public static String formatDateTime(final long date) {
        return formatDateTime(new Date(date));
    }

    @NonNull
    public static String formatDateTime(@NonNull final Date date) {
        Contracts.requireNonNull(date, "date == null");

        return getDateTimeFormat().format(date);
    }

    @NonNull
    public static String formatDate(final long date) {
        return formatDate(new Date(date));
    }

    @NonNull
    public static String formatDate(@NonNull final Date date) {
        Contracts.requireNonNull(date, "date == null");

        return getDateFormat().format(date);
    }

    @NonNull
    public static String formatTime(final long date) {
        return formatTime(new Date(date));
    }

    @NonNull
    public static String formatTime(@NonNull final Date date) {
        Contracts.requireNonNull(date, "date == null");

        return getTimeFormat().format(date);
    }

    @Nullable
    public static Date parseTime(@Nullable final String time) {
        Date parsedTime = null;

        if (time != null) {
            try {
                parsedTime = getTimeFormat().parse(time);
            } catch (final ParseException ignored) {
                parsedTime = null;
            }
        }

        return parsedTime;
    }

    @Nullable
    public static Date parseDate(@Nullable final String date) {
        Date parsedDate = null;

        if (date != null) {
            try {
                parsedDate = getDateFormat().parse(date);
            } catch (final ParseException ignored) {
                parsedDate = null;
            }
        }

        return parsedDate;
    }

    @Nullable
    public static Date parseDateTime(@Nullable final String date) {
        Date parsedDateTime = null;

        if (date != null) {
            try {
                parsedDateTime = getDateTimeFormat().parse(date);
            } catch (final ParseException ignored) {
                parsedDateTime = null;
            }
        }

        return parsedDateTime;
    }

    @NonNull
    public static DateFormat getDateFormat() {
        return DATE_FORMAT;
    }

    @NonNull
    public static DateFormat getTimeFormat() {
        return TIME_FORMAT;
    }

    @NonNull
    public static DateFormat getDateTimeFormat() {
        return DATE_TIME_FORMAT;
    }

    @NonNull
    public static String makeLocationParam(final double latitude, final double longitude) {
        return latitude + "," + longitude;
    }

    @NonNull
    public static String makeQueryOrderParam(
        @NonNull final String fieldName, final boolean reverse) {
        Contracts.requireNonNull(fieldName, "fieldName == null");

        final String queryOrder;

        if (reverse) {
            queryOrder = "-" + fieldName;
        } else {
            queryOrder = fieldName;
        }

        return queryOrder;
    }

    @NonNull
    public static String makeRepeatingIntervalWeekDays(
        @Nullable final Collection<WeekDay> weekdays) {
        if (weekdays != null && !weekdays.isEmpty()) {
            final val days = CollectionUtils.collect(weekdays, new Transformer<WeekDay, String>() {
                @Override
                public String transform(final WeekDay input) {
                    return input.getId();
                }
            });
            return TextUtils.join(",", days);
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Nullable
    public static Collection<WeekDay> parseRepeatIntervalWeekDays(@Nullable final String str) {
        Collection<WeekDay> result = null;

        if (!TextUtils.isEmpty(str)) {
            result = CollectionUtils.collect(Arrays.asList(TextUtils.split(str, ",")),
                                             new Transformer<String, WeekDay>() {
                                                 @Override
                                                 public WeekDay transform(final String id) {
                                                     return WeekDay.byId(id);
                                                 }
                                             });
        }

        return result;
    }

    @NonNull
    public static String makeIntervalId(
        final long playgroundId, @NonNull final WeekDay weekDay, final int intervalIndex) {
        Contracts.requireNonNull(weekDay, "weekDay == null");

        return String.format(Locale.US,
                             "%1$s-%2$s-%3$s",
                             String.valueOf(playgroundId),
                             weekDay.getId(),
                             String.valueOf(intervalIndex));
    }

    private ProSportApiDataUtils() {
        Contracts.unreachable();
    }
}
