package com.btc.prosport.core.manager.notification;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public interface ProSportNotificationManager<TNotificationType extends NotificationType> {
    void cancelAll();

    void cancelAllUserNotifications();

    void cancelNotification(@Nullable String tag, int id);

    void cancelNotification(int id);

    void cancelNotification(@NonNull TNotificationType type);

    @Nullable
    TNotificationType getNotificationType(@NonNull Map<String, String> params);

    TNotificationType[] getNotificationTypes();

    void showNotification(@NonNull TNotificationType type, @Nullable Map<String, String> params);
}
