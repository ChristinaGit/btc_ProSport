package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.SportComplexTitle;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SportComplexTitleEntity implements SportComplexTitle {
    public static final String FIELD_ID = "id";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_FAVORITE_STATE = "is_favorite";

    public static final String FIELD_PHONE_NUMBERS = "phones";

    public static final String FIELD_LOCAL_TIME = "local_time";

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
}
