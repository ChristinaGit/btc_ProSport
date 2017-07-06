package com.btc.prosport.player.core.manager.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v7.app.NotificationCompat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.model.Order;
import com.btc.prosport.api.model.OrderMetadataInterval;
import com.btc.prosport.api.model.Sale;
import com.btc.prosport.api.model.entity.OrderEntity;
import com.btc.prosport.api.model.entity.SaleEntity;
import com.btc.prosport.core.manager.notification.ApplicationProSportNotificationManager;
import com.btc.prosport.core.manager.notification.NotificationEventBroadcastReceiver;
import com.btc.prosport.core.manager.proSportServiceHelper.UserSettingsHelper;
import com.btc.prosport.core.utility.ProSportFormat;
import com.btc.prosport.player.R;
import com.btc.prosport.player.core.SportTypeContext;
import com.btc.prosport.player.screen.activity.ordersViewer.OrdersViewerActivity;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

@Accessors(prefix = "_")
public class PlayerProSportNotificationManager
    extends ApplicationProSportNotificationManager<PlayerNotificationType> {
    private static final String _TAG = PlayerProSportNotificationManager.class.getName();

    private static final String _SERVER_TYPE_ORDER_CONFIRMED = "order_confirmed";

    private static final String _SERVER_TYPE_ORDER_CANCELLED = "order_cancelled";

    private static final String _SERVER_TYPE_ORDER_TIME_CHANGED = "order_time_changed";

    private static final String _SERVER_TYPE_ORDER_NOT_CONFIRMED = "order_not_confirmed";

    private static final String _SERVER_TYPE_DISCOUNT = "discount";

    private static final String KEY_DATA = "data";

    private static final String KEY_EVENT = "event";

    public PlayerProSportNotificationManager(
        @NonNull final Context context,
        @NonNull final UserSettingsHelper userSettingsHelper,
        @NonNull final SportTypeContext sportTypeContext) {
        super(Contracts.requireNonNull(context, "context == null"),
              _TAG,
              Contracts.requireNonNull(userSettingsHelper, "userSettingsHelper == null"));
        Contracts.requireNonNull(sportTypeContext, "sportTypeContext == null");

        _gson = new Gson();
        _sportTypeContext = sportTypeContext;
    }

    @Nullable
    @Override
    public PlayerNotificationType getNotificationType(@NonNull final Map<String, String> params) {
        Contracts.requireNonNull(params, "params == null");

        final PlayerNotificationType notificationType;

        val serverType = params.get(KEY_EVENT);

        switch (serverType) {
            case _SERVER_TYPE_ORDER_CONFIRMED:
                notificationType = PlayerNotificationType.ORDER_CONFIRMED;
                break;
            case _SERVER_TYPE_ORDER_CANCELLED:
                notificationType = PlayerNotificationType.ORDER_CANCELLED;
                break;
            //            case _SERVER_TYPE_ORDER_TIME_CHANGED:
            //                notificationType = PlayerNotificationType.ORDER_TIME_CHANGED;
            //                break;
            //            case _SERVER_TYPE_ORDER_NOT_CONFIRMED:
            //                notificationType = PlayerNotificationType.ORDER_NOT_CONFIRMED;
            //                break;
            //            case _SERVER_TYPE_DISCOUNT:
            //                notificationType = PlayerNotificationType.DISCOUNT;
            //                break;
            default:
                notificationType = null;
        }

        return notificationType;
    }

    @Override
    public PlayerNotificationType[] getNotificationTypes() {
        return PlayerNotificationType.values();
    }

    protected void onBuildConfirmedNotification(
        @NonNull final NotificationCompat.Builder builder, @NonNull final Order order,
        final int notificationCount,
        @NonNull final PlayerNotificationType type) {
        Contracts.requireNonNull(builder, "builder == null");
        Contracts.requireNonNull(order, "order == null");
        Contracts.requireNonNull(type, "type == null");

        onBuildOrderNotification(builder,
                                 order,
                                 notificationCount,
                                 R.plurals.notification_order_confirmed_title,
                                 R.string.notification_order_confirmed_action,
                                 type);
    }

    protected void onBuildOrderCanceledNotification(
        @NonNull final NotificationCompat.Builder builder, @NonNull final Order order,
        final int notificationCount,
        @NonNull final PlayerNotificationType type) {
        Contracts.requireNonNull(builder, "builder == null");
        Contracts.requireNonNull(order, "order == null");

        onBuildOrderNotification(builder,
                                 order,
                                 notificationCount,
                                 R.plurals.notification_order_cancelled_title,
                                 R.string.notification_order_cancelled_action,
                                 type);
    }

    protected void onBuildOrderNotification(
        @NonNull final NotificationCompat.Builder builder, @NonNull final Order order,
        final int count,
        @PluralsRes final int moreTitle,
        @StringRes final int contentAction,
        @NonNull final PlayerNotificationType type) {
        Contracts.requireNonNull(builder, "builder == null");
        Contracts.requireNonNull(order, "order == null");
        Contracts.requireNonNull(type, "type == null");

        val context = getContext();

        final String title;
        if (count == 1) {
            title = ProSportFormat.getFormattedOrderPlace(order);

            val contentBuilder = new StringBuilder();

            //noinspection unchecked
            val intervals = (List<OrderMetadataInterval>) order.getIntervals();
            if (intervals != null) {
                val dates = ProSportFormat.getFormattedOrderIntervals(context, order, ", ");

                if (!dates.isEmpty()) {
                    contentBuilder.append(dates);
                    contentBuilder.append(". ");
                }
            }

            contentBuilder.append(context.getString(contentAction)).append(" ");

            builder
                .setContentText(contentBuilder.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentBuilder));
        } else {
            title = context.getResources().getQuantityString(moreTitle, count, count);
        }

        builder.setContentTitle(title);

        val playground = order.getPlayground();
        if (playground != null) {
            val viewIntent = OrdersViewerActivity.getIntent(context);
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            builder.setContentIntent(PendingIntent.getActivity(context,
                                                               type.getId(),
                                                               viewIntent,
                                                               PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    @Override
    protected void onShowNotification(
        @NonNull final NotificationCompat.Builder builder,
        @NonNull final PlayerNotificationType type,
        @Nullable final Map<String, String> params) {
        Contracts.requireNonNull(builder, "builder == null");
        Contracts.requireNonNull(type, "type == null");

        val context = getContext();

        builder.setSmallIcon(_sportTypeContext.getNotificationSmallIconId());
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                                          _sportTypeContext
                                                              .getNotificationLargeIconId()));
        builder.setAutoCancel(true);

        final String jsonData;
        if (params != null) {
            jsonData = params.get(KEY_DATA);
        } else {
            jsonData = null;
        }

        if (jsonData == null) {
            return;
        }

        final String tag;

        switch (type) {
            case ORDER_CONFIRMED: {
                val order = (Order) getGson().fromJson(jsonData, OrderEntity.class);

                if (order == null) {
                    return;
                }

                tag = null;

                val currentNotifications = getCurrentNotifications(type, null);

                final int size = currentNotifications != null ? currentNotifications.size() : 0;

                onBuildConfirmedNotification(builder, order, size + 1, type);

                saveNotification(type, null, jsonData);
                break;
            }
            case ORDER_CANCELLED: {
                val order = (Order) getGson().fromJson(jsonData, OrderEntity.class);

                if (order == null) {
                    return;
                }

                tag = null;

                val currentNotifications = getCurrentNotifications(type, null);

                final int size = currentNotifications != null ? currentNotifications.size() : 0;

                onBuildOrderCanceledNotification(builder, order, size + 1, type);

                saveNotification(type, null, jsonData);
                break;
            }
            case ORDER_TIME_CHANGED: {
                tag = null;

                break;
            }
            case ORDER_NOT_CONFIRMED: {
                tag = null;

                break;
            }
            case DISCOUNT: {
                val sale = (Sale) getGson().fromJson(jsonData, SaleEntity.class);

                tag = null;

                break;
            }
            default:
                tag = null;

                break;
        }

        val notificationDeleteIntent =
            NotificationEventBroadcastReceiver.getNotificationDeleteIntent(context,
                                                                           type.getId(),
                                                                           tag);

        builder.setDeleteIntent(PendingIntent.getBroadcast(context,
                                                           type.getId(),
                                                           notificationDeleteIntent,
                                                           PendingIntent.FLAG_UPDATE_CURRENT));

        showNotification(tag, type.getId(), builder.build());
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Gson _gson;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final SportTypeContext _sportTypeContext;
}
