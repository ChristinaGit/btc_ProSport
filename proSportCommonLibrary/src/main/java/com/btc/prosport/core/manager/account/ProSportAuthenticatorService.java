package com.btc.prosport.core.manager.account;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.prosport.core.manager.credential.CredentialManager;
import com.btc.prosport.core.manager.firebaseMessaging.FirebaseMessagingManager;
import com.btc.prosport.core.manager.notification.NotificationType;
import com.btc.prosport.core.manager.notification.ProSportNotificationManager;
import com.btc.prosport.core.manager.proSportApi.ProSportApiManager;

import javax.inject.Inject;

@Accessors(prefix = "_")
public class ProSportAuthenticatorService extends BaseProSportAuthenticatorService {
    @CallSuper
    @Override
    public IBinder onBind(final Intent intent) {
        IBinder binder = null;
        if (_authenticator != null) {
            binder = _authenticator.getIBinder();
        }
        return binder;
    }

    @CallSuper
    @Override
    protected void onInjectMembers() {
        super.onInjectMembers();

        getProSportApplicationComponent().inject(this);

        val proSportApiManger = getProSportApiManger();
        val credentialManager = getCredentialManager();
        val firebaseMessagingManager = getFirebaseMessagingManager();
        val proSportNotificationManager = getProSportNotificationManager();

        if (proSportApiManger != null && credentialManager != null &&
            firebaseMessagingManager != null && proSportNotificationManager != null) {
            _authenticator = new ProSportAccountAuthenticator(
                getApplicationContext(),
                proSportApiManger.getProSportApi(),
                credentialManager,
                firebaseMessagingManager,
                proSportNotificationManager);
        }
    }

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ CredentialManager _credentialManager;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ FirebaseMessagingManager _firebaseMessagingManager;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ ProSportApiManager _proSportApiManger;

    @Inject
    @Getter(AccessLevel.PROTECTED)
    @Nullable
    /*package-private*/ ProSportNotificationManager<NotificationType> _proSportNotificationManager;

    @Nullable
    private ProSportAccountAuthenticator _authenticator;
}