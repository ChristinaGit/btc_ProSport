package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Order;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class OrderEntity implements Order {
    public static final String FIELD_ID = "id";

    public static final String FIELD_CREATE_DATE = "order_date";

    public static final String FIELD_INTERVALS = "order_meta_interval";

    public static final String FIELD_PART = "part";

    public static final String FIELD_PLAYER = "player";

    public static final String FIELD_PLAYGROUND = "playground";

    public static final String FIELD_SPORT_COMPLEX = "sport_complex";

    public static final String FIELD_PRICE = "price";

    public static final String FIELD_ORIGINAL_PRICE = "original_price";

    public static final String FIELD_STATE_CODE = "state";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_CREATE_DATE)
    @Nullable
    private String _createDate;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_INTERVALS)
    @Nullable
    private List<OrderMetadataIntervalEntity> _intervals;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ORIGINAL_PRICE)
    private int _originalPrice;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PART)
    @Nullable
    private Boolean _part;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PLAYER)
    @Nullable
    private UserEntity _player;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PLAYGROUND)
    @Nullable
    private PlaygroundTitleEntity _playground;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PRICE)
    private int _price;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_SPORT_COMPLEX)
    @Nullable
    private SportComplexTitleEntity _sportComplex;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_STATE_CODE)
    private int _stateCode;
}
