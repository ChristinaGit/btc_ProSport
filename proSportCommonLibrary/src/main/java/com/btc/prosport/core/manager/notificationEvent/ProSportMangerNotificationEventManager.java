package com.btc.prosport.core.manager.notificationEvent;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.prosport.core.manager.notificationEvent.eventArgs.NotConfirmedOrdersChangedEventArgs;

public interface ProSportMangerNotificationEventManager {
    @NonNull
    Event<NotConfirmedOrdersChangedEventArgs> getNotConfirmedOrdersChangedEvent();
}
