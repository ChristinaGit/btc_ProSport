package com.btc.prosport.core.manager.firebaseMessaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

@Accessors(prefix = "_")
public class ProSportInstanceIdService extends FirebaseInstanceIdService {
    private static final String _LOG_TAG = ConstantBuilder.logTag(ProSportInstanceIdService.class);

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

    @Override
    public void onCreate() {
        super.onCreate();

        getProSportApplicationComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        val firebaseMessagingManager = getFirebaseMessagingManager();
        if (firebaseMessagingManager != null) {
            try {
                firebaseMessagingManager.userSubscribe(true).toBlocking().subscribe();
            } catch (final Exception error) {
                Log.w(_LOG_TAG, "Fail to subscribe on push notifications.", error);
            }
        }
    }

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ FirebaseMessagingManager _firebaseMessagingManager;
}
