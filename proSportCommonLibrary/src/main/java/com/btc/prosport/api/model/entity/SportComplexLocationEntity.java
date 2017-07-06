package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.SportComplexLocation;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SportComplexLocationEntity implements SportComplexLocation {
    public static final String FIELD_ID = "id";

    public static final String FIELD_FAVORITE_STATE = "is_favorite";

    public static final String FIELD_PLACE_ID = "place_id";

    public static final String FIELD_LOCAL_TIME = "local_time";

    public static final String FIELD_LOCATION_LATITUDE = "location_lat";

    public static final String FIELD_LOCATION_LONGITUDE = "location_lng";

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
    @SerializedName(FIELD_LOCATION_LATITUDE)
    @Nullable
    private Double _locationLatitude;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_LOCATION_LONGITUDE)
    @Nullable
    private Double _locationLongitude;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_PLACE_ID)
    @Nullable
    private String _placeId;
}
