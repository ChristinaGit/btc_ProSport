package com.btc.prosport.player.core.manager.notification;

import com.btc.prosport.core.manager.notification.NotificationType;

public enum PlayerNotificationType implements NotificationType {
    ORDER_CONFIRMED(true),
    ORDER_CANCELLED(true),
    ORDER_TIME_CHANGED(true),
    ORDER_NOT_CONFIRMED(true),
    DISCOUNT(true);

    @Override
    public int getId() {
        return ordinal();
    }

    @Override
    public boolean isUserType() {
        return _isUserType;
    }

    private final boolean _isUserType;

    PlayerNotificationType(final boolean isUserType) {
        _isUserType = isUserType;
    }
}
