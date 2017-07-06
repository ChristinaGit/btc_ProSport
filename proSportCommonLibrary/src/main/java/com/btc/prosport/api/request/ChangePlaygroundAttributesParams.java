package com.btc.prosport.api.request;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Accessors(prefix = "_")
public final class ChangePlaygroundAttributesParams {

    public ChangePlaygroundAttributesParams(@NonNull final List<Long> attributesIds) {
        _attributesIds = attributesIds;
    }

    @Getter
    @SerializedName("attribute")
    private final List<Long> _attributesIds;
}
