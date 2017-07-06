package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.PlaygroundReport;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PlaygroundReportEntity implements PlaygroundReport {
    private static final String FIELD_ID = "id";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_COLOR = "color";

    private static final String FIELD_NOT_CONFIRMED_ORDERS_COUNT = "orders_count";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_COLOR)
    @Nullable
    private String _color;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NAME)
    @Nullable
    private String _name;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NOT_CONFIRMED_ORDERS_COUNT)
    @Nullable
    private Integer _notConfirmedOrdersCount;
}
