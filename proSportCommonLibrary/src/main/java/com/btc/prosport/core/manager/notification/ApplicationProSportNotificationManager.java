package com.btc.prosport.core.manager.notification;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

@Accessors(prefix = "_")
public abstract class ApplicationProSportNotificationManager<TNotificationType extends
    NotificationType>
    implements ProSportNotificationManager<TNotificationType> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ApplicationProSportNotificationManager.class);

    @CallSuper
    @Override
    public void cancelAll() {
        getNotificationManagerCompat().cancelAll();

        getNotificationCounter().clearAll();
    }

    @Override
    public void cancelAllUserNotifications() {
        for (final val type : getNotificationTypes()) {
            if (type.isUserType()) {
                cancelNotification(type);
            }
        }
    }

    @Override
    public void cancelNotification(@Nullable final String tag, final int id) {
        getNotificationManagerCompat().cancel(tag, id);

        getNotificationCounter().clearNotificationCount(tag, id);
    }

    @Override
    public void cancelNotification(final int id) {
        getNotificationManagerCompat().cancel(id);

        getNotificationCounter().clearNotificationCount(id);
    }

    @CallSuper
    @Override
    public void cancelNotification(@NonNull final TNotificationType type) {
        Contracts.requireNonNull(type, "type == null");

        final int id = type.getId();

        getNotificationManagerCompat().cancel(id);

        getNotificationCounter().clearNotificationCount(id);
    }

    @CallSuper
    @Override
    public void showNotification(
        @NonNull final TNotificationType type, @Nullable final Map<String, String> params) {
        Contracts.requireNonNull(type, "type == null");

        val userSettingsHelper = getUserSettingsHelper();

        final boolean userNotifications = userSettingsHelper.getUserNotifications();
        if (getNotificationManagerCompat().areNotificationsEnabled() && userNotifications) {
            val builder = new NotificationCompat.Builder(getContext());

            prepareBuilder(userSettingsHelper, builder);

            onShowNotification(builder, type, params);
        }
    }

    protected ApplicationProSportNotificationManager(
        @NonNull final Context context,
        @NonNull final String tag,
        @NonNull final UserSettingsHelper userSettingsHelper) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(tag, "tag == null");
        Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null");

        _context = context;
        _userSettingsHelper = userSettingsHelper;
        _notificationManagerCompat = NotificationManagerCompat.from(context);

        val sharedPreferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
        _notificationCounter = new NotificationCounter(sharedPreferences);
    }

    @Nullable
    protected Set<String> getCurrentNotifications(
        @NonNull final TNotificationType notificationType, @Nullable final String tag) {
        Contracts.requireNonNull(notificationType, "notificationType == null");

        return _notificationCounter.getNotifications(tag, notificationType.getId());
    }

    protected void prepareBuilder(
        @NonNull final UserSettingsHelper userSettingsHelper,
        @NonNull final NotificationCompat.Builder builder) {
        Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null");
        Contracts.requireNonNull(builder, "builder == null");

        builder.setCategory(NotificationCompat.CATEGORY_EVENT);
        int defaults = NotificationCompat.DEFAULT_LIGHTS;
        val notificationSound = userSettingsHelper.getNotificationSound();
        if (!StringUtils.isEmpty(notificationSound)) {
            builder.setSound(Uri.parse(notificationSound));
        } else {
            defaults |= NotificationCompat.DEFAULT_SOUND;
        }
        if (userSettingsHelper.getNotificationVibration()) {
            defaults |= NotificationCompat.DEFAULT_VIBRATE;
        }

        builder.setDefaults(defaults);
    }

    protected int saveNotification(
        @NonNull final TNotificationType notificationType,
        @Nullable final String tag,
        @NonNull final String notificationJson) {
        Contracts.requireNonNull(notificationType, "notificationType == null");
        Contracts.requireNonNull(notificationJson, "notificationJson == null");

        return _notificationCounter.addNotification(tag,
                                                    notificationType.getId(),
                                                    notificationJson);
    }

    protected void showNotification(
        @Nullable final String tag, final int id, @NonNull final Notification notification) {
        Contracts.requireNonNull(notification, "notification == null");

        val notificationManagerCompat = getNotificationManagerCompat();
        if (tag == null) {
            notificationManagerCompat.notify(id, notification);
        } else {
            notificationManagerCompat.notify(tag, id, notification);
        }
    }

    protected abstract void onShowNotification(
        @NonNull final NotificationCompat.Builder builder,
        @NonNull final TNotificationType type,
        @Nullable final Map<String, String> params);

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Getter(AccessLevel.PRIVATE)
    @NonNull
    private final NotificationCounter _notificationCounter;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final NotificationManagerCompat _notificationManagerCompat;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final UserSettingsHelper _userSettingsHelper;
}
