package com.btc.prosport.api.request;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangePlaygroundCoveringParams {

    public ChangePlaygroundCoveringParams(final long coveringId) {
        _coveringId = coveringId;
    }

    @Getter
    @SerializedName("covering")
    private final long _coveringId;
}
