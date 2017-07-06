package com.btc.prosport.core.manager.firebaseMessaging;

import android.support.annotation.NonNull;

import rx.Observable;

public interface FirebaseMessagingManager {
    @NonNull
    Observable<Boolean> generalSubscribe(final boolean autoRetryOnError);

    @NonNull
    Observable<Void> unsubscribe(
        final boolean autoRetryOnError, @NonNull final String firebaseRegistrationId);

    @NonNull
    Observable<Boolean> userSubscribe(final boolean autoRetryOnError);
}
