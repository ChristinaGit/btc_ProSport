package com.btc.prosport.core.manager.notificationEvent;

import android.support.annotation.NonNull;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.event.Events;
import com.btc.common.event.generic.Event;
import com.btc.common.event.generic.ManagedEvent;
import com.btc.prosport.api.model.PlaygroundReport;
import com.btc.prosport.core.manager.notificationEvent.eventArgs.NotConfirmedOrdersChangedEventArgs;

@Accessors(prefix = "_")
public final class ProSportInternalNotificationEventManger
    implements ProSportMangerNotificationEventManager, ProSportPlayerNotificationEventManager {
    @NonNull
    @Override
    public final Event<NotConfirmedOrdersChangedEventArgs> getNotConfirmedOrdersChangedEvent() {
        return _notConfirmedOrdersChangedEvent;
    }

    public final void postNotConfirmedOrdersChangedMessage(
        @NonNull final PlaygroundReport playground) {
        Contracts.requireNonNull(playground, "playground == null");

        //@formatter:off
        final val eventArgs = new NotConfirmedOrdersChangedEventArgs(playground);
        //@formatter:on
        _notConfirmedOrdersChangedEvent.rise(eventArgs);
    }

    @NonNull
    private final ManagedEvent<NotConfirmedOrdersChangedEventArgs> _notConfirmedOrdersChangedEvent =
        Events.createEvent();
}
