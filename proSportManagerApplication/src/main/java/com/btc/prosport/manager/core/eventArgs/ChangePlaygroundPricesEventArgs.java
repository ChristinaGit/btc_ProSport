package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.eventArgs.IdEventArgs;

import java.util.Map;

@Accessors(prefix = "_")
public class ChangePlaygroundPricesEventArgs extends IdEventArgs {
    public ChangePlaygroundPricesEventArgs(
        final long playgroundId, @NonNull final Map<String, Integer> newPrices) {
        super(playgroundId);
        Contracts.requireNonNull(newPrices, "newPrices == null");

        _newPrices = newPrices;
    }

    @Getter
    @NonNull
    private final Map<String, Integer> _newPrices;
}
