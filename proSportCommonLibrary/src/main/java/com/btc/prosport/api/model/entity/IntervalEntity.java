package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Interval;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class IntervalEntity implements Interval {
    public static final String FIELD_ID = "slug";

    public static final String FIELD_IS_SALE = "is_sale";

    public static final String FIELD_INDEX = "interval";

    public static final String FIELD_STATE = "state";

    public static final String FIELD_PRICE = "price";

    public static final String FIELD_WEEK_DAY = "weekday";

    public static final String FIELD_ORDER_ID = "order_id";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    @Nullable
    private String _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_INDEX)
    private int _index;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_IS_SALE)
    private boolean _isSale;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ORDER_ID)
    @Nullable
    private Long _orderId;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PRICE)
    @Nullable
    private Integer _price;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_STATE)
    private String _stateCode;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_WEEK_DAY)
    @Nullable
    private String _weekDay;
}
