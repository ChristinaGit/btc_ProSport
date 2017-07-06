package com.btc.prosport.core.manager.notificationEvent.eventArgs;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.btc.common.event.eventArgs.EventArgs;
import com.btc.prosport.api.model.PlaygroundReport;

@Accessors(prefix = "_")
public class NotConfirmedOrdersChangedEventArgs extends EventArgs {
    public NotConfirmedOrdersChangedEventArgs(@NonNull final PlaygroundReport playground) {
        Contracts.requireNonNull(playground, "playground == null");

        _playground = playground;
    }

    @Getter
    @NonNull
    private final PlaygroundReport _playground;
}
