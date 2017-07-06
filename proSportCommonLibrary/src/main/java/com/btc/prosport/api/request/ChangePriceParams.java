package com.btc.prosport.api.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public class ChangePriceParams {
    public ChangePriceParams(@NonNull final String priceId, @Nullable final Integer priceValue) {
        Contracts.requireNonNull(priceId, "priceId == null");

        _priceId = priceId;
        _priceValue = priceValue;
    }

    @Getter
    @SerializedName("slug")
    @NonNull
    private final String _priceId;

    @SerializedName("price")
    @Getter
    @Nullable
    private final Integer _priceValue;
}
