package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.WorkPeriod;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class WorkPeriodEntity implements WorkPeriod {
    public static final String FIELD_END = "end";

    public static final String FIELD_START = "start";

    public static final String FIELD_WEEKDAY = "weekday";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_END)
    @Nullable
    private String _end;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_START)
    @Nullable
    private String _start;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_WEEKDAY)
    @Nullable
    private String _weekDay;
}
