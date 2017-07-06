package com.btc.prosport.core.manager.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;

import javax.inject.Inject;

@Accessors(prefix = "_")
public class NotificationEventBroadcastReceiver extends BroadcastReceiver {
    private static final String _ACTION_NOTIFICATION_REMOVED =
        "com.btc.prosport.common.NOTIFICATION_REMOVED";

    private static final String _EXTRA_NOTIFICATION_ID = "notificationType";

    private static final String _EXTRA_NOTIFICATION_TAG = "notificationTag";

    public static Intent getNotificationDeleteIntent(
        @NonNull final Context context,
        final int notificationId,
        @Nullable final String notificationTag) {
        Contracts.requireNonNull(context, "context == null");

        val intent = new Intent(context, NotificationEventBroadcastReceiver.class);
        intent.setAction(_ACTION_NOTIFICATION_REMOVED);
        intent.putExtra(_EXTRA_NOTIFICATION_ID, notificationId);
        if (notificationTag != null) {
            intent.putExtra(_EXTRA_NOTIFICATION_TAG, notificationTag);
        }
        return intent;
    }

    @NonNull
    public final ProSportApplicationComponent getProSportApplicationComponent(
        @NonNull final Context context) {
        Contracts.requireNonNull(context, "context == null");

        final val applicationContext = context.getApplicationContext();
        if (applicationContext instanceof ProSportApplicationComponentProvider) {
            final val componentProvider = (ProSportApplicationComponentProvider) applicationContext;
            return componentProvider.getProSportApplicationComponent();
        } else {
            throw new IllegalStateException("The application context must implement " +
                                            ProSportApplicationComponentProvider.class.getName());
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        val proSportApplicationComponent = getProSportApplicationComponent(context);
        proSportApplicationComponent.inject(this);

        val action = intent.getAction();
        if (action != null && _ACTION_NOTIFICATION_REMOVED.equals(action)) {
            val proSportNotificationManager = getProSportNotificationManager();
            if (proSportNotificationManager != null) {
                val extras = intent.getExtras();
                if (extras != null) {
                    if (extras.containsKey(_EXTRA_NOTIFICATION_ID)) {
                        final int notificationId = extras.getInt(_EXTRA_NOTIFICATION_ID);
                        val notificationTag = extras.getString(_EXTRA_NOTIFICATION_TAG);
                        proSportNotificationManager.cancelNotification(notificationTag,
                                                                       notificationId);
                    }
                }
            }
        }
    }

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ ProSportNotificationManager<NotificationType> _proSportNotificationManager;
}
