package com.btc.prosport.core.manager.firebaseMessaging;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Action1;
import rx.functions.Func1;

import com.btc.common.contract.Contracts;
import com.btc.prosport.api.ProSportApi;
import com.btc.prosport.api.model.entity.NotificationSubscribeEntity;
import com.btc.prosport.api.request.NotificationsSubscribeParams;
import com.btc.prosport.core.manager.proSportServiceHelper.ProSportBaseAccountHelper;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;

// TODO: 05.05.2017 Test subscribe after reboot if no internet connection
@Accessors(prefix = "_")
public class ProSportMessagingManager implements FirebaseMessagingManager {
    private static final String _TYPE_ANDROID = "android";

    public ProSportMessagingManager(
        @NonNull final Context context,
        @NonNull final ProSportApi proSportApi,
        @NonNull final ProSportBaseAccountHelper proSportBaseAccountHelper) {
        Contracts.requireNonNull(context, "context == null");
        Contracts.requireNonNull(proSportApi, "proSportApi == null");
        Contracts.requireNonNull(proSportBaseAccountHelper, "proSportBaseAccountHelper == null");

        _context = context;
        _proSportApi = proSportApi;
        _proSportBaseAccountHelper = proSportBaseAccountHelper;
    }

    @NonNull
    @Override
    public Observable<Boolean> generalSubscribe(final boolean autoRetryOnError) {
        return subscribe(null).compose(new Transformer<Boolean, Boolean>() {
            @Override
            public Observable<Boolean> call(final Observable<Boolean> observable) {
                if (autoRetryOnError) {
                    val applyAutoRetry = ProSportMessagingManager.this.<Boolean>applyAutoRetry(
                        SubscribeJobService.class,
                        SubscribeJobService.TAG);
                    return observable.compose(applyAutoRetry);
                } else {
                    return observable;
                }
            }
        });
    }

    @NonNull
    @Override
    public Observable<Void> unsubscribe(
        final boolean autoRetryOnError, @Nullable final String firebaseRegistrationId) {

        final String registrationId;
        if (firebaseRegistrationId == null) {
            registrationId = FirebaseInstanceId.getInstance().getToken();
        } else {
            registrationId = firebaseRegistrationId;
        }

        if (registrationId != null) {
            return getProSportApi()
                .unsubscribeNotifications(registrationId)
                .map(new Func1<ResponseBody, Void>() {
                    @Override
                    public Void call(final ResponseBody responseBody) {
                        return null;
                    }
                })
                .compose(new Transformer<Void, Void>() {
                    @Override
                    public Observable<Void> call(final Observable<Void> observable) {
                        if (autoRetryOnError) {
                            val applyAutoRetry = ProSportMessagingManager.this.<Void>applyAutoRetry(
                                UnsubscribeJobService.class,
                                UnsubscribeJobService.TAG);
                            return observable.compose(applyAutoRetry);
                        } else {
                            return observable;
                        }
                    }
                });
        } else {
            return Observable.error(new RuntimeException("Registration id not found"));
        }
    }

    @NonNull
    @Override
    public Observable<Boolean> userSubscribe(
        final boolean autoRetryOnError) {
        return getProSportBaseAccountHelper()
            .withAccessToken(new Func1<String, Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call(final String token) {
                    return subscribe(token);
                }
            })
            .compose(new Transformer<Boolean, Boolean>() {
                @Override
                public Observable<Boolean> call(final Observable<Boolean> observable) {
                    if (autoRetryOnError) {
                        val applyAutoRetry = ProSportMessagingManager.this.<Boolean>applyAutoRetry(
                            SubscribeJobService.class,
                            SubscribeJobService.TAG);
                        return observable.compose(applyAutoRetry);
                    } else {
                        return observable;
                    }
                }
            });
    }

    protected Observable<Boolean> subscribe(@Nullable final String accessToken) {
        val registrationId = FirebaseInstanceId.getInstance().getToken();
        if (registrationId != null) {
            val proSportApi = getProSportApi();
            return proSportApi
                .getNotificationsSubscribe(registrationId)
                .flatMap(new Func1<NotificationSubscribeEntity,
                    Observable<NotificationSubscribeEntity>>() {
                    @Override
                    public Observable<NotificationSubscribeEntity> call(
                        final NotificationSubscribeEntity entity) {

                        val entityRegistrationId = entity.getRegistrationId();
                        return unsubscribe(false,
                                           entityRegistrationId).flatMap(new Func1<Void,
                            Observable<NotificationSubscribeEntity>>() {
                            @Override
                            public Observable<NotificationSubscribeEntity> call(final Void aVoid) {
                                val params = new NotificationsSubscribeParams();
                                params.setActive(true);
                                params.setRegistrationId(entityRegistrationId);
                                params.setType(_TYPE_ANDROID);
                                return proSportApi.subscribeNotifications(accessToken, params);
                            }
                        });
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<NotificationSubscribeEntity>>() {
                    @Override
                    public Observable<NotificationSubscribeEntity> call(
                        final Throwable throwable) {
                        if (throwable instanceof HttpException &&
                            ((HttpException) throwable).code() ==
                            HttpURLConnection.HTTP_NOT_FOUND) {
                            val params = new NotificationsSubscribeParams();
                            params.setActive(true);
                            params.setRegistrationId(registrationId);
                            params.setType(_TYPE_ANDROID);
                            return proSportApi.subscribeNotifications(accessToken, params);
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                })
                .map(new Func1<NotificationSubscribeEntity, Boolean>() {
                    @Override
                    public Boolean call(
                        final NotificationSubscribeEntity notificationSubscribeEntity) {
                        return notificationSubscribeEntity.isActive();
                    }
                });
        } else {
            // TODO: Create custom exceptions.
            return Observable.error(new RuntimeException("Registration id not found"));
        }
    }

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final Context _context;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportApi _proSportApi;

    @Getter(AccessLevel.PROTECTED)
    @NonNull
    private final ProSportBaseAccountHelper _proSportBaseAccountHelper;

    @NonNull
    private <T> Transformer<T, T> applyAutoRetry(
        @NonNull final Class<? extends ProSportJobService> jobService, @NonNull final String tag) {
        Contracts.requireNonNull(jobService, "jobService == null");
        Contracts.requireNonNull(tag, "tag == null");

        return new Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> observable) {
                return observable.doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        val firebaseJobDispatcher =
                            new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));
                        val job = firebaseJobDispatcher
                            .newJobBuilder()
                            .setTag(tag)
                            .setService(jobService)
                            .setTrigger(Trigger.executionWindow(30, 60))
                            .setConstraints(Constraint.ON_ANY_NETWORK)
                            .setLifetime(Lifetime.FOREVER)
                            .setReplaceCurrent(true)
                            .build();
                        firebaseJobDispatcher.mustSchedule(job);
                    }
                });
            }
        };
    }
}
