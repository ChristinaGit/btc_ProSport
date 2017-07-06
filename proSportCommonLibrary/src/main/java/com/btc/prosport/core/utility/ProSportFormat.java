package com.btc.prosport.core.utility;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.Interval;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.PlaygroundTitle;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.SaleMetadataInterval;
import com.btc.prosport.api.model.SportComplexTitle;
import com.btc.prosport.api.model.SubwayStation;
import com.btc.prosport.api.model.utility.WeekDay;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ProSportFormat {
    @Nullable
    public static String getFormattedPlace(
        @Nullable final String sportComplexName, @Nullable final String playgroundName) {

        final String result;

        final val hasSportComplexNameName = !TextUtils.isEmpty(sportComplexName);
        final val hasPlaygroundName = !TextUtils.isEmpty(playgroundName);

        if (hasPlaygroundName || hasSportComplexNameName) {
            if (!hasSportComplexNameName) {
                result = playgroundName;
            } else if (!hasPlaygroundName) {
                result = sportComplexName;
            } else {
                result = String.format("%1$s (%2$s)", sportComplexName, playgroundName);
            }
        } else {
            result = null;
        }

        return result;
    }

    @Nullable
    public static String getFormattedOrderPlace(@NonNull final Order order) {
        Contracts.requireNonNull(order, "order == null");

        final String result;

        final val sportComplex = order.getSportComplex();
        final val playground = order.getPlayground();

        final val playgroundName = playground == null ? null : playground.getName();
        final val sportComplexName = sportComplex == null ? null : sportComplex.getName();

        result = getFormattedPlace(sportComplexName, playgroundName);

        return result;
    }

    @Nullable
    public static String getFormattedSubwayStations(
        @Nullable final List<? extends SubwayStation> subwayStations) {
        final String formattedSubwayStations;

        List<String> subwayStationsNames = null;
        if (subwayStations != null && !subwayStations.isEmpty()) {
            subwayStationsNames = new ArrayList<>(subwayStations.size());

            for (final val subwayStation : subwayStations) {
                final val subwayStationName = subwayStation.getName();
                if (!TextUtils.isEmpty(subwayStationName)) {
                    subwayStationsNames.add(subwayStationName);
                }
            }
        }

        if (subwayStationsNames != null && !subwayStationsNames.isEmpty()) {
            formattedSubwayStations = TextUtils.join(" \u00B7 ", subwayStationsNames);
        } else {
            formattedSubwayStations = null;
        }

        return formattedSubwayStations;
    }

    @NonNull
    public static CharSequence getFormattedOrderSalePrice(@NonNull final Order order) {
        Contracts.requireNonNull(order, "order == null");

        if (OrderUtils.isWithSale(order)) {
            final val stringBuilder = new SpannableStringBuilder();
            final val originalPrice =
                String.format(Locale.getDefault(), "%1$d \u20BD", order.getOriginalPrice());
            final val price = String.format(Locale.getDefault(), "%1$d \u20BD", order.getPrice());
            stringBuilder.append(originalPrice);
            stringBuilder.setSpan(new StrikethroughSpan(),
                                  0,
                                  originalPrice.length(),
                                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            stringBuilder.setSpan(new ForegroundColorSpan(Color.GRAY),
                                  0,
                                  originalPrice.length(),
                                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new RelativeSizeSpan(.75f),
                                  0,
                                  originalPrice.length(),
                                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            stringBuilder.insert(0, StringUtils.LF);
            stringBuilder.insert(0, price);

            return stringBuilder;
        } else {
            return String.format(Locale.getDefault(), "%1$d \u20BD", order.getPrice());
        }
    }

    @NonNull
    public static String getFormattedSaleIntervals(
        @NonNull final Context context,
        @NonNull final Sale sale,
        @NonNull final String intervalsDelimiter) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(sale, "sale == null");
        Contracts.requireNonNull(intervalsDelimiter, "intervalsDelimiter == null");

        final val intervals = (List<SaleMetadataInterval>) sale.getIntervals();
        final val intervalToString = new Transformer<SaleMetadataInterval, String>() {
            @Override
            public String transform(final SaleMetadataInterval interval) {
                return ProSportFormat.getFormattedInterval(context, interval);
            }
        };
        final val formattedIntervals = CollectionUtils.collect(intervals, intervalToString);

        return TextUtils.join(intervalsDelimiter, formattedIntervals);
    }

    @NonNull
    public static String getFormattedOrderIntervals(
        @NonNull final Context context,
        @NonNull final Order order,
        @NonNull final String intervalsDelimiter) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(order, "order == null");
        Contracts.requireNonNull(intervalsDelimiter, "intervalsDelimiter == null");

        final val intervals = (List<OrderMetadataInterval>) order.getIntervals();
        final val intervalToString = new Transformer<OrderMetadataInterval, String>() {
            @Override
            public String transform(final OrderMetadataInterval interval) {
                return ProSportFormat.getFormattedInterval(context, interval);
            }
        };
        final val formattedIntervals = CollectionUtils.collect(intervals, intervalToString);

        return TextUtils.join(intervalsDelimiter, formattedIntervals);
    }

    @Nullable
    public static String getFormattedIntervalDateTime(
        @NonNull final Context context, @NonNull final SaleMetadataInterval interval) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(interval, "interval == null");

        return getFormattedIntervalDateTime(context, wrapMetaInterval(interval));
    }

    @Nullable
    public static String getFormattedIntervalDateTime(
        @NonNull final Context context, @NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(interval, "interval == null");

        return getFormattedIntervalDateTime(context, wrapMetaInterval(interval));
    }

    @Nullable
    public static String getFormattedInterval(
        @NonNull final Context context, @NonNull final SaleMetadataInterval interval) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(interval, "interval == null");

        return getFormattedInterval(context, wrapMetaInterval(interval));
    }

    @Nullable
    public static String getFormattedInterval(
        @NonNull final Context context, @NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(interval, "interval == null");

        return getFormattedInterval(context, wrapMetaInterval(interval));
    }

    @Nullable
    private static String getFormattedInterval(
        @NonNull final Context context, @NonNull final MetadataInterval interval) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(interval, "interval == null");

        final String result;

        final val intervalDateTime = getFormattedIntervalDateTime(context, interval);

        final val repeatWeekDays = interval.getRepeatWeekDays();
        if (repeatWeekDays != null && !repeatWeekDays.isEmpty()) {
            final val weekDays = ProSportApiDataUtils.parseRepeatIntervalWeekDays(repeatWeekDays);

            final val shortWeekdays = DateFormatSymbols.getInstance().getShortWeekdays();
            final val formattedWeekdays =
                CollectionUtils.collect(weekDays, new Transformer<WeekDay, String>() {
                    @Override
                    public String transform(final WeekDay weekDay) {
                        return shortWeekdays[weekDay.getCode()];
                    }
                });

            result = String.format("%1$s (%2$s)",
                                   intervalDateTime,
                                   TextUtils.join(", ", formattedWeekdays));
        } else {
            result = intervalDateTime;
        }

        return result;
    }

    @Nullable
    private static String getFormattedIntervalDateTime(
        @NonNull final Context context, @NonNull final MetadataInterval interval) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(interval, "interval == null");

        final String result;

        final val timeFormat = getIntervalTimeFormat(context);
        final val dateFormat = getIntervalDateFormat();

        final val startDate = ProSportApiDataUtils.parseDate(interval.getDateStart());
        final val endDate = ProSportApiDataUtils.parseDate(interval.getDateEnd());
        final val startTime = ProSportApiDataUtils.parseTime(interval.getTimeStart());
        final val endTime = ProSportApiDataUtils.parseTime(interval.getTimeEnd());

        if (Objects.equals(startDate, endDate)) {
            if (startDate != null && startTime != null && endTime != null) {
                result = String.format("%1$s %2$s \u2013 %3$s",
                                       dateFormat.format(startDate),
                                       timeFormat.format(startTime),
                                       timeFormat.format(endTime));
            } else {
                result = null;
            }
        } else {

            if (startDate != null && endDate != null && startTime != null && endTime != null) {
                result = String.format("%1$s %2$s \u2013 %3$s %4$s",
                                       dateFormat.format(startDate),
                                       timeFormat.format(startTime),
                                       dateFormat.format(endDate),
                                       timeFormat.format(endTime));
            } else {
                result = null;
            }
        }

        return result;
    }

    @NonNull
    private static MetadataInterval wrapMetaInterval(
        @NonNull final OrderMetadataInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        return new MetadataInterval(interval);
    }

    @NonNull
    private static MetadataInterval wrapMetaInterval(@NonNull final SaleMetadataInterval interval) {
        Contracts.requireNonNull(interval, "interval == null");

        return new MetadataInterval(interval);
    }

    @NonNull
    public static java.text.DateFormat getIntervalLongDateFormat() {
        final val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "d MMMM yyyy");
        final val dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        dateFormat.getTimeZone().setRawOffset(0);

        return dateFormat;
    }

    @NonNull
    public static java.text.DateFormat getIntervalDateFormat() {
        final val dateFormat =
            new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "d MM"),
                                 Locale.getDefault());
        dateFormat.getTimeZone().setRawOffset(0);

        return dateFormat;
    }

    @NonNull
    public static java.text.DateFormat getIntervalTimeFormat(@NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        final val timeFormat = DateFormat.getTimeFormat(context);
        timeFormat.getTimeZone().setRawOffset(0);

        return timeFormat;
    }

    @NonNull
    public static String getFormattedIntervalsDateTime(
        @NonNull final Context context,
        @NonNull final Interval startInterval,
        @NonNull final Interval endInterval,
        final long date) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(startInterval, "startInterval == null");
        Contracts.requireNonNull(endInterval, "endInterval == null");

        final val timeFormat = getIntervalTimeFormat(context);
        final val dateFormat = getIntervalDateFormat();

        final long startTime = IntervalUtils.getStartTime(startInterval);
        final long endTime = IntervalUtils.getEndTime(endInterval);

        return String.format("%1$s %2$s \u2013 %3$s",
                             dateFormat.format(date),
                             timeFormat.format(startTime),
                             timeFormat.format(endTime));
    }

    @NonNull
    public static String getFormattedIntervalsDateTime(
        @NonNull final Context context,
        @NonNull final Interval startInterval,
        @NonNull final Interval endInterval,
        final long dateStart,
        final long dateEnd) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(startInterval, "startInterval == null");
        Contracts.requireNonNull(endInterval, "endInterval == null");

        final val timeFormat = getIntervalTimeFormat(context);
        final val dateFormat = getIntervalDateFormat();

        final long startTime = IntervalUtils.getStartTime(startInterval);
        final long endTime = IntervalUtils.getEndTime(endInterval);

        return String.format("%1$s %2$s \u2013 %3$s %4$s",
                             dateFormat.format(dateStart),
                             timeFormat.format(startTime),
                             dateFormat.format(dateEnd),
                             timeFormat.format(endTime));
    }

    @NonNull
    public static String getFormattedSalePlace(@NonNull final Sale sale) {
        Contracts.requireNonNull(sale, "sale == null");

        final String place;

        final val sportComplexes = sale.getSportComplexes();
        final val playgrounds = sale.getPlaygrounds();

        final val sportComplexToPlace = new Transformer<SportComplexTitle, String>() {
            @Override
            public String transform(final SportComplexTitle sportComplex) {
                final String sportComplexPlace;

                String playgroundsPlaces = null;

                if (playgrounds != null) {
                    final val playgroundBySportComplex = new Predicate<PlaygroundTitle>() {
                        @Override
                        public boolean evaluate(final PlaygroundTitle playground) {
                            return playground.getSportComplexId() == sportComplex.getId();
                        }
                    };

                    final val sportComplexPlaygrounds =
                        IterableUtils.filteredIterable(playgrounds, playgroundBySportComplex);

                    final val playgroundToName = new Transformer<PlaygroundTitle, String>() {
                        @Override
                        public String transform(final PlaygroundTitle playground) {
                            return playground.getName();
                        }
                    };
                    final val playgroundsNames = IterableUtils.transformedIterable(
                        sportComplexPlaygrounds,
                        playgroundToName);

                    playgroundsPlaces = TextUtils.join(", ", playgroundsNames);
                }

                if (playgroundsPlaces == null) {
                    sportComplexPlace = sportComplex.getName();
                } else {
                    sportComplexPlace =
                        String.format("%1$s (%2$s)", sportComplex.getName(), playgroundsPlaces);
                }

                return sportComplexPlace;
            }
        };
        final val sportComplexPlaces = CollectionUtils.collect(sportComplexes, sportComplexToPlace);
        place = TextUtils.join("\n\n", sportComplexPlaces);

        return place;
    }

    private ProSportFormat() {
        Contracts.unreachable();
    }

    private static final class MetadataInterval {
        @Nullable
        public final String getDateEnd() {
            return _orderInterval != null
                   ? _orderInterval.getDateEnd()
                   : _saleInterval.getDateEnd();
        }

        @Nullable
        public final String getDateStart() {
            return _orderInterval != null
                   ? _orderInterval.getDateStart()
                   : _saleInterval.getDateStart();
        }

        @Nullable
        public final String getRepeatWeekDays() {
            return _orderInterval != null
                   ? _orderInterval.getRepeatWeekDays()
                   : _saleInterval.getRepeatWeekDays();
        }

        @Nullable
        public final String getTimeEnd() {
            return _orderInterval != null
                   ? _orderInterval.getTimeEnd()
                   : _saleInterval.getTimeEnd();
        }

        @Nullable
        public final String getTimeStart() {
            return _orderInterval != null
                   ? _orderInterval.getTimeStart()
                   : _saleInterval.getTimeStart();
        }

        @Nullable
        private final OrderMetadataInterval _orderInterval;

        @Nullable
        private final SaleMetadataInterval _saleInterval;

        private MetadataInterval(
            @NonNull final OrderMetadataInterval orderInterval) {
            Contracts.requireNonNull(orderInterval, "orderInterval == null");

            _orderInterval = orderInterval;
            _saleInterval = null;
        }

        private MetadataInterval(
            @NonNull final SaleMetadataInterval orderInterval) {
            _orderInterval = null;
            _saleInterval = orderInterval;
        }
    }
}
