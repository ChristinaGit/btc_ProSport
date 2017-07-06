package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.SportComplexInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SportComplexInfoEntity implements SportComplexInfo {
    public static final String FIELD_ADDRESS = "address";

    public static final String FIELD_FAVORITE_STATE = "is_favorite";

    public static final String FIELD_ID = "id";

    public static final String FIELD_LOCAL_TIME = "local_time";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_PHONE_NUMBERS = "phone";

    public static final String FIELD_PHOTOS = "photo";

    public static final String FIELD_WORK_PERIODS = "work_time";

    public static final String FIELD_SUBWAY_STATIONS = "subway_station";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ADDRESS)
    @Nullable
    private String _address;

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
    @SerializedName(FIELD_NAME)
    @Nullable
    private String _name;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PHONE_NUMBERS)
    @Nullable
    private List<String> _phoneNumbers;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PHOTOS)
    @Nullable
    private List<PhotoEntity> _photos;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_SUBWAY_STATIONS)
    @Nullable
    private List<SubwayStationEntity> _subwayStations;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_WORK_PERIODS)
    @Nullable
    private List<WorkPeriodEntity> _workPeriods;
}
