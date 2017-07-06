package com.btc.prosport.api.request;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Accessors(prefix = "_")
public final class CreateOrderParams extends BaseCreateOrderParams {
    public CreateOrderParams(
        final long playerId, final long playgroundId, @NonNull final List<Interval> intervals) {
        super(playgroundId, Contracts.requireNonNull(intervals, "intervals == null"));

        _playerId = playerId;
    }

    @Getter
    @SerializedName("player")
    private final long _playerId;
}
