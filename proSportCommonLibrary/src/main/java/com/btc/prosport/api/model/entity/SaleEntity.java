package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Sale;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SaleEntity implements Sale {
    public static final String FIELD_ID = "id";

    public static final String FIELD_VALUE = "value";

    public static final String FIELD_SALE_TYPE_CODE = "value_type";

    public static final String FIELD_CREATE_DATE = "sale_date";

    public static final String FIELD_DESCRIPTION = "description";

    public static final String FIELD_PLAYGROUND = "playground";

    public static final String FIELD_SPORT_COMPLEX = "sport_complex";

    public static final String FIELD_INTERVALS = "sale_meta_interval";

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_CREATE_DATE)
    @Nullable
    private String _createDate;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_DESCRIPTION)
    @Nullable
    private String _description;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_INTERVALS)
    @Nullable
    private List<SaleMetadataIntervalEntity> _intervals;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PLAYGROUND)
    @Nullable
    private List<PlaygroundTitleEntity> _playgrounds;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_SALE_TYPE_CODE)
    private int _saleTypeCode;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_SPORT_COMPLEX)
    @Nullable
    private List<SportComplexTitleEntity> _sportComplexes;

    @Getter(onMethod = @__(@Override))
    @SerializedName(FIELD_VALUE)
    private int _value;
}
