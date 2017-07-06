package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.SportComplexPreview;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SportComplexPreviewEntity implements SportComplexPreview {
    public static final String FIELD_ID = "id";

    public static final String FIELD_CITY = "city";

    public static final String FIELD_FAVORITE_STATE = "is_favorite";

    public static final String FIELD_DISTANCE = "distance";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_PHOTO = "photo";

    public static final String FIELD_SUBWAY_STATIONS = "subway_station";

    public static final String FIELD_MINIMUM_PRICE = "price";

    public static final String FIELD_LOCAL_TIME = "local_time";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_CITY)
    @Nullable
    private CityEntity _city;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_DISTANCE)
    @Nullable
    private Long _distance;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_FAVORITE_STATE)
    @Nullable
    private Boolean _favoriteState;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_LOCAL_TIME)
    @Nullable
    private String _localTime;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_MINIMUM_PRICE)
    @Nullable
    private Integer _minimumPrice;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NAME)
    @Nullable
    private String _name;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PHOTO)
    @Nullable
    private PhotoEntity _photo;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_SUBWAY_STATIONS)
    @Nullable
    private List<SubwayStationEntity> _subwayStations;
}
