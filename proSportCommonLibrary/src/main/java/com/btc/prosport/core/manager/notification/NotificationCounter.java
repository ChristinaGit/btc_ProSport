package com.btc.prosport.core.manager.notification;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;

import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.common.extension.sharedPreferences.SharedPreferencesEditorWrapper;

import java.util.Set;

public class NotificationCounter extends SharedPreferencesEditorWrapper {
    private static final String KEY_PREFIX = NotificationCounter.class.getName() + "_";

    public int addNotification(final int id, @NonNull final String notificationJson) {
        Contracts.requireNonNull(notificationJson, "notificationJson == null");

        return addNotification(null, id, notificationJson);
    }

    public int addNotification(
        @Nullable final String tag, final int id, @NonNull final String notificationJson) {
        Contracts.requireNonNull(notificationJson, "notificationJson == null");

        val key = getKey(tag, id);
        val notifications = getNotifications(key, id);

        int increased;
        if (notifications != null) {
            increased = notifications.size();

            final boolean added = notifications.add(notificationJson);
            if (added) {
                increased++;
                putStringSet(key, notifications);
            }
        } else {
            final int count = 1;
            val stringSet = new ArraySet<String>(count);

            stringSet.add(notificationJson);
            putStringSet(key, stringSet);

            increased = count;
        }

        return increased;
    }

    public void clearAll() {
        clear();
    }

    public void clearNotificationCount(final int id) {
        clearNotificationCount(null, id);
    }

    public void clearNotificationCount(@Nullable final String tag, final int id) {
        remove(getKey(tag, id));
    }

    @Nullable
    public Set<String> getNotifications(final int id) {
        return getNotifications(null, id);
    }

    @Nullable
    public Set<String> getNotifications(@Nullable final String tag, final int id) {
        return getStringSet(getKey(tag, id));
    }

    /*package-private*/ NotificationCounter(
        @NonNull final SharedPreferences baseSharedPreferences) {
        super(Contracts.requireNonNull(baseSharedPreferences, "baseSharedPreferences == null"));
    }

    @NonNull
    private String getKey(@Nullable final String tag, final int id) {
        val keyBuilder = new StringBuilder(KEY_PREFIX);
        if (tag != null) {
            keyBuilder.append(tag).append("_");
        }
        keyBuilder.append(id);
        return keyBuilder.toString();
    }
}
