package com.btc.prosport.api.model.entity;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.prosport.api.model.SportKind;
import com.google.gson.annotations.SerializedName;

@ToString(doNotUseGetters = true)
@Accessors(prefix = "_")
public final class SportKindEntity implements SportKind {
    public static final String FIELD_ID = "id";

    public static final String FIELD_ICON = "icon";

    public static final String FIELD_NAME = "name";

    @Getter(onMethod = @__(@Override))
    @Setter
    @SerializedName(FIELD_ICON)
    @Nullable
    private String _icon;

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
