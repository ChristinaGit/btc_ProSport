package com.btc.prosport.core.manager.firebaseMessaging;

import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.common.contract.Contracts;
import com.btc.common.utility.HandlerUtils;
import com.btc.prosport.api.model.entity.PlaygroundReportEntity;
import com.btc.prosport.core.manager.notification.NotificationType;
import com.btc.prosport.core.manager.notification.ProSportNotificationManager;
import com.btc.prosport.core.manager.notificationEvent.NotificationEvent;
import com.btc.prosport.core.manager.notificationEvent.ProSportInternalNotificationEventManger;
import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import javax.inject.Inject;

@Accessors(prefix = "_")
public class ProSportMessagingService extends FirebaseMessagingService {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ProSportMessagingService.class);

    private static final String _FIELD_EVENT_NAME = "event";

    private static final String _FIELD_EVENT_DATA = "data";

    @NonNull
    public final ProSportApplicationComponent getProSportApplicationComponent() {
        final val application = getApplication();
        if (application instanceof ProSportApplicationComponentProvider) {
            final val componentProvider = (ProSportApplicationComponentProvider) application;
            return componentProvider.getProSportApplicationComponent();
        } else {
            throw new IllegalStateException("The application must implement " +
                                            ProSportApplicationComponentProvider.class.getName());
        }
    }

    @Nullable
    public <TMessage> TMessage getNotificationEventMessage(
        @Nullable final String messageData, @NonNull final Class<TMessage> messageType) {
        Contracts.requireNonNull(messageType, "messageType == null");

        final TMessage message;

        final val messageParser = getMessageParser();
        if (messageParser != null) {
            message = messageParser.fromJson(messageData, messageType);
        } else {
            message = null;
        }

        return message;
    }

    @CallSuper
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        getProSportApplicationComponent().inject(this);

        final val notificationManager = getNotificationManager();
        if (notificationManager != null) {
            final val data = remoteMessage.getData();

            if (data != null) {
                final val eventName = data.get(_FIELD_EVENT_NAME);
                final val notificationEvent = NotificationEvent.byEventName(eventName);
                if (notificationEvent == null) {
                    Log.w(_LOG_TAG, "Unknown notification: " + eventName);
                } else {
                    switch (notificationEvent) {
                        case ORDER_CONFIRMED:
                        case ORDER_CANCELLED:
                        case ORDER_TIME_CHANGED:
                        case ORDER_NOT_CONFIRMED:
                        case DISCOUNT:
                        case ORDER_CREATED: {
                            final val notificationType =
                                notificationManager.getNotificationType(data);
                            if (notificationType != null) {
                                notificationManager.showNotification(notificationType, data);
                            }
                            break;
                        }
                        case NOT_CONFIRMED_ORDERS_CHANGED: {
                            final val eventData = data.get(_FIELD_EVENT_DATA);

                            final val changedPlayground = getNotificationEventMessage(eventData,
                                                                                      PlaygroundReportEntity.class);

                            final val postMessage = new Runnable() {
                                @Override
                                public void run() {
                                    final val notificationEventManger =
                                        getNotificationEventManger();
                                    if (notificationEventManger != null &&
                                        changedPlayground != null) {
                                        notificationEventManger
                                            .postNotConfirmedOrdersChangedMessage(
                                            changedPlayground);
                                    }
                                }
                            };
                            getMainHandler().post(postMessage);

                            break;
                        }
                    }
                }
            }
        }
    }

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ Gson _messageParser;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ ProSportInternalNotificationEventManger _notificationEventManger;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ ProSportNotificationManager<NotificationType> _notificationManager;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Handler _mainHandler = HandlerUtils.getMainThreadHandler();
}
