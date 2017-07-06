package com.btc.prosport.api.request;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeOrderStateParams {
    public ChangeOrderStateParams(final int state) {
        _state = state;
    }

    @Getter
    @SerializedName("state")
    private final int _state;
}
