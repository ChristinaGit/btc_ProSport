package com.btc.prosport.api.request;

import android.support.annotation.NonNull;

import com.btc.common.contract.Contracts;

import java.util.List;

public final class CreateOrderForMeParams extends BaseCreateOrderParams {
    public CreateOrderForMeParams(
        final long playgroundId, @NonNull final List<Interval> intervals) {
        super(playgroundId, Contracts.requireNonNull(intervals, "intervals == null"));
    }
}
