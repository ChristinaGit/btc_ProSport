package com.btc.prosport.manager.core.manager.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.core.manager.notification.ApplicationProSportNotificationManager;
import com.btc.prosport.core.manager.notification.NotificationEventBroadcastReceiver;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.manager.R;
import com.btc.prosport.manager.core.OrderStatusFilter;
import com.btc.prosport.manager.screen.activity.workspace.WorkspaceIntent;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

@Accessors(prefix = "_")
public class ManagerProSportNotificationManager
    extends ApplicationProSportNotificationManager<ManagerNotificationType> {
    private static final String _LOG_TAG =
        ConstantBuilder.logTag(ManagerProSportNotificationManager.class);

    private static final String _TAG = ManagerProSportNotificationManager.class.getName();

    private static final String _SERVER_TYPE_ORDER_CREATED = "order_created";

    private static final String KEY_DATA = "data";

    private static final String KEY_EVENT = "event";

    public ManagerProSportNotificationManager(
        @NonNull final Context context, @NonNull final UserSettingsHelper userSettingsHelper) {
        super(Contracts.requireNonNull(context, "context == null"),
              _TAG,
              Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null"));

        _gson = new Gson();
    }

    @Nullable
    @Override
    public ManagerNotificationType getNotificationType(@NonNull final Map<String, String> params) {
        Contracts.requireNonNull(params, "params == null");

        final ManagerNotificationType type;

        val serverType = params.get(KEY_EVENT);

        switch (serverType) {
            case _SERVER_TYPE_ORDER_CREATED:
                type = ManagerNotificationType.ORDER_CREATED;
                break;
            default:
                type = null;
                break;
        }

        return type;
    }

    @Override
    public ManagerNotificationType[] getNotificationTypes() {
        return ManagerNotificationType.values();
    }

    protected void onBuildOrderCreatedNotification(
        @NonNull final NotificationCompat.Builder builder, @NonNull final Order order,
        final int count) {
        Contracts.requireNonNull(builder, "builder == null");
        Contracts.requireNonNull(order, "order == null");

        val context = getContext();

        final String title;
        if (count == 1) {
            title = ProSportFormat.getFormattedOrderPlace(order);

            val contentBuilder = new StringBuilder();
            contentBuilder
                .append(context.getString(R.string.notification_new_order_action))
                .append(" ");

            val intervals = (List<OrderMetadataInterval>) order.getIntervals();
            if (intervals != null) {
                val dates = ProSportFormat.getFormattedOrderIntervals(context, order, ", ");

                if (!dates.isEmpty()) {
                    contentBuilder.append(dates);
                    contentBuilder.append(".");
                }
            }

            builder
                .setContentText(contentBuilder.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentBuilder));
        } else {
            title = context
                .getResources()
                .getQuantityString(R.plurals.notification_order_created_title, count, count);
        }

        builder.setContentTitle(title);

        val playground = order.getPlayground();
        if (playground != null) {
            val viewIntent = WorkspaceIntent
                .builder(getContext())
                .setOrderStateFilter(OrderStatusFilter.NOT_CONFIRMED)
                .setTabId(WorkspaceIntent.TAB_ID_ORDERS)
                .build();
            viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            builder.setContentIntent(PendingIntent.getActivity(context,
                                                               ManagerNotificationType.ORDER_CREATED
                                                                   .getId(),
                                                               viewIntent,
                                                               PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    @Override
    protected void onShowNotification(
        @NonNull final NotificationCompat.Builder builder,
        @NonNull final ManagerNotificationType type,
        @Nullable final Map<String, String> params) {

        val context = getContext();

        builder.setSmallIcon(R.drawable.ic_statusbar);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                                          R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        switch (type) {
            case ORDER_CREATED:
                if (params != null) {
                    val jsonOrder = params.get(KEY_DATA);
                    // TODO: 22.05.2017 Add abstraction for parsing
                    val order = (Order) getGson().fromJson(jsonOrder, OrderEntity.class);

                    if (order == null) {
                        return;
                    }

                    val currentNotifications = getCurrentNotifications(type, null);
                    final int count;
                    if (currentNotifications != null) {
                        count = currentNotifications.size();
                    } else {
                        count = 0;
                    }

                    onBuildOrderCreatedNotification(builder, order, count + 1);

                    val notificationRemovedIntent =
                        NotificationEventBroadcastReceiver.getNotificationDeleteIntent(context,
                                                                                       type.getId(),
                                                                                       null);

                    builder.setDeleteIntent(PendingIntent.getBroadcast(context,
                                                                       type.getId(),
                                                                       notificationRemovedIntent,
                                                                       PendingIntent
                                                                           .FLAG_UPDATE_CURRENT));

                    saveNotification(type, null, jsonOrder);

                    showNotification(null, type.getId(), builder.build());
                }
                break;
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Gson _gson;
}
