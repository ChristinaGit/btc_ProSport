package com.btc.prosport.manager.core.manager.notification;

import com.btc.prosport.core.manager.notification.NotificationType;

public enum ManagerNotificationType implements NotificationType {
    ORDER_CREATED(true);

    @Override
    public int getId() {
        return ordinal();
    }

    @Override
    public boolean isUserType() {
        return _isUserType;
    }

    private final boolean _isUserType;

    ManagerNotificationType(final boolean isUserType) {
        _isUserType = isUserType;
    }
}
