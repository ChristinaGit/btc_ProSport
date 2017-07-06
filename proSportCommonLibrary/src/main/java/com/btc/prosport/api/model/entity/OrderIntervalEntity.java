package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.OrderInterval;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class OrderIntervalEntity implements OrderInterval {
    public static final String FIELD_ID = "id";

    public static final String FIELD_DATE = "date";

    public static final String FIELD_START_INTERVAL_INDEX = "interval_start";

    public static final String FIELD_END_INTERVAL_INDEX = "interval_end";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_DATE)
    @Nullable
    private String _date;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_END_INTERVAL_INDEX)
    private int _endIntervalIndex;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_START_INTERVAL_INDEX)
    private int _startIntervalIndex;
}
