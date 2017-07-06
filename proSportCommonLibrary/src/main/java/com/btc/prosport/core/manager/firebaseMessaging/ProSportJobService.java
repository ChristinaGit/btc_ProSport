package com.btc.prosport.core.manager.firebaseMessaging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

import com.btc.prosport.di.ProSportApplicationComponentProvider;
import com.btc.prosport.di.application.ProSportApplicationComponent;
import com.firebase.jobdispatcher.SimpleJobService;

import javax.inject.Inject;

@Accessors(prefix = "_")
public abstract class ProSportJobService extends SimpleJobService {
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

    @Getter(AccessLevel.PROTECTED)
    @Inject
    @Nullable
    /*package-private*/ FirebaseMessagingManager _firebaseMessagingManager;
}
