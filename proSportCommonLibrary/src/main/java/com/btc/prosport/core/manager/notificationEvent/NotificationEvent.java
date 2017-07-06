package com.btc.prosport.core.manager.notificationEvent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;

import java.util.Objects;

@Accessors(prefix = "_")
public enum NotificationEvent {
    ORDER_CONFIRMED("order_confirmed"),
    ORDER_CANCELLED("order_cancelled"),
    ORDER_TIME_CHANGED("order_time_changed"),
    ORDER_NOT_CONFIRMED("order_not_confirmed"),
    DISCOUNT("discount"),
    ORDER_CREATED("order_created"),
    SALE_CREATED("sale_created"),
    NOT_CONFIRMED_ORDERS_CHANGED("order_status_changed");

    @Nullable
    public static NotificationEvent byEventName(@Nullable final String eventName) {
        NotificationEvent result = null;

        for (final val notificationEvent : values()) {
            if (Objects.equals(notificationEvent.getEventName(), eventName)) {
                result = notificationEvent;
                break;
            }
        }

        return result;
    }

    @Getter
    @NonNull
    private final String _eventName;

    NotificationEvent(@NonNull final String eventName) {
        Contracts.requireNonNull(eventName, "eventName == null");

        _eventName = eventName;
    }
}
