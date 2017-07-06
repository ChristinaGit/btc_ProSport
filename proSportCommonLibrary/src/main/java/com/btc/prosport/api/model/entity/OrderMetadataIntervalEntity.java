package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.OrderMetadataInterval;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class OrderMetadataIntervalEntity implements OrderMetadataInterval {
    public static final String FIELD_DATE_END = "date_end";

    public static final String FIELD_DATE_START = "date_start";

    public static final String FIELD_REPEAT_WEEK_DAYS = "weekdays";

    public static final String FIELD_TIME_END = "time_end";

    public static final String FIELD_TIME_START = "time_start";

    public static final String FIELD_ID = "id";

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_DATE_END)
    @Nullable
    private String _dateEnd;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_DATE_START)
    @Nullable
    private String _dateStart;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_REPEAT_WEEK_DAYS)
    @Nullable
    private String _repeatWeekDays;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_TIME_END)
    @Nullable
    private String _timeEnd;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_TIME_START)
    @Nullable
    private String _timeStart;
}
