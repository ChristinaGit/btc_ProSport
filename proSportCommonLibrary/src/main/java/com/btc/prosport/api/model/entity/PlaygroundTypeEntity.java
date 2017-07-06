package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.PlaygroundType;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class PlaygroundTypeEntity implements PlaygroundType {
    public static final String FIELD_ID = "id";

    public static final String FIELD_NAME = "name";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ID)
    private long _id;

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_NAME)
    @Nullable
    private String _name;
}
