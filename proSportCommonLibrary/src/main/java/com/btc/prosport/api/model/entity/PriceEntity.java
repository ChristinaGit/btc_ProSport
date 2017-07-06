package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Price;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PriceEntity implements Price {
    public static final String FIELD_ID = "slug";

    public static final String FIELD_WEEK_DAY = "weekday";

    public static final String FIELD_INTERVAL_INDEX = "interval";

    public static final String FIELD_VALUE = "price";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    @Nullable
    private String _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_INTERVAL_INDEX)
    private int _intervalIndex;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_VALUE)
    @Nullable
    private Integer _value;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_WEEK_DAY)
    @Nullable
    private String _weekDay;
}
