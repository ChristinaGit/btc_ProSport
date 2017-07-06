package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.PlaygroundReport;
import com.btc.prosport.api.model.SportComplexReport;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SportComplexReportEntity implements SportComplexReport {
    private static final String FIELD_ID = "id";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_PLAYGROUNDS = "playground";

    private static final String FIELD_NOT_CONFIRMED_ORDERS_COUNT = "orders_count";

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

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PLAYGROUNDS)
    @Nullable
    private List<PlaygroundReportEntity> _playgrounds;
}
