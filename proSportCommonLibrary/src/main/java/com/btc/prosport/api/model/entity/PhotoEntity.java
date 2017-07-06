package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.Photo;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PhotoEntity implements Photo {
    public static final String FIELD_ID = "id";

    public static final String FIELD_URI = "photo";

    public static final String FIELD_POSITION = "position";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_POSITION)
    private int _position;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_URI)
    @Nullable
    private String _uri;
}
